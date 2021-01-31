/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.config;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.aspect.Example;
import com.composum.sling.core.ResourceHandle;
import org.apache.sling.api.resource.Resource;

import java.util.List;
import java.util.regex.Pattern;

public class ImageConfig extends AssetConfig {

    public static final String CHILD_NAME = "imageconfig";
    public static final Pattern NAME_PATTERN = Pattern.compile("^" + CHILD_NAME + "$");
    public static final String NODE_TYPE = AssetsConstants.NODE_TYPE_IMAGE_CONFIG;

    public class Preview extends Example {

        public Preview() {
            super(ImageConfig.this.getPath());
        }
    }

    public ImageConfig(Resource resource) {
        super(resource);
    }

    public ImageConfig(List<ResourceHandle> cascade) {
        super(cascade);
    }

    @Override
    public String getConfigType() {
        return CHILD_NAME;
    }

    @Override
    public Boolean getExtension() {
        if (extension == null) {
            extension = getResource().getProperty(EXTENSION, Boolean.TRUE);
        }
        return extension;
    }

    public Example getExample() {
        return new Preview();
    }
}
