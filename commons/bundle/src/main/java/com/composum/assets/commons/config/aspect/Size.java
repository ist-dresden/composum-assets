package com.composum.assets.commons.config.aspect;

import com.composum.assets.commons.config.ConfigHandle;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.Map;

public class Size {

    public static final DecimalFormat RATIO_FMT = new DecimalFormat("#0.0#");

    public final Integer width;
    public final Integer height;
    public final Float aspectRatio;
    protected final String aspectRatioRule;

    public Size(ConfigHandle config) {
        this(config.getInherited(ConfigHandle.WIDTH, Integer.class),
                config.getInherited(ConfigHandle.HEIGHT, Integer.class),
                config.getInherited(ConfigHandle.ASPECT_RATIO, ""));
    }

    public Size(Map<String, Object> options) {
        this((Integer) options.get(ConfigHandle.WIDTH),
                (Integer) options.get(ConfigHandle.HEIGHT),
                (String) options.get(ConfigHandle.ASPECT_RATIO));
    }

    @SuppressWarnings("ConstantConditions")
    public Size(Integer cfgWidth, Integer cfgHeight, String cfgRatio) {
        Float ratio = null;
        aspectRatioRule = cfgRatio != null ? cfgRatio : "";
        if (StringUtils.isNotBlank(aspectRatioRule)) {
            String[] values = aspectRatioRule.split(":");
            if (values.length == 2) {
                float w = Float.parseFloat(values[0]);
                float h = Float.parseFloat(values[1]);
                ratio = w / h;
            }
        }
        width = (cfgWidth != null ? cfgWidth
                : (cfgHeight != null && ratio != null ? Math.round(((float) cfgHeight) * ratio) : null));
        height = (cfgHeight != null ? cfgHeight
                : (cfgWidth != null && ratio != null ? Math.round(((float) cfgWidth) / ratio) : null));
        aspectRatio = (ratio != null ? ratio
                : (cfgWidth != null && cfgHeight != null ? (float) cfgWidth / (float) cfgHeight : null));
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
