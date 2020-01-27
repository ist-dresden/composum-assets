package com.composum.assets.commons.config.aspect;

import com.composum.assets.commons.config.ConfigHandle;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;

public class Watermark {

    public static final String NONE = "none";

    public static final String DEFAULT_VERTICAL = "0.5";
    public static final String DEFAULT_HORIZONTAL = "0.5";
    public static final String DEFAULT_COLOR = "ffffff";
    public static final float DEFAULT_ALPHA = 0.25f;

    public static class Font {

        public static final String DEFAULT_FAMILY = "sans-serif";
        public static final boolean DEFAULT_BOLD = true;
        public static final boolean DEFAULT_ITALIC = false;
        public static final String DEFAULT_SIZE = "18";

        protected final String family;
        protected final Boolean bold;
        protected final Boolean italic;
        protected final String size;

        public Font(ConfigHandle config) {
            this.family = config.getInherited(ConfigHandle.WATERMARK_FONT_FAMILY, DEFAULT_FAMILY);
            this.bold = config.getInherited(ConfigHandle.WATERMARK_FONT_BOLD, DEFAULT_BOLD);
            this.italic = config.getInherited(ConfigHandle.WATERMARK_FONT_ITALIC, DEFAULT_ITALIC);
            this.size = config.getInherited(ConfigHandle.WATERMARK_FONT_SIZE, DEFAULT_SIZE);
        }

        public Font(ValueMap values, boolean useDefault) {
            this.family = useDefault
                    ? values.get(ConfigHandle.WATERMARK_FONT_FAMILY, DEFAULT_FAMILY)
                    : values.get(ConfigHandle.WATERMARK_FONT_FAMILY, String.class);
            this.bold = useDefault
                    ? values.get(ConfigHandle.WATERMARK_FONT_BOLD, DEFAULT_BOLD)
                    : values.get(ConfigHandle.WATERMARK_FONT_BOLD, Boolean.class);
            this.italic = useDefault
                    ? values.get(ConfigHandle.WATERMARK_FONT_ITALIC, DEFAULT_ITALIC)
                    : values.get(ConfigHandle.WATERMARK_FONT_ITALIC, Boolean.class);
            this.size = useDefault
                    ? values.get(ConfigHandle.WATERMARK_FONT_SIZE, DEFAULT_SIZE)
                    : values.get(ConfigHandle.WATERMARK_FONT_SIZE, String.class);
        }

        @Nullable
        public String getFamily() {
            return family;
        }

        public boolean isBold() {
            return bold != null && bold;
        }

        @Nullable
        public Boolean getBold() {
            return bold;
        }

        public boolean isItalic() {
            return italic != null && italic;
        }

        @Nullable
        public Boolean getItalic() {
            return italic;
        }

        @Nullable
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

    protected final String text;
    protected final Font font;
    protected final String vertical;
    protected final String horizontal;
    protected final Color color;
    protected final Float alpha;

    public Watermark(ConfigHandle config) {
        this.text = config.getInherited(ConfigHandle.WATERMARK_TEXT, "");
        this.font = new Font(config);
        this.vertical = config.getInherited(ConfigHandle.WATERMARK_POS_VERTICAL, DEFAULT_VERTICAL);
        this.horizontal = config.getInherited(ConfigHandle.WATERMARK_POS_HORIZONTAL, DEFAULT_HORIZONTAL);
        this.color = getColor(config.getInherited(ConfigHandle.WATERMARK_COLOR, DEFAULT_COLOR));
        this.alpha = config.getInherited(ConfigHandle.WATERMARK_ALPHA, DEFAULT_ALPHA);
    }

    /**
     * @param values     a simple value map (can be a ValueMap of a resource)
     * @param useDefault if 'false' all attributes can be 'null' (used on config editing)
     */
    public Watermark(ValueMap values, boolean useDefault) {
        this.text = useDefault
                ? values.get(ConfigHandle.WATERMARK_TEXT, "")
                : values.get(ConfigHandle.WATERMARK_TEXT, String.class);
        this.font = new Font(values, useDefault);
        this.vertical = useDefault
                ? values.get(ConfigHandle.WATERMARK_POS_VERTICAL, DEFAULT_VERTICAL)
                : values.get(ConfigHandle.WATERMARK_POS_VERTICAL, String.class);
        this.horizontal = useDefault
                ? values.get(ConfigHandle.WATERMARK_POS_HORIZONTAL, DEFAULT_HORIZONTAL)
                : values.get(ConfigHandle.WATERMARK_POS_HORIZONTAL, String.class);
        this.color = getColor(useDefault
                ? values.get(ConfigHandle.WATERMARK_COLOR, DEFAULT_COLOR)
                : values.get(ConfigHandle.WATERMARK_COLOR, String.class));
        this.alpha = useDefault
                ? values.get(ConfigHandle.WATERMARK_ALPHA, DEFAULT_ALPHA)
                : values.get(ConfigHandle.WATERMARK_ALPHA, Float.class);
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(text) && !NONE.equals(text);
    }

    @Nullable
    public String getText() {
        return text;
    }

    @Nonnull
    public Font getFont() {
        return font;
    }

    @Nullable
    public String getVertical() {
        return vertical;
    }

    @Nullable
    public String getHorizontal() {
        return horizontal;
    }

    @Nullable
    public Color getColor() {
        return color;
    }

    @Nonnull
    public String getColorCode() {
        Color color = getColor();
        return color != null ? '#' + Integer.toHexString(color.getRGB() & 0x00FFFFFF).toUpperCase() : "";
    }

    @Nullable
    public Float getAlpha() {
        return alpha;
    }

    @Nullable
    public String getAlphaStr() {
        return alpha != null ? alpha.toString() : "";
    }

    @Nullable
    public Color getColor(@Nullable String value) {
        Color result = null;
        if (value != null) {
            Integer colorValue = null;
            try {
                value = value.replace('#', ' ').trim();
                colorValue = Integer.parseInt(value, 16);
            } catch (NumberFormatException ignored) {
            }
            if (colorValue == null) {
                colorValue = Integer.parseInt(DEFAULT_COLOR, 16);
            }
            int i = colorValue;
            result = new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
        }
        return result;
    }
}
