/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.service;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.io.InputStream;

public interface AssetsService {

    /**
     * Updates or creates the original rendition for a given variation.
     */
    @Nonnull
    Resource uploadImageAsset(@Nonnull BeanContext context,
                              @Nonnull String assetOrParentPath, @Nullable String assetName,
                              @Nonnull String variation, @Nonnull InputStream imageData)
            throws Exception;

    /**
     * Updates the original rendition for a given variation.
     */
    @Nonnull
    Resource createImageAsset(@Nonnull BeanContext context,
                              @Nonnull String parentPath, @Nonnull String name,
                              @Nonnull String variation, @Nonnull InputStream imageData)
            throws Exception;

    /**
     * Creates the original rendition for a given variation.
     */
    void changeImageAsset(@Nonnull BeanContext context, @Nonnull Resource assetResource,
                          @Nonnull String variation, @Nonnull InputStream imageData)
            throws Exception;

    void transformToImageAsset(@Nonnull BeanContext context, @Nonnull Resource imageResource)
            throws PersistenceException, RepositoryException;

    void transformToSimpleImage(@Nonnull BeanContext context, @Nonnull Resource assetResource)
            throws PersistenceException, RepositoryException;

    void deleteAsset(@Nullable Resource assetResource)
            throws PersistenceException;

    void setDefaultConfiguration(@Nonnull BeanContext context, @Nonnull Resource configResource, boolean commit)
            throws PersistenceException;

    Resource getOrCreateConfiguration(@Nonnull BeanContext context, @Nonnull String path, boolean commit)
            throws PersistenceException;

    Resource copyConfigNode(@Nonnull BeanContext context, @Nonnull Resource parent,
                            @Nonnull Resource template, boolean commit)
            throws PersistenceException;

    @Nullable
    Resource createConfigNode(@Nonnull BeanContext context,
                              @Nonnull Resource parent, @Nonnull String name, boolean commit)
            throws PersistenceException;

    void deleteConfigNode(@Nonnull BeanContext context, @Nullable Resource configNode, boolean commit)
            throws PersistenceException;
}
