/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.image;

import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.transform.Blur;
import com.composum.assets.commons.config.aspect.Crop;
import com.composum.assets.commons.config.aspect.Size;
import com.composum.assets.commons.config.aspect.Watermark;
import com.composum.assets.commons.handle.AssetRendition;
import org.imgscalr.Scalr;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;

/**
 * Created by rw on 19.02.16.
 */
public class DefaultRenditionTransformer implements RenditionTransformer {

    public static final float RATIO_SIMILARITY = 0.05f;

    public static final Crop ASPECT_RATIO_CROP = new Crop();

    public BufferedImage transform(AssetRendition rendition, BufferedImage image, BuilderContext context) {
        RenditionConfig config = rendition.getConfig();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        Size size = config.getSize();
        Integer renditionWidth = size.width;
        Integer renditionHeight = size.height;
        Float aspectRatio = size.aspectRatio;
        if (aspectRatio == null) {
            aspectRatio = (float) imageWidth / (float) imageHeight;
        }
        if (renditionHeight == null && renditionWidth != null) {
            renditionHeight = Math.round((float) renditionWidth / aspectRatio);
        }
        if (renditionWidth == null && renditionHeight != null) {
            renditionWidth = Math.round((float) renditionHeight * aspectRatio);
        }
        if (renditionWidth != null && renditionHeight != null &&
                (renditionWidth < imageWidth || renditionHeight < imageHeight)) {
            Crop crop = config.getCrop();
            float imageRatio = (float) imageWidth / (float) imageHeight;
            if (crop.isDefault()) {
                float ratioAbberation = imageRatio - aspectRatio;
                if (ratioAbberation < -RATIO_SIMILARITY) {
                    image = getCroppedImage(image, renditionWidth, renditionHeight, ASPECT_RATIO_CROP);
                } else if (ratioAbberation > RATIO_SIMILARITY) {
                    image = getCroppedImage(image, renditionWidth, renditionHeight, ASPECT_RATIO_CROP);
                } else {
                    image = getScaledImage(image, renditionWidth, renditionHeight);
                }
            } else {
                image = getCroppedImage(image, renditionWidth, renditionHeight, crop);
            }
        }
        Blur blur = config.getBlur();
        if (blur.isValid()) {
            image = getBlurredImage(image, blur);
        }
        Watermark watermark = config.getWatermark();
        if (watermark.isValid()) {
            image = getMarkedImage(image, watermark);
        }
        return image;
    }

    protected BufferedImage getScaledImage(BufferedImage image,
                                           int width, int height) {
        if (width < image.getWidth() || height < image.getHeight()) {
            image = getScaledByScalr(image, width, height);
        }
        return image;
    }

    protected BufferedImage getScaledByScalr(BufferedImage image,
                                             int width, int height) {
        image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, width, height);
        return image;
    }

    protected BufferedImage getScaledByGraphics(BufferedImage image,
                                                int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();
        return scaled;
    }

    protected BufferedImage getCroppedImage(BufferedImage image,
                                            int width, int height,
                                            Crop crop) {
        if (crop.scale != null && crop.scale > 0f) {
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            float aspectRatio = (float) imageWidth / (float) imageHeight;
            float requestedRatio = (float) width / (float) height;
            if (crop.scale < 1.0f) {
                // scale to factor
                if (aspectRatio < requestedRatio) {
                    imageWidth = Math.round((float) imageWidth * crop.scale);
                    imageHeight = Math.round((float) imageWidth / aspectRatio);
                } else {
                    imageHeight = Math.round((float) imageHeight * crop.scale);
                    imageWidth = Math.round((float) imageHeight * aspectRatio);
                }
                image = getScaledImage(image, imageWidth, imageHeight);
            } else {
                // scale to target size if scale = '1'
                if (aspectRatio < requestedRatio) {
                    imageWidth = width;
                    imageHeight = Math.round((float) imageWidth / aspectRatio);
                } else {
                    imageHeight = height;
                    imageWidth = Math.round((float) imageHeight * aspectRatio);
                }
                image = getScaledImage(image, imageWidth, imageHeight);
            }
        }
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        width = Math.min(width, imageWidth);
        height = Math.min(height, imageHeight);
        int x = Math.round((imageWidth - width) * crop.horizontal);
        int y = Math.round((imageHeight - height) * crop.vertical);
        BufferedImage cropped = image.getSubimage(x, y, width, height);
        return cropped;
    }

    protected BufferedImage getMarkedImage(BufferedImage image, Watermark watermark) {

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

            int fontStyle = 0 | (font.bold ? Font.BOLD : 0) | (font.italic ? Font.ITALIC : 0);
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

            int x = 0;
            try {
                int xPos = Integer.parseInt(watermark.horizontal);
                x = xPos < 0
                        ? Math.round(width - (int) rect.getWidth()) + xPos
                        : xPos;
            } catch (NumberFormatException nfex) {
                float xWeight = Float.parseFloat(watermark.horizontal);
                x = Math.round((width - (int) rect.getWidth()) * xWeight);
            }
            int y = 0;
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

    protected BufferedImage getBlurredImage(BufferedImage image, Blur blur) {
        ConvolveOp filter = getGaussianBlurFilter(blur.factor);
        for (int i = 0; i < blur.factor; i++) {
            image = filter.filter(image, null);
        }
        return image;
    }

    public static ConvolveOp getGaussianBlurFilter(int blurFactor) {

        int square = blurFactor * blurFactor;
        float value = 1.0f / (float) square;
        float kernelData[] = new float[square];
        Arrays.fill(kernelData, value);

        Kernel kernel = new Kernel(blurFactor, blurFactor, kernelData);

        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    }
}
