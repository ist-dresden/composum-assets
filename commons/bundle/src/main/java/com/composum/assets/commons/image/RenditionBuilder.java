/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.image;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.handle.AbstractAsset;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.AssetVariation;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.concurrent.LazyCreationService;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.Validate;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RenditionBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(RenditionBuilder.class);

    public static final HashMap<String, Object> CRUD_VARIATION_PROPS;

    static {
        CRUD_VARIATION_PROPS = new HashMap<>();
        CRUD_VARIATION_PROPS.put(ResourceUtil.PROP_PRIMARY_TYPE, "cpa:Variation");
        CRUD_VARIATION_PROPS.put(ResourceUtil.PROP_RESOURCE_TYPE, AssetVariation.RESOURCE_TYPE);
    }

    public static final HashMap<String, Object> CRUD_RENDITION_PROPS;

    static {
        CRUD_RENDITION_PROPS = new HashMap<>();
        CRUD_RENDITION_PROPS.put(ResourceUtil.PROP_PRIMARY_TYPE, "cpa:Rendition");
        CRUD_RENDITION_PROPS.put(ResourceUtil.PROP_RESOURCE_TYPE, AssetRendition.RESOURCE_TYPE);
        CRUD_RENDITION_PROPS.put(AbstractAsset.VALID, false);
        CRUD_RENDITION_PROPS.put(JcrConstants.JCR_MIXINTYPES, new String[]{JcrConstants.MIX_LOCKABLE});
    }

    public static final HashMap<String, Object> CRUD_FILE_PROPS;

    static {
        CRUD_FILE_PROPS = new HashMap<>();
        CRUD_FILE_PROPS.put(ResourceUtil.PROP_PRIMARY_TYPE, "nt:file");
    }

    public static final HashMap<String, Object> CRUD_RESOURCE_PROPS;

    static {
        CRUD_RESOURCE_PROPS = new HashMap<>();
        CRUD_RESOURCE_PROPS.put(ResourceUtil.PROP_PRIMARY_TYPE, "nt:resource");
    }

    protected final BuilderContext context;
    protected final ImageAsset asset;
    protected final AssetConfig assetConfig;
    protected final VariationConfig variationConfig;
    protected final RenditionConfig renditionConfig;
    protected final String renditionName;
    protected final String variationName;
    protected final String renditionPath;
    protected final String variationPath;

    public RenditionBuilder(ImageAsset asset, RenditionConfig config, BuilderContext context)
            throws PersistenceException {

        if (!asset.getResource().isValid()) {
            throw new PersistenceException("asset not valid");
        }

        this.asset = asset;
        renditionConfig = config;
        this.context = context;

        variationConfig = renditionConfig.getVariation();
        assetConfig = variationConfig.getAssetConfig();

        renditionName = renditionConfig.getName();
        variationName = variationConfig.getName();

        variationPath = asset.getPath() + "/" + variationName;
        renditionPath = variationPath + "/" + renditionName;
    }

    /** Retrieves or creates a rendition. */
    public AssetRendition getOrCreateRendition() throws RepositoryException, PersistenceException {
        return context.getLazyCreationService().getOrCreate(asset.getResolver(), renditionPath,
                getRetrievalStrategy(), getCreationStrategy(), getInitializationStrategy(), getParentStrategy());
    }

    protected Resource createRendition(ResourceResolver resolver, Resource renditionPath)
            throws IOException, RepositoryException {
        BeanContext.Service beanContext = new BeanContext.Service(resolver);
        AssetVariation variation = new AssetVariation(beanContext, renditionPath.getParent(), asset);
        return buildRenditionContent(resolver, variation, beanContext).getResource();
    }

    /**
     * Build the rendition into the rendition resource and switch to valid state.
     */
    protected AssetRendition buildRenditionContent(ResourceResolver resolver, AssetVariation variation,
                                                   BeanContext beanContext)
            throws IOException, RepositoryException {
        AssetRendition rendition = variation.getRendition(renditionName);
        AssetRendition original;
        if (!rendition.isValid() && !rendition.equals(original = rendition.getOriginal())) {

            if (original != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Building rendition '" + renditionPath + "'...");
                }

                RenditionReader reader = new RenditionReader();
                BufferedImage image = reader.readImage(original, context);

                // TODO make transformers configurable via OSGi
                RenditionTransformer transformer = context.getService().getTransformers().get("default");
                image = transformer.transform(rendition, image, context);

                RenditionWriter writer = new RenditionWriter();
                InputStream imageStream = writer.writeImage(rendition, image, context);

                Resource fileResource = resolver.create(rendition.getResource(), asset.getName(), CRUD_FILE_PROPS);
                Resource contentResource = resolver.create(fileResource, "jcr:content", CRUD_RESOURCE_PROPS);

                ModifiableValueMap contentValues = contentResource.adaptTo(ModifiableValueMap.class);
                contentValues.put("jcr:data", imageStream);
                contentValues.put("jcr:mimeType", rendition.getMimeType());
                resolver.commit();

                Resource renditionResource = resolver.getResource(rendition.getPath());
                contentValues = renditionResource.adaptTo(ModifiableValueMap.class);
                contentValues.put(AbstractAsset.VALID, true);
                resolver.commit();
                if (LOG.isInfoEnabled()) {
                    LOG.info("New rendition '" + renditionPath + "' built successfully.");
                }

            } else {
                LOG.error("No original found to build rendition '" + renditionPath + "'.");
            }
        }

        return rendition;
    }

    protected LazyCreationService.RetrievalStrategy<AssetRendition> getRetrievalStrategy() {
        return new LazyCreationService.RetrievalStrategy<AssetRendition>() {
            @Override
            public AssetRendition get(ResourceResolver resolver, String path) throws RepositoryException {
                return context.getService().getRendition(asset, variationConfig.getName(), renditionName);
            }
        };
    }

    protected LazyCreationService.ParentCreationStrategy getParentStrategy() {
        return new LazyCreationService.ParentCreationStrategy() {
            @Override
            public Resource createParent(ResourceResolver resolver, Resource parentsParent, String parentName,
                                         int level) throws RepositoryException, PersistenceException {
                Validate.inclusiveBetween(1, 1, level, "Bug: level %s not expected for %s", level, parentsParent);
                LOG.debug("Creating parent {}/{}", parentsParent.getPath(), parentName);
                return resolver.create(parentsParent, parentName, CRUD_VARIATION_PROPS);
            }
        };
    }

    protected LazyCreationService.CreationStrategy getCreationStrategy() {
        return new LazyCreationService.CreationStrategy() {
            @Override
            public Resource create(ResourceResolver resolver, Resource parent, String name) throws RepositoryException,
                    PersistenceException {
                LOG.debug("Creating {}/{}", parent.getPath(), name);
                return resolver.create(parent, name, CRUD_RENDITION_PROPS);
            }
        };
    }

    protected LazyCreationService.InitializationStrategy getInitializationStrategy() {
        return new LazyCreationService.InitializationStrategy() {
            @Override
            public void initialize(ResourceResolver resolver, Resource resource) throws RepositoryException, PersistenceException {
                LOG.debug("Initializing {}/{}", resource.getPath());
                try {
                    RenditionBuilder.this.createRendition(resolver, resource);
                } catch (IOException e) {
                    throw new PersistenceException("Error creating " + resource.getPath(), e);
                }
            }
        };
    }

}
