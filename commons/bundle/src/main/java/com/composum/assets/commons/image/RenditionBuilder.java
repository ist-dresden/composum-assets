/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.image;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.handle.AbstractAsset;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.AssetVariation;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.service.AdaptiveImageService;
import com.composum.sling.core.concurrent.LazyCreationService;
import com.composum.sling.core.util.ResourceUtil;
import com.google.common.collect.ImmutableMap;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/** Builds the transient renditions in {@value AssetsConstants#PATH_TRANSIENTS} from the originals in /content. */
public class RenditionBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(RenditionBuilder.class);

    protected static final Map<String, Object> VARIATION_PROPS;

    static {
        VARIATION_PROPS = new HashMap<>();
        VARIATION_PROPS.put(ResourceUtil.PROP_PRIMARY_TYPE, AssetsConstants.NODE_TYPE_VARIATION);
        VARIATION_PROPS.put(ResourceUtil.PROP_RESOURCE_TYPE, AssetVariation.RESOURCE_TYPE);
    }

    protected static final Map<String, Object> RENDITION_PROPS;

    static {
        RENDITION_PROPS = new HashMap<>();
        RENDITION_PROPS.put(ResourceUtil.PROP_PRIMARY_TYPE, AssetsConstants.NODE_TYPE_RENDITION);
        RENDITION_PROPS.put(ResourceUtil.PROP_RESOURCE_TYPE, AssetRendition.RESOURCE_TYPE);
        RENDITION_PROPS.put(AbstractAsset.VALID, false);
        RENDITION_PROPS.put(JcrConstants.JCR_MIXINTYPES, new String[]{JcrConstants.MIX_LOCKABLE});
    }

    protected static final Map<String, Object> FILE_PROPS;

    static {
        FILE_PROPS = new HashMap<>();
        FILE_PROPS.put(ResourceUtil.PROP_PRIMARY_TYPE, "nt:file");
    }

    protected static final Map<String, Object> RESOURCE_PROPS;

    static {
        RESOURCE_PROPS = new HashMap<>();
        RESOURCE_PROPS.put(ResourceUtil.PROP_PRIMARY_TYPE, "nt:resource");
        RESOURCE_PROPS.put(JcrConstants.JCR_MIXINTYPES,
                new String[]{ResourceUtil.TYPE_VERSIONABLE, ResourceUtil.TYPE_LAST_MODIFIED});
    }

    protected static final Map<String, Object> FOLDER_PROPS =
            ImmutableMap.of(ResourceUtil.PROP_PRIMARY_TYPE, ResourceUtil.TYPE_SLING_FOLDER);

    @Nonnull
    protected final BuilderContext context;
    @Nonnull
    protected final ImageAsset asset;
    @Nonnull
    protected final AssetConfig assetConfig;
    @Nonnull
    protected final VariationConfig variationConfig;
    @Nonnull
    protected final RenditionConfig renditionConfig;
    @Nonnull
    protected final String transientsPath;
    @Nonnull
    protected final AssetVariation variation;

    public RenditionBuilder(@Nonnull ImageAsset asset, @Nonnull RenditionConfig config, @Nonnull BuilderContext context)
            throws PersistenceException {

        if (!asset.getResource().isValid()) {
            throw new PersistenceException("asset not valid");
        }

        this.asset = asset;
        renditionConfig = config;
        this.context = context;

        variationConfig = renditionConfig.getVariation();
        assetConfig = variationConfig.getAssetConfig();

        variation = asset.giveVariation(variationConfig);
        AssetRendition rendition = variation.giveRendition(renditionConfig);
        transientsPath = rendition.getTransientsPath();

    }

    /** Retrieves or creates a rendition. */
    public AssetRendition getOrCreateRendition() throws RepositoryException, PersistenceException {
        return context.getLazyCreationService().getOrCreate(asset.getResolver(), transientsPath,
                getRetrievalStrategy(), getCreationStrategy(), getInitializationStrategy(), getParentCreationStrategy());
    }

    /**
     * Build the rendition into the rendition resource and switch to valid state.
     */
    protected void createRendition(ResourceResolver resolver)
            throws IOException, RepositoryException {
        AssetRendition rendition = variation.giveRendition(renditionConfig);
        AssetRendition original;
        if (!rendition.isValid() && !rendition.equals(original = rendition.getOriginal())) {

            if (original != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Building rendition transient '" + transientsPath + "'...");
                }

                RenditionReader reader = new RenditionReader();
                BufferedImage image = reader.readImage(original, context);

                // TODO make transformers configurable via OSGi
                RenditionTransformer transformer = context.getService().getRenditionTransformer();
                image = transformer.transform(rendition, image, context);

                RenditionWriter writer = new RenditionWriter();
                InputStream imageStream = writer.writeImage(rendition, image, context);

                Resource transientResource = resolver.getResource(rendition.getTransientsPath());
                Resource fileResource = resolver.create(transientResource, asset.getName(), FILE_PROPS);
                Resource contentResource = resolver.create(fileResource, "jcr:content", RESOURCE_PROPS);

                ModifiableValueMap contentValues = contentResource.adaptTo(ModifiableValueMap.class);
                contentValues.put("jcr:data", imageStream);
                contentValues.put("jcr:mimeType", rendition.getMimeType());
                resolver.commit();

                context.getMetaPropertiesService().adjustMetaProperties(resolver, fileResource);
                resolver.commit();

                Resource renditionResource = resolver.getResource(rendition.getTransientsPath());
                contentValues = renditionResource.adaptTo(ModifiableValueMap.class);
                contentValues.put(AbstractAsset.VALID, true);
                resolver.commit();
                if (LOG.isInfoEnabled()) {
                    LOG.info("New rendition '" + renditionConfig.getName() + "' built successfully.");
                }

            } else {
                LOG.error("No original found to build rendition '" + rendition.getPath() + "'.");
            }
        }

    }

    protected LazyCreationService.RetrievalStrategy<AssetRendition> getRetrievalStrategy() {
        return new LazyCreationService.RetrievalStrategy<AssetRendition>() {
            @Override
            public AssetRendition get(ResourceResolver resolver, String path) throws RepositoryException {
                AdaptiveImageService service = context.getService();
                String variationName = variationConfig.getName();
                String renditionName = renditionConfig.getName();
                return service.getRendition(asset, variationName, renditionName);
            }
        };
    }

    protected LazyCreationService.ParentCreationStrategy getParentCreationStrategy() {
        return new LazyCreationService.ParentCreationStrategy() {
            @Override
            public Resource createParent(ResourceResolver resolver, Resource parentsParent, String parentName,
                                         int level) throws RepositoryException, PersistenceException {
                LOG.debug("Creating parent {}/{}", parentsParent.getPath(), parentName);
                Map<String, Object> props;
                switch (level) {
                    case 2:
                        props = VARIATION_PROPS;
                        break;
                    default:
                        props = FOLDER_PROPS;
                }
                return resolver.create(parentsParent, parentName, props);
            }
        };
    }

    protected LazyCreationService.CreationStrategy getCreationStrategy() {
        return new LazyCreationService.CreationStrategy() {
            @Override
            public Resource create(ResourceResolver resolver, Resource parent, String name) throws RepositoryException,
                    PersistenceException {
                LOG.debug("Creating {}/{}", parent.getPath(), name);
                return resolver.create(parent, name, RENDITION_PROPS);
            }
        };
    }

    protected LazyCreationService.InitializationStrategy getInitializationStrategy() {
        return new LazyCreationService.InitializationStrategy() {
            @Override
            public void initialize(ResourceResolver resolver, Resource resource) throws RepositoryException, PersistenceException {
                LOG.debug("Initializing {}/{}", resource.getPath());
                try {
                    RenditionBuilder.this.createRendition(resolver);
                } catch (IOException e) {
                    throw new PersistenceException("Error initializing " + resource.getPath(), e);
                }
            }
        };
    }

}
