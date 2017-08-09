package com.composum.assets.commons.config.transform;

import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.aspect.GenericAspect;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 *
 */
public class Blur extends GenericAspect {

    public static final String FACTOR = "factor";

    public static final String DEFAULT_FACTOR_KEY = "default";
    public static final int DEFAULT_FACTOR = 9;

    public Blur() {
    }

    public Blur(Map<String,Object> template) {
        super (template);
    }

    public Blur(ConfigHandle config) {
        String factor = config.getInherited(ConfigHandle.TRANSFORMATION_BLUR_FACTOR, "");
        if (StringUtils.isNotBlank(factor)) {
            if (factor.equalsIgnoreCase(DEFAULT_FACTOR_KEY)) {
                put(FACTOR, DEFAULT_FACTOR);
            } else {
                put(FACTOR, Integer.decode(factor));
            }
        }
    }

    public boolean isValid() {
        Integer factor = (Integer) get(FACTOR);
        return get(FACTOR) != null && factor > 0;
    }

    public String getFactor() {
        Integer factor = (Integer) get(FACTOR);
        return factor != null ? factor.toString() : "";
    }
}
