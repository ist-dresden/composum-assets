/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.handle;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ImageConfig;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.util.AssetConfigUtil;
import com.composum.assets.commons.util.ImageUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ImageAsset extends AbstractAsset {

    private static final Logger LOG = LoggerFactory.getLogger(ImageAsset.class);

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

    @Override
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

    /**
     * Returns the URL (fed through
     * {@link com.composum.sling.core.util.LinkUtil#getUrl(SlingHttpServletRequest, String)} of an original of this
     * asset.
     */
    public String getImageUrl() {
        String url = "";
        AssetRendition original = getOriginal();
        if (original != null) {
            url = original.getImageUrl();
        }
        return url;
    }

    /**
     * Returns the URI for the given variation and rendition of this asset. If there is no corresponding rendition /
     * variation configured, we return an original and log this as a warning.
     */
    public String getImageUri(String variationKey, String renditionKey) {
        String uri = "";
        VariationConfig variationConfig = this.getConfig().findVariation(variationKey);
        RenditionConfig renditionConfig = variationConfig != null ?
                variationConfig.findRendition(renditionKey) : null;
        AssetVariation variation = variationConfig != null ? this.giveVariation(variationConfig) : null;
        AssetRendition rendition = renditionConfig != null ? variation.giveRendition(renditionConfig) : null;
        if (rendition == null && variation != null) {
            rendition = variation.getOriginal();
            if (rendition != null) {
                LOG.warn("No config available for asset {} variation {} rendition {}, using variation original",
                        this.getPath(), variationKey, renditionKey);
            }
        }
        if (rendition != null) {
            uri = rendition.getImageUri();
        }
        if (StringUtils.isBlank(uri)) {
            LOG.warn("No config available for asset {} variation {} rendition {}", this.getPath(), variationKey,
                    renditionKey);
            // use the original if no configuration available
            AssetRendition original = this.getOriginal();
            if (original != null) { uri = original.getImageUri(); }
        }
        return uri;
    }


    public String getImageCSS() {
        return ImageUtil.mimeTypeToCss(getMimeType());
    }
}
