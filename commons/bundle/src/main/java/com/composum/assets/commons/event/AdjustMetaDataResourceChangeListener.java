package com.composum.assets.commons.event;

import com.composum.assets.commons.service.MetaPropertiesService;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChange.ChangeType;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.jcr.api.SlingRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Calls the {@link MetaPropertiesService#adjustMetaProperties(ResourceResolver, Resource)}
 * on file changes in /content. For other paths (e.g. transient renditions) the service has
 * to be called explicitly.
 */
@Component(
        immediate = true,
        service = ResourceChangeListener.class,
        property = {
                ResourceChangeListener.PATHS + "=" + "/content",
                ResourceChangeListener.CHANGES + "=ADDED", // ChangeType.ADDED
                ResourceChangeListener.CHANGES + "=CHANGED", // ChangeType.CHANGED,
                // ResourceChangeListener.PROPERTY_NAMES_HINT + "=" + ResourceUtil.PROP_DATA
        }
)
public class AdjustMetaDataResourceChangeListener implements ResourceChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(AdjustMetaDataResourceChangeListener.class);

    /** Filter resources again since there is no guarantee that they really match the registered paths. */
    protected static final StringFilter PATH_FILTER = new StringFilter.WhiteList("^/content/.*");

    /** Matches both the node containing an asset as well as it's jcr:content since both can appear in an event. */
    protected static final ResourceFilter RESOURCE_FILTER =
            new ResourceFilter.NodeTypeFilter(new StringFilter.WhiteList(ResourceUtil.NT_FILE,
                    ResourceUtil.NT_RESOURCE, ResourceUtil.TYPE_SLING_RESOURCE));

    @Reference
    protected SlingRepository repository;

    @Reference
    protected ResourceResolverFactory resolverFactory;

    @Reference
    protected MetaPropertiesService metaPropertiesService;

    /** Fetches service resolver - do not forget to close! */
    @Nonnull
    protected ResourceResolver getResolver() {
        try {
            return resolverFactory.getServiceResourceResolver(null);
        } catch (LoginException e) {
            LOG.error("Cannot get service resolver: " + e, e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Doublechecks that the event is for us, since that's not guaranteed.
     * We also check that the change doesn't come from our service user but is an external change
     * to avoid endless loops triggered by our changes.
     */
    protected boolean acceptChange(@Nonnull ResourceChange change, @Nullable String serviceResolverUserId) {
        return !change.isExternal() &&
                (change.getType() == ChangeType.ADDED || change.getType() == ChangeType.CHANGED) &&
                !StringUtils.equals(serviceResolverUserId, change.getUserId()) &&
                PATH_FILTER.accept(change.getPath());
        // deliberately not check userid since we want to work on transient renderings, too.
    }

    /** Checks that the resource is of the appropriate kind. */
    protected boolean acceptResource(Resource resource) {
        return RESOURCE_FILTER.accept(resource);
    }


    /**
     * Feeds all relevant changes through
     * {@link MetaPropertiesService#adjustMetaProperties(ResourceResolver, Resource)}.
     */
    @Override
    public void onChange(@NotNull List<ResourceChange> changes) {
        try (ResourceResolver resolver = getResolver()) {

            Set<String> paths = new LinkedHashSet<>();
            List<ResourceChange> ignoredChanges = new ArrayList<>();
            List<String> ignoredPaths = new ArrayList<>();

            for (ResourceChange change : changes) {
                if (acceptChange(change, resolver.getUserID())) {
                    paths.add(change.getPath());
                } else {
                    ignoredChanges.add(change);
                }
            }

            if (!paths.isEmpty()) {
                LOG.debug("Processing changed paths {}", paths);
                for (String path : paths) {
                    Resource resource = resolver.getResource(path);
                    if (acceptResource(resource)) {
                        try {
                            metaPropertiesService.adjustMetaProperties(resolver, resource);
                            resolver.commit();
                        } catch (PersistenceException e) {
                            LOG.error("Error updating " + path, e);
                        }
                    } else if (resource == null) { // might be some kind of race condition as well, though.
                        LOG.warn("Can't find resource {} - are ACLs for service user {} correct?",
                                path, resolver.getUserID());
                    } else {
                        ignoredPaths.add(path);
                    }
                }
            }

            if (!ignoredChanges.isEmpty() || !ignoredPaths.isEmpty()) {
                LOG.debug("Ignoring changes {} {}", ignoredChanges, ignoredPaths);
            }

        } catch (RuntimeException e) {
            LOG.error("Exception when processing events: " + e, e);
        }
    }

}
