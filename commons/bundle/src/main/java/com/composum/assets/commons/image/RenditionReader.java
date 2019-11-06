/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.image;

import com.composum.assets.commons.handle.AssetRendition;

import javax.imageio.ImageIO;
import javax.jcr.RepositoryException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class RenditionReader {

    public BufferedImage readImage(AssetRendition rendition, BuilderContext context)
            throws IOException, RepositoryException {
        try (InputStream stream = rendition.getStream()) {
            return ImageIO.read(stream);
        }
    }
}
