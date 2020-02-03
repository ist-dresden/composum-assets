/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.model;

import com.composum.assets.commons.handle.AssetMetaData;
import com.composum.assets.commons.handle.VideoAsset;
import com.composum.sling.core.AbstractSlingBean;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class SimpleVideo extends AbstractSlingBean {

    private transient String title;

    private transient VideoAsset asset;

    public SimpleVideo(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public SimpleVideo() {
    }

    /**
     * Returns the URL of an original of this asset.
     */
    public String getVideoUrl() {
        return getAsset().getVideoUrl();
    }

    /**
     * returns the value of the optional 'title' or a 'jcr:title' property;
     * if this value is empty the 'title' value from the meta data of the asset is used
     */
    @Override
    public String getTitle() {
        if (title == null) {
            title = getProperty("title", "");
            if (StringUtils.isBlank(title)) {
                title = getProperty("jcr:title", "");
                if (StringUtils.isBlank(title)) {
                    title = getMetaData().getTitle();
                }
            }
        }
        return title;
    }

    /**
     * returns the referenced video asset resource
     */
    public VideoAsset getAsset() {
        if (asset == null) {
            asset = new VideoAsset(context, getResource());
        }
        return asset;
    }

    /**
     * returns the meta data of the referenced asset
     */
    public AssetMetaData getMetaData() {
        return getAsset().getMetaData();
    }
}
