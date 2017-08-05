/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.config;

import com.composum.sling.core.ResourceHandle;
import org.apache.sling.api.resource.Resource;

import java.util.List;
import java.util.regex.Pattern;

public class ImageConfig extends AssetConfig {

    public static final String CHILD_NAME = "imageconfig";
    public static final Pattern NAME_PATTERN = Pattern.compile("^" + CHILD_NAME + "$");

    public ImageConfig(Resource resource) {
        super(resource);
    }

    public ImageConfig(List<ResourceHandle> cascade) {
        super(cascade);
    }
}
