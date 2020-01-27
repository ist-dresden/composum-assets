package com.composum.assets.commons.config.aspect;

import com.composum.assets.commons.config.ConfigHandle;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

public class Example {

    public final String path;

    public Example(ConfigHandle config) {
        this(config.getInherited(ConfigHandle.EXAMPLE_IMAGE_PATH, ""));
    }

    /**
     * @param values     a simple value map (can be a ValueMap of a resource)
     * @param useDefault if 'false' all attributes can be 'null' (used on config editing)
     */
    public Example(ValueMap values, boolean useDefault) {
        this(values.get(ConfigHandle.EXAMPLE_IMAGE_PATH, ""));
    }

    public Example(String path) {
        this.path = path;
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(path);
    }

    public String getPath() {
        return path;
    }
}
