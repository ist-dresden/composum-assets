package com.composum.assets.commons.config.aspect;

import com.composum.assets.commons.config.ConfigHandle;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;

public class Watermark {

    public static final String DEFAULT_VERTICAL = "0.5";
    public static final String DEFAULT_HORIZONTAL = "0.5";
    public static final String DEFAULT_COLOR = "ffffff";
    public static final float DEFAULT_ALPHA = 0.25f;

    public static class Font {

        public static final String DEFAULT_FAMILY = "sans-serif";
        public static final boolean DEFAULT_BOLD = true;
        public static final boolean DEFAULT_ITALIC = false;
        public static final String DEFAULT_SIZE = "18";

        public final String family;
        public final boolean bold;
        public final boolean italic;
        public final String size;

        public Font(ConfigHandle config) {
            this.family = config.getInherited(ConfigHandle.WATERMARK_FONT_FAMILY, DEFAULT_FAMILY);
            this.bold = config.getInherited(ConfigHandle.WATERMARK_FONT_BOLD, DEFAULT_BOLD);
            this.italic = config.getInherited(ConfigHandle.WATERMARK_FONT_ITALIC, DEFAULT_ITALIC);
            this.size = config.getInherited(ConfigHandle.WATERMARK_FONT_SIZE, DEFAULT_SIZE);
        }

        public String getFamily() {
            return family;
        }

        public boolean isBold() {
            return bold;
        }

        public boolean isItalic() {
            return italic;
        }

        public String getSize() {
            return size;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(family).append(':').append(size);
            if (bold || italic) {
                builder.append(" (");
                if (bold) {
                    builder.append('B');
                }
                if (italic) {
                    builder.append('I');
                }
                builder.append(')');
            }
            return builder.toString();
        }
    }

    public final String text;
    public final Font font;
    public final String vertical;
    public final String horizontal;
    public final Color color;
    public final Float alpha;

    public Watermark(ConfigHandle config) {
        this.text = config.getInherited(ConfigHandle.WATERMARK_TEXT, "");
        this.font = new Font(config);
        this.vertical = config.getInherited(ConfigHandle.WATERMARK_POS_VERTICAL, DEFAULT_VERTICAL);
        this.horizontal = config.getInherited(ConfigHandle.WATERMARK_POS_HORIZONTAL, DEFAULT_HORIZONTAL);
        this.color = getColor(config, ConfigHandle.WATERMARK_COLOR, DEFAULT_COLOR);
        this.alpha = config.getInherited(ConfigHandle.WATERMARK_ALPHA, DEFAULT_ALPHA);
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(text);
    }

    public String getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    public String getVertical() {
        return vertical;
    }

    public String getHorizontal() {
        return horizontal;
    }

    public Color getColor() {
        return color;
    }

    public String getColorCode() {
        return '#' + Integer.toHexString(this.color.getRGB() & 0x00FFFFFF).toUpperCase();
    }

    public String getAlpha() {
        return alpha != null ? alpha.toString() : "";
    }

    public Color getColor(ConfigHandle config, String key, String defaultValue) {
        Color result = null;
        Integer colorValue = null;
        String value = config.getInherited(key, "");
        if (StringUtils.isNotBlank(value)) {
            try {
                value = value.replace('#', ' ').trim();
                colorValue = Integer.parseInt(value, 16);
            } catch (NumberFormatException ignored) {
            }
        }
        if (colorValue == null && StringUtils.isNotBlank(defaultValue)) {
            colorValue = Integer.parseInt(defaultValue, 16);
        }
        if (colorValue != null) {
            int i = colorValue;
            result = new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
        }
        return result;
    }
}
