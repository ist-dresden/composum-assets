package com.composum.assets.manager.image;

import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.handle.AssetHandle;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.sling.core.AbstractSlingBean;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public abstract class AbstractImageBean<Config extends ConfigHandle> extends AbstractSlingBean {

    public static final String IMAGE_ASSET_BEAN = "imageAssetBean";

    protected ImageAssetBean imageAssetBean;
    protected ImageAsset asset;

    public AbstractImageBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public AbstractImageBean(BeanContext context) {
        super(context);
    }

    public AbstractImageBean() {
        super();
    }

    @Override
    public void initialize(BeanContext context, Resource resource) {
        Resource assetResource = getImageAssetResource(resource);
        super.initialize(context, assetResource);
        if (imageAssetBean == null) {
            imageAssetBean = context.getAttribute(IMAGE_ASSET_BEAN, ImageAssetBean.class);
            if (imageAssetBean == null) {
                imageAssetBean = new ImageAssetBean(context, assetResource);
                context.setAttribute(IMAGE_ASSET_BEAN, imageAssetBean, BeanContext.Scope.request);
            }
            asset = imageAssetBean.getAsset();
        }
    }

    public Resource getImageAssetResource(Resource resource) {
        while (resource != null && !resource.isResourceType(AssetHandle.IMAGE_RESOURCE_TYPE)) {
            resource = resource.getParent();
        }
        return resource;
    }

    public ImageAsset getAsset() {
        return asset;
    }

    public abstract Config getConfig();
}
