package com.composum.assets.commons.util;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class ImageUtil {

    public static ImageAsset getImageAsset(BeanContext context, Resource resource, String propertyName) {
        ImageAsset asset = null;
        String assetPath = resource.getValueMap().get(propertyName, "");
        if (StringUtils.isNotBlank(assetPath)) {
            Resource assetResource = context.getResource().getResourceResolver().getResource(assetPath);
            if (assetResource != null) {
                asset = new ImageAsset(context, assetResource);
            }
        }
        if (asset == null) {
            asset = new ImageAsset(context, resource);
        }
        return asset;
    }

    /**
     * Calls {@link ImageAsset#getImageUri(String, String)}.
     *
     * @deprecated for backward compatibility only
     */
    @Deprecated
    public static String getImageUri(ImageAsset asset, String variationKey, String renditionKey) {
        return asset != null ? asset.getImageUri(variationKey, renditionKey) : null;
    }


    public static String mimeTypeToCss(String mimeType) {
        return mimeType.substring(mimeType.indexOf('/') + 1).replaceAll("[+]", " ");
    }
}
