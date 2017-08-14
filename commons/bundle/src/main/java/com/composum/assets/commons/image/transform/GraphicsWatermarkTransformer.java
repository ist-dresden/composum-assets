package com.composum.assets.commons.image.transform;

import com.composum.assets.commons.config.aspect.GenericAspect;
import com.composum.assets.commons.config.aspect.Watermark;
import com.composum.assets.commons.image.ImageTransformer;
import com.composum.assets.commons.image.RenditionTransformer;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * the SCALE transformer service based on the JDK Graphics algorithms
 */
@SuppressWarnings("deprecation")
@Component(configurationFactory = true, immediate = true)
@Service()
public class GraphicsWatermarkTransformer implements ImageTransformer {

    public static final Set<String> OP_SET = Collections.singleton(OP_WATERMARK);

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
        @SuppressWarnings("unchecked")
        Watermark watermark = options instanceof Watermark
                ? (Watermark) options : new Watermark(new GenericAspect((Map<String, Object>) options));
        if (watermark.isValid()) {

            String text = watermark.text;
            Watermark.Font font = watermark.font;
            int width = image.getWidth();
            int height = image.getHeight();
            Graphics2D g2d = (Graphics2D) image.getGraphics();

            AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, watermark.alpha);
            g2d.setComposite(alphaChannel);
            g2d.setColor(watermark.color);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            FontMetrics fontMetrics;
            Rectangle2D rect = null;

            int fontStyle = (font.bold ? Font.BOLD : 0) | (font.italic ? Font.ITALIC : 0);
            try {
                int fontSize = Integer.parseInt(font.size);
                g2d.setFont(new Font(font.family, fontStyle, fontSize));
                fontMetrics = g2d.getFontMetrics();
                rect = fontMetrics.getStringBounds(text, g2d);

            } catch (NumberFormatException nfex) {

                float fontSizeWeight = Float.parseFloat(font.size);
                for (int fontSize = 128; fontSize > 8; fontSize = (int) (fontSize * 0.75f)) {
                    g2d.setFont(new Font(font.family, fontStyle, fontSize));
                    fontMetrics = g2d.getFontMetrics();
                    rect = fontMetrics.getStringBounds(text, g2d);
                    if (rect.getWidth() < (int) (width * fontSizeWeight) &&
                            rect.getHeight() < (int) (height * fontSizeWeight)) {
                        break;
                    }
                }
            }

            int x;
            try {
                int xPos = Integer.parseInt(watermark.horizontal);
                x = xPos < 0
                        ? Math.round(width - (int) rect.getWidth()) + xPos
                        : xPos;
            } catch (NumberFormatException nfex) {
                float xWeight = Float.parseFloat(watermark.horizontal);
                x = Math.round((width - (int) rect.getWidth()) * xWeight);
            }
            int y;
            try {
                int yPos = Integer.parseInt(watermark.vertical);
                y = yPos < 0
                        ? Math.round(height - (int) rect.getHeight()) + yPos
                        : yPos;
            } catch (NumberFormatException nfex) {
                float yWeight = Float.parseFloat(watermark.vertical);
                y = Math.round((height - (int) rect.getHeight()) * yWeight);
            }
            y += Math.round((int) rect.getHeight() * 0.8f);
            g2d.drawString(text, x, y);
            g2d.dispose();
        }
        return image;
    }
}