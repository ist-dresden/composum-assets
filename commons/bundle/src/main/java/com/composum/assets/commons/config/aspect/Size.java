package com.composum.assets.commons.config.aspect;

import com.composum.assets.commons.config.ConfigHandle;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

public class Size {

    public static final DecimalFormat RATIO_FMT = new DecimalFormat("#0.0#");

    public final Integer width;
    public final Integer height;
    public final Float aspectRatio;
    protected final String aspectRatioRule;

    public Size(ConfigHandle config) {
        Long wdth = config.getInherited(ConfigHandle.WIDTH, Long.class);
        Long hght = config.getInherited(ConfigHandle.HEIGHT, Long.class);
        Float ratio = null;
        aspectRatioRule = config.getInherited(ConfigHandle.ASPECT_RATIO, "");
        if (StringUtils.isNotBlank(aspectRatioRule)) {
            String[] values = aspectRatioRule.split(":");
            if (values.length == 2) {
                float w = Float.parseFloat(values[0]);
                float h = Float.parseFloat(values[1]);
                ratio = w / h;
            }
        }
        width = (wdth != null ? (Integer) wdth.intValue()
                : (hght != null && ratio != null ? Math.round(((float) hght) * ratio) : null));
        height = (hght != null ? (Integer) hght.intValue()
                : (wdth != null && ratio != null ? Math.round(((float) wdth) / ratio) : null));
        aspectRatio = (ratio != null ? ratio
                : (wdth != null && hght != null ? (float) wdth / (float) hght : null));
    }

    public String getWidth() {
        return width != null ? width.toString() : "#";
    }

    public String getHeight() {
        return height != null ? height.toString() : "#";
    }

    public String getAspectRatio() {
        return StringUtils.isNotBlank(aspectRatioRule)
                ? aspectRatioRule
                : aspectRatio != null ? RATIO_FMT.format(aspectRatio) : "#";
    }
}
