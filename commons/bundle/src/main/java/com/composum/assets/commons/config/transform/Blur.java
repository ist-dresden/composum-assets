package com.composum.assets.commons.config.transform;

import com.composum.assets.commons.config.ConfigHandle;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by rw on 06.06.17.
 */
public class Blur {

    public static final String DEFAULT_FACTOR_KEY = "default";
    public static final int DEFAULT_FACTOR = 9;

    public final Integer factor;

    public Blur(ConfigHandle config) {
        String factor = config.getInherited(ConfigHandle.TRANSFORMATION_BLUR_FACTOR, "");
        if (StringUtils.isNotBlank(factor)) {
            if (factor.equalsIgnoreCase(DEFAULT_FACTOR_KEY)) {
                this.factor = DEFAULT_FACTOR;
            } else {
                this.factor = Integer.decode(factor);
            }
        } else {
            this.factor = null;
        }
    }

    public boolean isValid() {
        return factor != null && factor > 0;
    }

    public String getFactor() {
        return factor != null ? factor.toString() : "";
    }
}
