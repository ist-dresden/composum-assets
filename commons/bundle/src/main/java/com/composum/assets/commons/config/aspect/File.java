package com.composum.assets.commons.config.aspect;

import com.composum.assets.commons.config.ConfigHandle;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import javax.annotation.Nullable;

import static com.composum.assets.commons.config.ConfigHandle.FILE_QUALITY;

public class File extends GenericAspect {

    public static final String DEFAULT_QUALITY = "80";

    protected Double quality;

    public File(ConfigHandle config) {
        this(config.getInherited(FILE_QUALITY, ""));
    }

    /**
     * @param values     a simple value map (can be a ValueMap of a resource)
     * @param useDefault if 'false' all attributes can be 'null' (used on config editing)
     */
    public File(ValueMap values, boolean useDefault) {
        this(useDefault
                ? values.get(ConfigHandle.FILE_QUALITY, DEFAULT_QUALITY)
                : values.get(ConfigHandle.FILE_QUALITY, String.class));
    }

    public File(String qualityRule) {
        if (StringUtils.isNotBlank(qualityRule)) {
            quality = Double.parseDouble(qualityRule);
            if (quality > 1.0) {
                quality = quality / 100.0;
            }
        }
    }

    @Nullable
    public Double getQuality() {
        return quality;
    }
}
