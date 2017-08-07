/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.image;

import com.composum.assets.commons.handle.AssetRendition;

import java.awt.image.BufferedImage;

/**
 * Created by rw on 19.02.16.
 */
public interface RenditionTransformer {

    BufferedImage transform(AssetRendition rendition, BufferedImage image, BuilderContext context);
}
