package com.composum.assets.commons.config.aspect;

import com.composum.assets.commons.config.ConfigHandle;

public class Crop {

    public static final float DEFAULT_VERTICAL = 0.5f;
    public static final float DEFAULT_HORIZONTAL = 0.5f;
    public static final float DEFAULT_SCALE = 1f;

    public final float vertical;
    public final float horizontal;
    public final Float scale;

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
}
