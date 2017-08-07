/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.image;

import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.handle.AssetRendition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Created by rw on 19.02.16.
 */
public class RenditionWriter {

    private static final Logger LOG = LoggerFactory.getLogger(RenditionWriter.class);

    public InputStream writeImage(final AssetRendition rendition, final BufferedImage image, BuilderContext context)
            throws IOException {

        RenditionConfig config = rendition.getConfig();
        String mimeType = rendition.getMimeType();
        Double quality = null;

        final ImageWriter imageWriter = ImageIO.getImageWritersByMIMEType(mimeType).next();
        final ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        if (imageWriteParam.canWriteCompressed() && (quality = config.getFile().quality) != null) {
            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            imageWriteParam.setCompressionQuality(quality.floatValue());
        }

        final PipedOutputStream outputStream = new PipedOutputStream();
        InputStream result = new PipedInputStream(outputStream);
        final ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);

        context.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    imageWriter.setOutput(imageOutputStream);
                    imageWriter.write(null, new IIOImage(image, null, null), imageWriteParam);
                    imageOutputStream.flush();
                    outputStream.flush();
                    imageWriter.dispose();
                    imageOutputStream.close();
                    outputStream.close();
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        });

        return result;
    }
}
