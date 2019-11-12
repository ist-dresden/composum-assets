package com.composum.assets.commons.service;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.AssetVariation;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Periodically cleans up transient renditions.
 *
 * @see "https://sling.apache.org/documentation/bundles/scheduler-service-commons-scheduler.html"
 * @see "http://www.docjar.com/docs/api/org/quartz/CronTrigger.html"
 */
@Component(
        service = Runnable.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Assets Cleanup Service",
                Scheduler.PROPERTY_SCHEDULER_CONCURRENT + ":Boolean=false",
                // for testing: Scheduler.PROPERTY_SCHEDULER_PERIOD + ":Long=60",
                Scheduler.PROPERTY_SCHEDULER_RUN_ON + "=" + Scheduler.VALUE_RUN_ON_SINGLE
        },
        immediate = true
)
@Designate(ocd = TransientRenditionsCleanupService.Configuration.class)
public class TransientRenditionsCleanupService implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TransientRenditionsCleanupService.class);

    /** If a rendition is that old but still not valid, we assume the rendering process was broken and remove it. */
    protected static final long MINAGEBROKEN = TimeUnit.HOURS.toMillis(3);

    @Reference
    protected ResourceResolverFactory resolverFactory;

    protected Configuration config;

    @Override
    public void run() {
        LOG.info("TransientRenditionsCleanupService.run start");
        if (config != null && config.enabled()) {
            try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(null)) {
                removeObsoleteAssets(resolver);
                resolver.commit();
                removeObsoleteFolders(resolver.getResource(AssetsConstants.PATH_TRANSIENTS));
                resolver.commit();
            } catch (LoginException | RuntimeException | PersistenceException e) {
                LOG.error("" + e, e);
            }
        }
        LOG.info("TransientRenditionsCleanupService.run stop");
    }

    protected void removeObsoleteAssets(ResourceResolver resolver) throws PersistenceException {
        StringBuilder buf = new StringBuilder();
        QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);
        Query query = queryBuilder.createQuery().path(AssetsConstants.PATH_TRANSIENTS).type(AssetsConstants.NODE_TYPE_RENDITION);
        long cutTime = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(this.config.keepTime());
        BeanContext beanContext = new BeanContext.Service(resolver);
        try {
            for (Resource renditionResource : query.execute()) {
                if (renditionExpired(beanContext, renditionResource, cutTime)) {
                    try {
                        resolver.delete(renditionResource);
                        buf.append("\n    ").append(renditionResource.getPath());
                    } catch (PersistenceException e) {
                        LOG.error("Could not delete " + renditionResource.getPath(), e);
                    }
                }
            }
        } finally {
            resolver.commit();
            LOG.info("Expired assets deleted: {}", buf);
        }
    }

    protected boolean renditionExpired(BeanContext beanContext, @Nonnull Resource renditionResource, long cutTime) {
        ValueMap valueMap = renditionResource.getValueMap();
        if (isPermanentPath(renditionResource.getPath())) {
            String assetPath = valueMap.get(AssetsConstants.PROP_ASSETPATH, String.class);
            String variationName = valueMap.get(AssetsConstants.PROP_VARIATIONNAME, String.class);
            Resource assetResource = assetPath != null ? beanContext.getResolver().getResource(assetPath) : null;
            ImageAsset asset = assetResource != null ? new ImageAsset(beanContext, assetResource) : null;
            AssetVariation variation = asset != null ? asset.getVariation(variationName) : null;
            AssetRendition rendition = variation != null ? variation.getRendition(renditionResource.getName()) : null;
            String transientPath = rendition != null ? rendition.getTransientsPath() : null;

            boolean pathValid = StringUtils.equals(transientPath, renditionResource.getPath());
            boolean renditionBroken = false;
            if (pathValid && rendition != null && !rendition.isValid() && rendition.getLastModified() != null) {
                // if a rendition is not valid, it is probably being created in parallel right now.
                // But if it's old, anyway, something's wrong, and the creation should be done again.
                renditionBroken =
                        rendition.getLastModified().getTimeInMillis() < System.currentTimeMillis() - MINAGEBROKEN;
            }
            return !pathValid || renditionBroken;
            // we have expired=true also if the service user has no rights to the original, but then we wouldn't
            // normally have a rendition, anyway.
        } else {
            Calendar lastRendered = valueMap.get(AssetsConstants.PROP_LAST_RENDERED, Calendar.class);
            long lastRenderedTime = lastRendered != null ? lastRendered.getTimeInMillis() : Long.MIN_VALUE;
            return lastRenderedTime < cutTime;
        }
    }

    protected boolean isPermanentPath(String path) {
        for (String permanent : config.permanentPaths()) {
            if (path.startsWith(AssetsConstants.PATH_TRANSIENTS + permanent)) { return true; }
        }
        return false;
    }

    /**
     * Scans the resource tree below topresource for {@link AssetsConstants#NODE_TYPE_RENDITION}s and returns true
     * if there is one or false if there isn't. Subresources are removed if they do not contain a rendition;
     * subresources of a rendition are not checked.
     */
    protected boolean removeObsoleteFolders(Resource resource) throws PersistenceException {
        if (ResourceUtil.isPrimaryType(resource, AssetsConstants.NODE_TYPE_RENDITION)) { return true; }
        boolean containsRenditions = false;
        for (Resource child : resource.getChildren()) {
            boolean childHasRenditions = removeObsoleteFolders(child);
            if (!childHasRenditions) {
                resource.getResourceResolver().delete(child);
            }
            containsRenditions = containsRenditions || childHasRenditions;
        }
        return containsRenditions;
    }

    @Activate
    @Modified
    public void activate(Configuration configuration) {
        this.config = configuration;
        LOG.info("activated or modified");
    }

    @Deactivate
    public void deactivate() {
        this.config = null;
    }

    @ObjectClassDefinition(
            name = "Composum Assets Cleanup Service Configuration",
            description = "periodically removes obsolete transient renditions of assets"
    )
    public @interface Configuration {

        @AttributeDefinition(
                name = "Enabled",
                description = "Whether the service is enabled."
        )
        boolean enabled() default true;

        @AttributeDefinition(
                name = "Permanent Paths",
                description = "Paths for which the renditions have to be kept around as long as they could be needed " +
                        "- usually for live versions where re-rendering of the assets would disturb users." +
                        "These must be real paths (e.g. /public, /preview) in the repository (e.g. In-Place-" +
                        "Replication), not staged releases that are not replicated."
        )
        String[] permanentPaths() default {"/public", "/preview"};

        @AttributeDefinition(
                name = "Keep Time",
                description = "Time a transient rendering is kept around without being used, in seconds." +
                        "Default: 14 days (1209600)."
        )
        int keepTime() default 1209600;

        @AttributeDefinition(
                name = "Scheduler Expression",
                description = "Quartz expression when the cleanup process is triggered. E.g. 0 13 1 * * ? is 1:13 am."
        )
        String scheduler_expression() default "0 13 1 * * ?";
        // random time in the night to avoid running in parallel to other jobs.

    }

}
