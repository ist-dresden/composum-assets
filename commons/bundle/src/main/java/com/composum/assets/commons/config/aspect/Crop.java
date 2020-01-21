package com.composum.assets.commons.config.aspect;

import com.composum.assets.commons.config.ConfigHandle;
import org.apache.sling.api.resource.ValueMap;

import javax.annotation.Nullable;

public class Crop extends GenericAspect {

    public static final Float DEFAULT_VERTICAL = 0.5f;
    public static final Float DEFAULT_HORIZONTAL = 0.5f;
    public static final Float DEFAULT_SCALE = 1f;

    protected final Float vertical;
    protected final Float horizontal;
    protected final Float scale;

    protected Integer width;
    protected Integer height;

    public Crop() {
        vertical = DEFAULT_VERTICAL;
        horizontal = DEFAULT_HORIZONTAL;
        scale = DEFAULT_SCALE;
    }

    public Crop(ConfigHandle config) {
        this.vertical = config.getInherited(ConfigHandle.CROP_VERTICAL, DEFAULT_VERTICAL);
        this.horizontal = config.getInherited(ConfigHandle.CROP_HORIZONTAL, DEFAULT_HORIZONTAL);
        this.scale = config.getInherited(ConfigHandle.CROP_SCALE, DEFAULT_SCALE);
    }

    /**
     * @param values     a simple value map (can be a ValueMap of a resource)
     * @param useDefault if 'false' all attributes can be 'null' (used on config editing)
     */
    public Crop(ValueMap values, boolean useDefault) {
        super(values);
        this.vertical = useDefault
                ? values.get(ConfigHandle.CROP_VERTICAL, DEFAULT_VERTICAL)
                : values.get(ConfigHandle.CROP_VERTICAL, Float.class);
        this.horizontal = useDefault
                ? values.get(ConfigHandle.CROP_HORIZONTAL, DEFAULT_HORIZONTAL)
                : values.get(ConfigHandle.CROP_HORIZONTAL, Float.class);
        this.scale = useDefault
                ? values.get(ConfigHandle.CROP_SCALE, DEFAULT_SCALE)
                : values.get(ConfigHandle.CROP_SCALE, Float.class);
    }

    public Crop(Integer width, Integer height, Crop template) {
        super(template.values);
        this.width = width;
        this.height = height;
        this.vertical = template.vertical;
        this.horizontal = template.horizontal;
        this.scale = template.scale;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(vertical);
        builder.append(",").append(horizontal);
        builder.append(",").append(scale);
        return builder.toString();
    }

    public boolean isDefault() {
        return DEFAULT_VERTICAL.equals(vertical) &&
                DEFAULT_HORIZONTAL.equals(horizontal) &&
                (scale == null || DEFAULT_SCALE.equals(scale));
    }

    @Nullable
    public Float getVertical() {
        return vertical;
    }

    @Nullable
    public Float getHorizontal() {
        return horizontal;
    }

    @Nullable
    public Float getScale() {
        return scale;
    }

    @Nullable
    public Integer getWidth() {
        return width;
    }

    @Nullable
    public Integer getHeight() {
        return height;
    }
}
