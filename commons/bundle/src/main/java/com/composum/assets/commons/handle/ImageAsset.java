/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.handle;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ImageConfig;
import com.composum.assets.commons.util.AssetConfigUtil;
import com.composum.assets.commons.util.ImageUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import org.apache.sling.api.resource.Resource;

import java.util.List;

public class ImageAsset extends AbstractAsset {

    public static final ResourceFilter FILTER = new ResourceFilter.PrimaryTypeFilter(
            new StringFilter.WhiteList("^cpa:Asset$"));

    public ImageAsset(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public ImageAsset(BeanContext context, Resource resource, AssetConfig config) {
        super(context, resource, config);
    }

    public ImageAsset() {
    }

    @Override
    public boolean isValid() {
        return super.isValid() && resource.isResourceType(IMAGE_RESOURCE_TYPE);
    }

    public AbstractAsset getAsset() {
        return this;
    }

    @Override
    protected AssetConfig createAssetConfig(List<ResourceHandle> cascade) {
        return new ImageConfig(cascade);
    }

    @Override
    protected List<ResourceHandle> getConfigCascade() {
        List<ResourceHandle> cascade = super.getConfigCascade();
        AssetConfigUtil.assetConfigCascade(cascade, resource, ImageConfig.NAME_PATTERN);
        return cascade;
    }

    public String getImageUrl() {
        String url = "";
        AssetRendition original = getOriginal();
        if (original != null) {
            url = original.getImageUrl();
        }
        return url;
    }

    public String getImageCSS() {
        return ImageUtil.mimeTypeToCss(getMimeType());
    }
}
