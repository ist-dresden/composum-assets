/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.service;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.AssetVariation;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.image.BuilderContext;
import com.composum.assets.commons.image.RenditionBuilder;
import com.composum.assets.commons.image.RenditionTransformer;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.concurrent.LazyCreationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
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
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The Composum Assets - Image Service supports renditions of image assets".
 */
@Component(
        service = AdaptiveImageService.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Assets - Image Service: supports renditions of image assets",
        },
        immediate = true
)
@SuppressWarnings("deprecation")
@Designate(ocd = DefaultAdaptiveImageService.Configuration.class)
public class DefaultAdaptiveImageService implements AdaptiveImageService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAdaptiveImageService.class);

    @Reference
    protected ResourceResolverFactory resolverFactory;

    @Reference
    protected LazyCreationService lazyCreationService;

    @Reference
    protected MetaPropertiesService metaPropertiesService;

    protected ExecutorService executorService;

    @Reference
    protected RenditionTransformer renditionTransformer;

    protected Configuration config;

    @Activate
    @Modified
    protected void activate(@Nonnull ComponentContext context, @Nonnull Configuration configuration) {
        if (executorService != null) {
            executorService.shutdown();
        }
        executorService = Executors.newFixedThreadPool(configuration.threadPoolSize());
        config = configuration;
    }

    @Deactivate
    protected void deactivate() {
        executorService.shutdown();
        executorService = null;
        config = null;
    }

    @Override
    public RenditionTransformer getRenditionTransformer() {
        return renditionTransformer;
    }

    @Override
    @Nullable
    public AssetRendition getRendition(@Nonnull final ImageAsset asset,
                                       @Nonnull final String variationKey, @Nonnull final String renditionKey)
            throws RepositoryException {
        AssetRendition rendition = null;
        AssetVariation variation = asset.getVariation(variationKey);
        if (variation != null) {
            rendition = variation.getRendition(renditionKey);
        }
        if (null != rendition && rendition.isValid() &&
                (lazyCreationService.isInitialized(rendition.getResource()) || rendition.isOriginal())
        ) {
            updateLastRendered(rendition);
            return rendition;
        }
        return null;
    }

    @Override
    @Nullable
    public RenditionConfig getRenditionConfig(@Nonnull final ImageAsset asset,
                                              @Nonnull final String variationKey, @Nonnull final String renditionKey) {
        RenditionConfig renditionConfig = null;
        AssetConfig assetConfig = asset.getConfig();
        VariationConfig variationConfig = assetConfig.getVariation(variationKey);
        if (variationConfig != null) {
            renditionConfig = variationConfig.getRendition(renditionKey);
        }
        return renditionConfig;
    }

    @Override
    @Nullable
    public RenditionConfig findRenditionConfig(@Nonnull final ImageAsset asset,
                                               @Nonnull final String variationKey, @Nonnull final String renditionKey) {
        RenditionConfig renditionConfig = null;
        AssetConfig assetConfig = asset.getConfig();
        VariationConfig variationConfig = assetConfig.findVariation(variationKey);
        if (variationConfig != null) {
            renditionConfig = variationConfig.findRendition(renditionKey);
        }
        return renditionConfig;
    }

    @Override
    public AssetRendition getOrCreateRendition(@Nonnull final ImageAsset asset,
                                               @Nonnull final String variationKey, @Nonnull final String renditionKey)
            throws RepositoryException, IOException {

        AssetRendition rendition = getRendition(asset, variationKey, renditionKey);

        if (rendition == null) {
            RenditionConfig config = getRenditionConfig(asset, variationKey, renditionKey);
            if (config != null) {
                rendition = createRendition(asset, config);
            }
        }

        return rendition;
    }

    protected AssetRendition createRendition(ImageAsset asset, RenditionConfig renditionConfig)
            throws PersistenceException, RepositoryException {
        HashMap<String, Object> hints = new HashMap<>();
        BuilderContext context = new BuilderContext(this, lazyCreationService, metaPropertiesService, executorService, hints);
        RenditionBuilder builder = new RenditionBuilder(asset, renditionConfig, context);
        AssetRendition rendition = builder.getOrCreateRendition();
        LOG.debug("Rendition created: {}", rendition);
        return rendition;
    }

    protected void updateLastRendered(AssetRendition rendition) {
        if (!rendition.isTransient()) {
            return;
        }
        Calendar lastRendered = rendition.getProperty(AssetsConstants.PROP_LAST_RENDERED, Calendar.class);
        long lastRenderedTime = lastRendered != null ? lastRendered.getTimeInMillis() : Long.MIN_VALUE;
        if (lastRenderedTime < System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(config.lastRenderTimestep())) {
            try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(null)) {
                Resource updateResource = resolver.getResource(rendition.getPath());
                if (updateResource != null) {
                    ModifiableValueMap values = updateResource.adaptTo(ModifiableValueMap.class);
                    if (values != null) {
                        values.put(AssetsConstants.PROP_LAST_RENDERED, Calendar.getInstance());
                    } else {
                        LOG.error("can't modify '{}'", updateResource.getPath());
                    }
                } else { // that should not be possible, but somehow happens. Why?
                    LOG.error("Bug: Can't find resource in updateLastRendered: {}", rendition.getPath(),
                            new Exception("Stacktrace, not thrown"));
                }
                resolver.commit();
            } catch (LoginException | RuntimeException | PersistenceException e) {
                LOG.error("Trouble updating last rendering time for " + rendition.getPath(), e);
            }
        }
    }

    @Override
    public void dropRenditions(@Nonnull String path,
                               @Nonnull final String variationKey, @Nullable final String renditionKey)
            throws PersistenceException {

        path = path.trim();
        LOG.info("dropRenditions('{}','{}','{}')...", path, variationKey, renditionKey);

        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(null)) {

            PersistenceException fault = null;
            Iterator<Resource> iterator;
            StringBuilder query;
            String queryString;

            query = new StringBuilder("/jcr:root");
            if (!path.equals("/")) {
                query.append(path);
            }
            query.append("//");
            String queryBase = query.toString();

            query = new StringBuilder(queryBase);
            if (StringUtils.isNotBlank(variationKey)) {
                query.append(variationKey).append('/');
            }
            query.append("element(").append(StringUtils.isNotBlank(renditionKey) ? renditionKey : "*")
                    .append(",").append(AssetRendition.NODE_TYPE).append(")");
            queryString = query.toString();

            if (LOG.isDebugEnabled()) {
                LOG.debug("dropRenditions.query '{}'", queryString);
            }
            iterator = resolver.findResources(queryString, Query.XPATH);
            while (iterator.hasNext()) {
                ResourceHandle resource = ResourceHandle.use(iterator.next());
                try {
                    dropRendition(resolver, resource, variationKey);
                } catch (PersistenceException ex) {
                    // we drop all other stuff even if there was something broken.
                    LOG.error("Failure dropping rendition " + resource, ex);
                    fault = ex;
                } catch (RuntimeException ex) {
                    // we drop all other stuff even if there was something broken.
                    LOG.error("Failure dropping rendition " + resource, ex);
                    fault = new PersistenceException(ex.getMessage(), ex);
                }
            }

            query = new StringBuilder(queryBase);
            query.append("element(").append(StringUtils.isNotBlank(variationKey) ? variationKey : "*")
                    .append(",").append(AssetVariation.NODE_TYPE).append(")");
            queryString = query.toString();

            LOG.debug("dropVariations.query '{}'", queryString);
            iterator = resolver.findResources(queryString, Query.XPATH);
            while (iterator.hasNext()) {
                ResourceHandle resource = ResourceHandle.use(iterator.next());
                try {
                    dropVariation(resolver, resource);
                } catch (PersistenceException ex) {
                    LOG.error("Failure dropping variation " + resource, ex);
                    fault = ex;
                } catch (RuntimeException ex) {
                    LOG.error("Failure dropping variation " + resource, ex);
                    fault = new PersistenceException(ex.getMessage(), ex);
                }
            }

            resolver.commit();

            if (fault != null) {
                throw fault;
            }
        } catch (LoginException ex) {
            LOG.error(ex.getMessage());
            throw new PersistenceException(ex.getMessage());
        }
    }

    protected void dropRendition(ResourceResolver resolver, ResourceHandle rendition, String variationKey)
            throws PersistenceException {
        if (!isProtected(rendition)) {
            ResourceHandle variation = Objects.requireNonNull(rendition.getParent());
            if (StringUtils.isBlank(variationKey) || variationKey.equals(variation.getName())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("dropRendition({})...", rendition.getPath());
                }
                resolver.delete(rendition);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("keepRendition({}) - rendition is protected", rendition.getPath());
            }
        }
    }

    protected void dropVariation(ResourceResolver resolver, ResourceHandle variation)
            throws PersistenceException {
        if (!variation.listChildren().hasNext()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("dropVariation({})...", variation.getPath());
            }
            resolver.delete(variation);
        }
    }

    protected boolean isProtected(Resource resource) {
        ResourceHandle handle = ResourceHandle.use(resource);
        if (ConfigHandle.ORIGINAL.equals(handle.getName())) {
            return true;
        }
        String[] categories = handle.getProperty(ConfigHandle.CATEGORIES, new String[0]);
        for (String category : categories) {
            if (ConfigHandle.ORIGINAL.equals(category)) {
                return true;
            }
        }
        return false;
    }

    @ObjectClassDefinition(
            name = "Composum Assets - Image Service Configuration",
            description = "delivers renditions of image assets for the configured variations"
    )
    public @interface Configuration {

        @AttributeDefinition(
                name = "Thread Pool Size",
                description = "The maximum number of threads used to create image renditions."
        )
        int threadPoolSize() default 9;

        @AttributeDefinition(
                name = "Last Rendered Timelag",
                description = "Minimum timestep the last rendering time of an asset rendering is updated for " +
                        "performance reasons. On access, the last rendering time is only updated if it's this old in " +
                        "seconds. default: 1 day (86400)."
        )
        int lastRenderTimestep() default 86400;
    }

}
