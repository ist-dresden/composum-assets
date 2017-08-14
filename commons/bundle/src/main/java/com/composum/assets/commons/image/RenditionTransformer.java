/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.image;

import com.composum.assets.commons.config.aspect.Crop;
import com.composum.assets.commons.handle.AssetRendition;

import java.awt.image.BufferedImage;

/**
 */
public interface RenditionTransformer {

    BufferedImage transform(AssetRendition rendition, BufferedImage image, BuilderContext context);

    BufferedImage transform(BufferedImage image, String operation, Object options);

    BufferedImage scale(BufferedImage image, int width, int height);

    BufferedImage crop(BufferedImage image, int width, int height, Crop options);
}
