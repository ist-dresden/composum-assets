package com.composum.assets.manager.image;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.handle.AssetVariation;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.request.DomIdentifiers;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageAssetBean extends AbstractImageBean<AssetConfig> {

    private transient AssetConfig config;
    private transient List<VariationConfig> variationConfigList;
    private transient List<ImageVariationBean> variationList;

    public ImageAssetBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public ImageAssetBean(BeanContext context) {
        super(context);
    }

    public ImageAssetBean() {
        super();
    }

    @Override
    public void initialize(BeanContext context, Resource resource) {
        imageAssetBean = this;
        super.initialize(context, resource);
        asset = new ImageAsset(context, resource);
    }

    public AssetConfig getConfig() {
        if (config == null) {
            config = asset.getConfig();
        }
        return config;
    }

    @Override
    public String getDomId() {
        return DomIdentifiers.getInstance(context).getElementId(getConfig().getResource());
    }

    public List<VariationConfig> getVariationConfigList() {
        if (variationConfigList == null) {
            AssetConfig config = getConfig();
            variationConfigList = config.getVariationList(true);
            Collections.sort(variationConfigList);
        }
        return variationConfigList;
    }

    public List<ImageVariationBean> getVariationList() {
        if (variationList == null) {
            variationList = new ArrayList<>();
            ResourceHandle assetResource = asset.getResource();
            for (Resource variation : assetResource.getChildren()) {
                if (ResourceUtil.isResourceType(variation, AssetVariation.NODE_TYPE)) {
                    variationList.add(new ImageVariationBean(context, variation));
                }
            }
        }
        return variationList;
    }
}
