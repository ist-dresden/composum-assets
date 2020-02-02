package com.composum.assets.commons.image.transform;

import com.composum.assets.commons.config.aspect.Watermark;
import com.composum.assets.commons.image.ImageTransformer;
import com.composum.assets.commons.image.RenditionTransformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Set;

/**
 * the SCALE transformer service based on the JDK Graphics algorithms
 */
@Component(
        immediate = true
)
public class GraphicsWatermarkTransformer implements ImageTransformer {

    public static final Set<String> OP_SET = Collections.singleton(OP_WATERMARK);

    @Nonnull
    @Override
    public Set<String> getOperations() {
        return OP_SET;
    }

    @Override
    public int rating(String operation, Object options) {
        return OP_WATERMARK.equalsIgnoreCase(operation) ? 5 : 0;
    }

    @Override
    public BufferedImage transform(RenditionTransformer service, BufferedImage image,
                                   String operation, Object options) {
        Watermark watermark = options instanceof Watermark
                ? (Watermark) options : new Watermark((ValueMap) options, true);
        if (watermark.isValid()) {

            String text = watermark.getText();
            Watermark.Font font = watermark.getFont();
            int width = image.getWidth();
            int height = image.getHeight();
            Graphics2D g2d = (Graphics2D) image.getGraphics();

            Float alpha = watermark.getAlpha();
            if (alpha != null) {
                AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC, alpha);
                g2d.setComposite(alphaChannel);
            }
            Color color = watermark.getColor();
            if (color != null) {
                g2d.setColor(watermark.getColor());
            }
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            FontMetrics fontMetrics;
            Rectangle2D rect = null;

            String fontSizeExp = font.getSize();
            if (StringUtils.isNotBlank(fontSizeExp)) {
                int fontStyle = (font.isBold() ? Font.BOLD : 0) | (font.isItalic() ? Font.ITALIC : 0);
                try {
                    int fontSize = Integer.parseInt(fontSizeExp);
                    g2d.setFont(new Font(font.getFamily(), fontStyle, fontSize));
                    fontMetrics = g2d.getFontMetrics();
                    rect = fontMetrics.getStringBounds(text, g2d);

                } catch (NumberFormatException nfex) {

                    float fontSizeWeight = Float.parseFloat(fontSizeExp);
                    for (int fontSize = 128; fontSize > 8; fontSize = (int) (fontSize * 0.75f)) {
                        g2d.setFont(new Font(font.getFamily(), fontStyle, fontSize));
                        fontMetrics = g2d.getFontMetrics();
                        rect = fontMetrics.getStringBounds(text, g2d);
                        if (rect.getWidth() < (int) (width * fontSizeWeight) &&
                                rect.getHeight() < (int) (height * fontSizeWeight)) {
                            break;
                        }
                    }
                }
            }

            Integer x = null;
            if (rect != null && watermark.getHorizontal() != null) {
                try {
                    int xPos = Integer.parseInt(watermark.getHorizontal());
                    x = xPos < 0
                            ? Math.round(width - (int) rect.getWidth()) + xPos
                            : xPos;
                } catch (NumberFormatException nfex) {
                    float xWeight = Float.parseFloat(watermark.getHorizontal());
                    x = Math.round((width - (int) rect.getWidth()) * xWeight);
                }
            }
            Integer y = null;
            if (rect != null && watermark.getVertical() != null) {
                try {
                    int yPos = Integer.parseInt(watermark.getVertical());
                    y = yPos < 0
                            ? Math.round(height - (int) rect.getHeight()) + yPos
                            : yPos;
                } catch (NumberFormatException nfex) {
                    float yWeight = Float.parseFloat(watermark.getVertical());
                    y = Math.round((height - (int) rect.getHeight()) * yWeight);
                }
                y += Math.round((int) rect.getHeight() * 0.8f);
            }
            if (x != null && y != null) {
                g2d.drawString(text, x, y);
            }

            g2d.dispose();
        }
        return image;
    }
}
