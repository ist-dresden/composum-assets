package com.composum.assets.manager.image;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.AssetVariation;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.request.DomIdentifiers;
import com.composum.sling.core.util.LinkUtil;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ImageRenditionBean extends AbstractImageBean<RenditionConfig> {

    protected RenditionConfig config;
    protected AssetVariation variation;
    protected AssetRendition rendition;

    private transient String imageUrl;
    private transient String imageUri;

    public ImageRenditionBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public ImageRenditionBean(BeanContext context) {
        super(context);
    }

    public ImageRenditionBean() {
        super();
    }

    @Override
    public void initialize(BeanContext context, Resource resource) {
        super.initialize(context, resource);
        String variationName = Objects.requireNonNull(resource.getParent()).getName();
        AssetConfig assetConfig = asset.getConfig();
        VariationConfig variationConfig = assetConfig.findVariation(variationName);
        config = variationConfig.findRendition(resource.getName());
        variation = asset.getVariation(variationName);
        rendition = variation != null ? variation.getRendition(resource.getName()) : null;
    }

    @Override
    public RenditionConfig getConfig() {
        return config;
    }

    @Override
    public String getDomId() {
        return DomIdentifiers.getInstance(context).getElementId(getConfig().getResource());
    }

    @Nonnull
    @Override
    public String getName() {
        return rendition != null ? rendition.getName() : config.getName();
    }

    @Nonnull
    @Override
    public String getPath() {
        return rendition != null ? rendition.getPath() : config.getPath();
    }

    @Override
    public ResourceHandle getResource() {
        return ResourceHandle.use(rendition != null ? rendition.getResource() : config.getResource());
    }

    public String getImageUri() {
        if (imageUri == null) {
            imageUri = "";
            ImageAsset image = getAsset();
            if (image != null) {
                imageUri = image.getImageUri(config.getVariation().getName(), config.getName());
            }
        }
        return imageUri;
    }

    public String getImageUrl() {
        if (imageUrl == null) {
            imageUrl = LinkUtil.getUrl(getRequest(), getImageUri());
        }
        return imageUrl;
    }
}
