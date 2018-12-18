/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons;

import com.composum.sling.core.filter.ResourceFilter;

import javax.annotation.Nonnull;

/**
 * The configuration service for all servlets in the core bundle.
 */
public interface AssetsConfiguration {

    @Nonnull
    String[] getAssetsCategories();

    @Nonnull
    ResourceFilter getAssetNodeFilter();

    @Nonnull
    ResourceFilter getAssetPathFilter();

    @Nonnull
    ResourceFilter getDefaultNodeFilter();

    @Nonnull
    ResourceFilter getTreeIntermediateFilter();
}
