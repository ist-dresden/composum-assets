package com.composum.assets.commons.util;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.Calendar;
import java.util.Date;

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

    public static String getImageUri(ImageAsset asset, String variationKey, String renditionKey) {
        StringBuilder builder = new StringBuilder();
        if (asset != null) {
            String mimeType = asset.getMimeType();
            if (mimeType != null) {
                AssetConfig config = asset.getConfig();
                VariationConfig variation = config.findVariation(variationKey);
                RenditionConfig rendition = variation.findRendition(renditionKey);
                String path = asset.getPath();
                String ext = mimeType.substring("image/".length());
                if (ext.equals("jpeg")) {
                    ext = "jpg";
                }
                if (path.endsWith("." + ext)) {
                    path = path.substring(0, path.length() - (ext.length() + 1));
                }
                String name = path.substring(path.lastIndexOf('/') + 1);
                builder.append(path);
                builder.append(".adaptive");
                builder.append('.').append(variation.getName());
                builder.append('.').append(rendition.getName());
                builder.append('.').append(ext);
                builder.append('/').append(getCacheHash(asset));
                builder.append('/').append(name);
                builder.append('.').append(ext);
            }
        }
        return builder.toString();
    }

    public static String getCacheHash(ImageAsset asset) {
        StringBuilder builder = new StringBuilder("T");
        Calendar lastModified = asset.getLastModified();
        if (lastModified != null) {
            builder.append(lastModified.getTimeInMillis());
        } else {
            builder.append(new Date().getTime());
        }
        return builder.toString();
    }

    public static String mimeTypeToCss(String mimeType) {
        return mimeType.substring(mimeType.indexOf('/') + 1).replaceAll("[+]", " ");
    }
}
