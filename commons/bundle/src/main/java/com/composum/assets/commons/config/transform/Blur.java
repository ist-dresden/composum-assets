package com.composum.assets.commons.config.transform;

import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.aspect.GenericAspect;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 */
public class Blur extends GenericAspect {

    public static final String FACTOR = "factor";

    public static final String DEFAULT_FACTOR_KEY = "default";
    public static final int DEFAULT_FACTOR = 9;

    protected Integer factor;

    public Blur() {
    }

    public Blur(ConfigHandle config) {
        initialze(config.getInherited(ConfigHandle.TRANSFORMATION_BLUR_FACTOR, ""));
    }

    public Blur(ValueMap values, boolean useDefault) {
        super(values);
        initialze(values.get(ConfigHandle.TRANSFORMATION_BLUR_FACTOR, ""));
    }

    public Blur(String factorRule) {
        initialze(factorRule);
    }

    public void initialze(String factorRule) {
        factor = StringUtils.isNotBlank(factorRule)
                ? factorRule.equalsIgnoreCase(DEFAULT_FACTOR_KEY) ? DEFAULT_FACTOR : Integer.decode(factorRule)
                : null;
    }

    public boolean isValid() {
        return factor != null && factor > 0;
    }

    @Nullable
    public Integer getFactor() {
        return factor;
    }

    @Nonnull
    public String getFactorStr() {
        return factor != null ? factor.toString() : "";
    }
}
