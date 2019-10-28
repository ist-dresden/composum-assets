/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.service;

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
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component(
        label = "Composum Assets - Image Service",
        description = "supports renditions of image assets",
        immediate = true,
        metatype = true
)
@Service
@Properties({
        @Property(
                name = DefaultAdaptiveImageService.THREAD_POOL_SIZE,
                label = "Thread Pool Size",
                description = "the maximum number of threads used to create image renditions",
                intValue = DefaultAdaptiveImageService.THREAD_POOL_SIZE_DEFAULT),
})
@SuppressWarnings("deprecation")
public class DefaultAdaptiveImageService implements AdaptiveImageService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAdaptiveImageService.class);

    public static final String THREAD_POOL_SIZE = "thread.pool.size";

    public static final int THREAD_POOL_SIZE_DEFAULT = 9;

    @Reference
    protected ResourceResolverFactory resolverFactory;

    @Reference
    protected LazyCreationService lazyCreationService;

    protected ExecutorService executorService;

    @Reference
    protected RenditionTransformer renditionTransformer;

    @Activate
    protected void activate(ComponentContext context) throws Exception {
        Dictionary<String, Object> properties = context.getProperties();
        int threadPoolSize = PropertiesUtil.toInteger(properties.get(THREAD_POOL_SIZE), THREAD_POOL_SIZE_DEFAULT);
        executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    @Override
    public RenditionTransformer getRenditionTransformer() {
        return renditionTransformer;
    }

    @Override
    public AssetRendition getRendition(ImageAsset asset,
                                       String variationKey, String renditionKey) throws RepositoryException {
        AssetRendition rendition = null;
        AssetVariation variation = asset.getVariation(variationKey);
        if (variation != null) {
            rendition = variation.getRendition(renditionKey);
        }
        if (null != rendition && rendition.isValid() &&
                (lazyCreationService.isInitialized(rendition.getResource()) || rendition.isOriginal())
        ) {
            return rendition;
        }
        return null;
    }

    @Override
    public RenditionConfig getRenditionConfig(ImageAsset asset,
                                              String variationKey, String renditionKey) {
        RenditionConfig renditionConfig = null;
        AssetConfig assetConfig = asset.getConfig();
        VariationConfig variationConfig = assetConfig.getVariation(variationKey);
        if (variationConfig != null) {
            renditionConfig = variationConfig.getRendition(renditionKey);
        }
        return renditionConfig;
    }

    @Override
    public RenditionConfig findRenditionConfig(ImageAsset asset,
                                               String variationKey, String renditionKey) {
        RenditionConfig renditionConfig = null;
        AssetConfig assetConfig = asset.getConfig();
        VariationConfig variationConfig = assetConfig.findVariation(variationKey);
        if (variationConfig != null) {
            renditionConfig = variationConfig.findRendition(renditionKey);
        }
        return renditionConfig;
    }

    @Override
    public AssetRendition getOrCreateRendition(ImageAsset asset,
                                               String variationKey, String renditionKey)
            throws LoginException, RepositoryException, IOException {

        AssetRendition rendition = getRendition(asset, variationKey, renditionKey);

        if (rendition == null) {
            RenditionConfig config = getRenditionConfig(asset, variationKey, renditionKey);
            if (config != null) {
                rendition = createRendition(asset, config);
            }
        }

        return rendition;
    }

    protected AssetRendition createRendition(ImageAsset asset, RenditionConfig renditionConfig) throws
            PersistenceException,
            RepositoryException {
        HashMap<String, Object> hints = new HashMap<>();
        BuilderContext context = new BuilderContext(this, lazyCreationService, executorService, hints);
        RenditionBuilder builder = new RenditionBuilder(asset, renditionConfig, context);
        AssetRendition rendition = builder.getOrCreateRendition();
        LOG.debug("Rendition created: {}", rendition);
        return rendition;
    }

    @Override
    public void dropRenditions(String path,
                               String variationKey, String renditionKey)
            throws Exception {

        path = path.trim();
        if (LOG.isInfoEnabled()) {
            LOG.info("dropRenditions('" + path + "','" + variationKey + "','" + renditionKey + "')...");
        }

        try (ResourceResolver resolver = createServiceResolver()) {

            Exception fault = null;
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
                LOG.debug("dropRenditions.query '" + queryString + "'");
            }
            iterator = resolver.findResources(queryString, Query.XPATH);
            while (iterator.hasNext()) {
                ResourceHandle resource = ResourceHandle.use(iterator.next());
                try {
                    dropRendition(resolver, resource, variationKey);
                } catch (PersistenceException | RuntimeException ex) {
                    // we drop all other stuff even if there was something broken.
                    LOG.error("Failure dropping rendition" + resource, ex);
                    fault = ex;
                }
            }

            query = new StringBuilder(queryBase);
            query.append("element(").append(StringUtils.isNotBlank(variationKey) ? variationKey : "*")
                    .append(",").append(AssetVariation.NODE_TYPE).append(")");
            queryString = query.toString();

            if (LOG.isDebugEnabled()) {
                LOG.debug("dropVariations.query '" + queryString + "'");
            }
            iterator = resolver.findResources(queryString, Query.XPATH);
            while (iterator.hasNext()) {
                ResourceHandle resource = ResourceHandle.use(iterator.next());
                try {
                    dropVariation(resolver, resource);
                } catch (PersistenceException | RuntimeException ex) {
                    LOG.error("Failure dropping variation" + resource, ex);
                    fault = ex;
                }
            }

            resolver.commit();

            if (fault != null) {
                throw fault;
            }
        }
    }

    protected void dropRendition(ResourceResolver resolver, ResourceHandle rendition, String variationKey)
            throws PersistenceException {
        if (!isProtected(rendition)) {
            ResourceHandle variation = rendition.getParent();
            if (StringUtils.isBlank(variationKey) || variationKey.equals(variation.getName())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("dropRendition(" + rendition.getPath() + ")...");
                }
                resolver.delete(rendition);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("keepRendition(" + rendition.getPath() + ") - rendition is protected");
            }
        }
    }

    protected void dropVariation(ResourceResolver resolver, ResourceHandle variation)
            throws PersistenceException {
        if (!variation.listChildren().hasNext()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("dropVariation(" + variation.getPath() + ")...");
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

    protected ResourceResolver createServiceResolver() throws LoginException {
        return resolverFactory.getAdministrativeResourceResolver(null);
    }
}
