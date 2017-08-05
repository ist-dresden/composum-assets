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
import org.apache.sling.api.resource.LoginException;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.Map;

public interface AdaptiveImageService {

    AssetRendition getRendition(ImageAsset asset,
                                String variationKey, String renditionKey) throws RepositoryException;

    RenditionConfig getRenditionConfig(ImageAsset asset,
                                       String variationKey, String renditionKey);

    RenditionConfig findRenditionConfig(ImageAsset asset,
                                        String variationKey, String renditionKey);

    AssetRendition getOrCreateRendition(ImageAsset asset,
                                        String variationKey, String renditionKey)
            throws LoginException, RepositoryException, IOException;

    void dropRenditions(String path,
                        String variationKey, String renditionKey)
            throws Exception;

    Map<String, RenditionTransformer> getTransformers();
}
