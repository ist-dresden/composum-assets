package com.composum.assets.manager.image;

import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.AssetVariation;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.List;

public class ImageVariationBean extends AbstractImageBean<VariationConfig> {

    protected VariationConfig config;
    protected AssetVariation variation;
    protected AssetRendition original;

    public ImageVariationBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public ImageVariationBean(BeanContext context) {
        super(context);
    }

    public ImageVariationBean() {
        super();
    }

    @Override
    public void initialize(BeanContext context, Resource resource) {
        super.initialize(context, resource);
        config = asset.getConfig().findVariation(resource.getName());
        variation = asset.getVariation(resource.getName());
        if (variation != null) {
            original = variation.getRendition(ConfigHandle.ORIGINAL);
        }
    }

    public VariationConfig getConfig() {
        return config;
    }

    public List<RenditionConfig> getRenditionConfigs() {
        return config.getRenditionList(true);
    }

    @Nonnull
    @Override
    public String getName() {
        return variation != null ? variation.getName() : config.getName();
    }

    @Nonnull
    @Override
    public String getPath() {
        return variation != null ? variation.getPath() : config.getPath();
    }

    @Override
    public ResourceHandle getResource() {
        return ResourceHandle.use(variation != null ? variation.getResource() : config.getResource());
    }

    public boolean isHasOriginal() {
        return original != null;
    }

    public AssetRendition getOriginal() {
        return original;
    }

    public String getOriginalUri() {
        return original != null ? original.getPath() + "/" + asset.getName() : "";
    }
}
