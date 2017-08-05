package com.composum.assets.manager.config;


import com.composum.assets.commons.config.ConfigHandle;

import java.util.LinkedHashMap;

public class ConfigSet {

    public final LinkedHashMap<String, ConfigHandle.ConfigProperty> file = new LinkedHashMap<>();
    public final LinkedHashMap<String, ConfigHandle.ConfigProperty> size = new LinkedHashMap<>();
    public final LinkedHashMap<String, ConfigHandle.ConfigProperty> crop = new LinkedHashMap<>();
    public final LinkedHashMap<String, ConfigHandle.ConfigProperty> transform = new LinkedHashMap<>();
    public final LinkedHashMap<String, ConfigHandle.ConfigProperty> watermark = new LinkedHashMap<>();
    public final LinkedHashMap<String, ConfigHandle.ConfigProperty> example = new LinkedHashMap<>();

    public final LinkedHashMap<String, LinkedHashMap<String, ConfigHandle.ConfigProperty>> set;

    public ConfigSet() {
        set = new LinkedHashMap<>();
        set.put (ConfigHandle.FILE, file);
        set.put (ConfigHandle.SIZE, size);
        set.put (ConfigHandle.CROP, crop);
        set.put (ConfigHandle.TRANSFORMATION, transform);
        set.put (ConfigHandle.WATERMARK, watermark);
        set.put (ConfigHandle.EXAMPLE, example);
    }

    public ConfigSet(ConfigHandle handle) {
        this();
        fromConfigHandle(handle);
    }

    public void fromConfigHandle(ConfigHandle handle) {

        file.put(ConfigHandle.FILE_QUALITY, handle.getOrigin(ConfigHandle.FILE_QUALITY, Float.class));

        size.put(ConfigHandle.WIDTH, handle.getOrigin(ConfigHandle.WIDTH, Long.class));
        size.put(ConfigHandle.HEIGHT, handle.getOrigin(ConfigHandle.HEIGHT, Long.class));
        size.put(ConfigHandle.ASPECT_RATIO, handle.getOrigin(ConfigHandle.ASPECT_RATIO, String.class));

        crop.put(ConfigHandle.CROP_VERTICAL, handle.getOrigin(ConfigHandle.CROP_VERTICAL, Float.class));
        crop.put(ConfigHandle.CROP_HORIZONTAL, handle.getOrigin(ConfigHandle.CROP_HORIZONTAL, Float.class));
        crop.put(ConfigHandle.CROP_SCALE, handle.getOrigin(ConfigHandle.CROP_SCALE, Float.class));

        transform.put(ConfigHandle.TRANSFORMATION_BLUR_FACTOR,
                handle.getOrigin(ConfigHandle.TRANSFORMATION_BLUR_FACTOR, String.class));

        watermark.put(ConfigHandle.WATERMARK_TEXT, handle.getOrigin(ConfigHandle.WATERMARK_TEXT, String.class));
        watermark.put(ConfigHandle.WATERMARK_FONT_FAMILY, handle.getOrigin(ConfigHandle.WATERMARK_FONT_FAMILY, String.class));
        watermark.put(ConfigHandle.WATERMARK_FONT_BOLD, handle.getOrigin(ConfigHandle.WATERMARK_FONT_BOLD, Boolean.class));
        watermark.put(ConfigHandle.WATERMARK_FONT_ITALIC, handle.getOrigin(ConfigHandle.WATERMARK_FONT_ITALIC, Boolean.class));
        watermark.put(ConfigHandle.WATERMARK_FONT_SIZE, handle.getOrigin(ConfigHandle.WATERMARK_FONT_SIZE, String.class));
        watermark.put(ConfigHandle.WATERMARK_POS_VERTICAL, handle.getOrigin(ConfigHandle.WATERMARK_POS_VERTICAL, Double.class));
        watermark.put(ConfigHandle.WATERMARK_POS_HORIZONTAL, handle.getOrigin(ConfigHandle.WATERMARK_POS_HORIZONTAL, Double.class));
        watermark.put(ConfigHandle.WATERMARK_COLOR, handle.getOrigin(ConfigHandle.WATERMARK_COLOR, String.class));
        watermark.put(ConfigHandle.WATERMARK_ALPHA, handle.getOrigin(ConfigHandle.WATERMARK_ALPHA, Float.class));

        example.put(ConfigHandle.EXAMPLE_IMAGE_PATH, handle.getOrigin(ConfigHandle.EXAMPLE_IMAGE_PATH, String.class));
    }
}
