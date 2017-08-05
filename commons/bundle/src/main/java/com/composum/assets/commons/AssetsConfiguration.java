/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons;

import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.AbstractServiceServlet;

import java.util.Dictionary;

/**
 * The configuration service for all servlets in the core bundle.
 */
public interface AssetsConfiguration {

    String ASSETS_CATEGORIES_KEY = "assets.categories";

    String ASSET_NODE_FILTER_KEY = "assets.filter.node";
    String ASSET_PATH_FILTER_KEY = "assets.filter.path";
    String DEFAULT_NODE_FILTER_KEY = "node.default.filter";
    String TREE_INTERMEDIATE_FILTER_KEY = "tree.intermediate.filter";

    String[] getAssetsCategories();

    boolean isEnabled(AbstractServiceServlet servlet);

    ResourceFilter getAssetNodeFilter();

    ResourceFilter getAssetPathFilter();

    ResourceFilter getDefaultNodeFilter();

    ResourceFilter getTreeIntermediateFilter();

    Dictionary getProperties();
}
