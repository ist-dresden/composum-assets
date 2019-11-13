/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.service;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;

/**
 * Service that deals with the metadata for images / videos which are automatically saved on
 * {@value com.composum.assets.commons.AssetsConstants#PATH_META}.*
 */
public interface MetaPropertiesService {

    /**
     * Update the {@value com.composum.assets.commons.AssetsConstants#PATH_META} node of all subresources of
     * {resource}.
     *
     * @param resolver a resource resolver to write data with
     * @param resource the resource to look at - we look for files at {resource} and in subfolders.
     * @return true if the service found a strategy to deal with the given resource.
     */
    boolean adjustMetaProperties(@Nonnull ResourceResolver resolver, @Nonnull Resource resource);

}
