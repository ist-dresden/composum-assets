package com.composum.assets.commons.config.aspect;

import com.composum.assets.commons.config.ConfigHandle;

import java.util.Map;

public class Crop {

    public static final float DEFAULT_VERTICAL = 0.5f;
    public static final float DEFAULT_HORIZONTAL = 0.5f;
    public static final float DEFAULT_SCALE = 1f;

    public final float vertical;
    public final float horizontal;
    public final Float scale;

    private Integer width;
    private Integer height;

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

    public Crop(Map<String, Object> config) {
        this((Integer) config.get(ConfigHandle.WIDTH),
                (Integer) config.get(ConfigHandle.HEIGHT),
                (Float) config.get(ConfigHandle.CROP_VERTICAL),
                (Float) config.get(ConfigHandle.CROP_HORIZONTAL),
                (Float) config.get(ConfigHandle.CROP_SCALE));
    }

    public Crop(Integer width, Integer height, Float vertical, Float horizontal, Float scale) {
        this.width = width;
        this.height = height;
        this.vertical = vertical != null ? vertical : DEFAULT_VERTICAL;
        this.horizontal = horizontal = horizontal != null ? horizontal : DEFAULT_HORIZONTAL;
        this.scale = scale != null ? scale : DEFAULT_SCALE;
    }

    public Crop(Integer width, Integer height, Crop config) {
        this(width, height, config.vertical, config.horizontal, config.scale);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(vertical);
        builder.append(",").append(horizontal);
        builder.append(",").append(scale);
        return builder.toString();
    }

    public boolean isDefault() {
        return vertical == DEFAULT_VERTICAL &&
                horizontal == DEFAULT_HORIZONTAL &&
                (scale == null || scale == DEFAULT_SCALE);
    }

    public float getVertical() {
        return vertical;
    }

    public float getHorizontal() {
        return horizontal;
    }

    public Float getScale() {
        return scale;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }
}
