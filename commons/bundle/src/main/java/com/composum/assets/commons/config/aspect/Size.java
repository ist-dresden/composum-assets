package com.composum.assets.commons.config.aspect;

import com.composum.assets.commons.config.ConfigHandle;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class Size extends GenericAspect {

    public static final DecimalFormat RATIO_FMT = new DecimalFormat("#0.0#");

    protected final Integer width;
    protected final Integer height;
    protected final Float aspectRatio;
    protected final String aspectRatioRule;

    public Size(ConfigHandle config) {
        this(config.getInherited(ConfigHandle.WIDTH, Integer.class),
                config.getInherited(ConfigHandle.HEIGHT, Integer.class),
                config.getInherited(ConfigHandle.ASPECT_RATIO, ""));
    }

    /**
     * @param values     a simple value map (can be a ValueMap of a resource)
     * @param useDefault if 'false' all attributes can be 'null' (used on config editing)
     */
    public Size(ValueMap values, boolean useDefault) {
        this(values.get(ConfigHandle.WIDTH, Integer.class),
                values.get(ConfigHandle.HEIGHT, Integer.class),
                values.get(ConfigHandle.ASPECT_RATIO, ""));
    }

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

    @Nullable
    public Integer getWidth() {
        return width;
    }

    @Nonnull
    public String getWidthStr() {
        return width != null ? width.toString() : "";
    }

    @Nullable
    public Integer getHeight() {
        return height;
    }

    @Nonnull
    public String getHeightStr() {
        return height != null ? height.toString() : "";
    }

    @Nullable
    public String getAspectRatioRule() {
        return aspectRatioRule;
    }

    @Nullable
    public Float getAspectRatio() {
        return aspectRatio;
    }

    @Nonnull
    public String getAspectRatioStr() {
        return StringUtils.isNotBlank(aspectRatioRule)
                ? aspectRatioRule
                : aspectRatio != null ? RATIO_FMT.format(aspectRatio) : "";
    }
}
