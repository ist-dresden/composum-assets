/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.handle;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public class ImageMetaData extends AssetMetaData {

    public ImageMetaData(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public ImageMetaData(BeanContext context) {
        super(context);
    }

    public ImageMetaData() {
    }
}
