package com.composum.assets.commons.config.aspect;

import com.composum.assets.commons.config.ConfigHandle;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class Example {

    public final String path;

    public Example(ConfigHandle config) {
        String path = config.getInherited(ConfigHandle.EXAMPLE_IMAGE_PATH, "");
        Resource image = null;
        if (StringUtils.isNotBlank(path)) {
            Resource configRes = config.getResource();
            image = configRes.getResourceResolver().getResource(path);
        }
        this.path = image != null ? image.getPath() : null;
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(path);
    }
}
