/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.handle;

import com.composum.sling.core.AbstractSlingBean;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class AssetMetaData extends AbstractSlingBean {

    private transient Integer width;
    private transient Integer height;

    private transient String altText;
    private transient String description;

    public AssetMetaData(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public AssetMetaData(BeanContext context) {
        super(context);
    }

    public AssetMetaData() {
        super();
    }

    public int getWidth() {
        if (width == null) {
            width = getProperty("width", 0);
        }
        return width;
    }

    public int getHeight() {
        if (height == null) {
            height = getProperty("height", 0);
        }
        return height;
    }

    public String getAltText() {
        if (altText == null) {
            altText = getProperty("altText", "");
            if (StringUtils.isBlank(altText)) {
                altText = getDescription();
            }
        }
        return altText;
    }

    public String getDescription() {
        if (description == null) {
            description = getProperty("description", "");
        }
        return description;
    }
}
