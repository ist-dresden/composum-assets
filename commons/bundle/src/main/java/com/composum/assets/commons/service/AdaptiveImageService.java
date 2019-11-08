/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.service;

import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.image.RenditionTransformer;
import org.apache.sling.api.resource.PersistenceException;

import javax.jcr.RepositoryException;
import java.io.IOException;

public interface AdaptiveImageService {

    AssetRendition getRendition(ImageAsset asset,
                                String variationKey, String renditionKey) throws RepositoryException;

    RenditionConfig getRenditionConfig(ImageAsset asset,
                                       String variationKey, String renditionKey);

    RenditionConfig findRenditionConfig(ImageAsset asset,
                                        String variationKey, String renditionKey);

    AssetRendition getOrCreateRendition(ImageAsset asset,
                                        String variationKey, String renditionKey)
            throws RepositoryException, IOException;

    void dropRenditions(String path,
                        String variationKey, String renditionKey)
            throws PersistenceException;

    RenditionTransformer getRenditionTransformer();
}
