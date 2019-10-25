/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.service;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import javax.jcr.RepositoryException;
import java.io.InputStream;

public interface AssetsService {

    /** Updates or creates the original rendition for a given variation. */
    void uploadImageAsset(BeanContext context, String parentPath, String assetName,
                          String variation, InputStream imageData)
            throws Exception;

    /** Updates the original rendition for a given variation. */
    Resource createImageAsset(BeanContext context, String parentPath, String name,
                              String variation, InputStream imageData)
            throws Exception;

    /** Creates the original rendition for a given variation. */
    void changeImageAsset(BeanContext context, Resource assetResource,
                          String variation, InputStream imageData)
            throws Exception;

    void transformToImageAsset(BeanContext context, Resource imageResource)
            throws PersistenceException, RepositoryException;

    void transformToSimpleImage(BeanContext context, Resource assetResource)
            throws PersistenceException, RepositoryException;

    void deleteAsset(Resource assetResource)
            throws PersistenceException;

    void setDefaultConfiguration(BeanContext context, Resource configResource, boolean commit)
            throws PersistenceException;

    Resource getOrCreateConfiguration(BeanContext context, String path, boolean commit)
            throws PersistenceException;

    Resource copyConfigNode(BeanContext context, Resource parent, Resource template, boolean commit)
            throws PersistenceException;

    Resource createConfigNode(BeanContext context, Resource parent, String name, boolean commit)
            throws PersistenceException;

    void deleteConfigNode(BeanContext context, Resource configNode, boolean commit)
            throws PersistenceException;
}
