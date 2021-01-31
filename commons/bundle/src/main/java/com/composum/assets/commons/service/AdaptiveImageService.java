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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.OutputStream;

public interface AdaptiveImageService {

    @Nullable
    AssetRendition getRendition(@Nonnull ImageAsset asset,
                                @Nonnull String variationKey, @Nonnull String renditionKey)
            throws RepositoryException;

    @Nullable
    RenditionConfig getRenditionConfig(@Nonnull ImageAsset asset,
                                       @Nonnull String variationKey, @Nonnull String renditionKey);

    @Nullable
    RenditionConfig findRenditionConfig(@Nonnull ImageAsset asset,
                                        @Nonnull String variationKey, @Nonnull String renditionKey);

    void volatileRendition(@Nonnull AssetRendition rendition, @Nonnull OutputStream outputStream)
            throws RepositoryException, IOException;

    AssetRendition getOrCreateRendition(@Nonnull ImageAsset asset,
                                        @Nonnull String variationKey, @Nonnull String renditionKey)
            throws RepositoryException, IOException;

    void dropRenditions(@Nonnull String path,
                        @Nonnull String variationKey, @Nullable String renditionKey)
            throws PersistenceException;

    RenditionTransformer getRenditionTransformer();
}
