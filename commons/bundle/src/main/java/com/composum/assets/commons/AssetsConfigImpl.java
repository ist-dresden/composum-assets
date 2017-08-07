/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons;

import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.mapping.jcr.ResourceFilterMapping;
import com.composum.sling.core.servlet.AbstractServiceServlet;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * The configuration service for all servlets in the core bundle.
 */
@Component(
        label = "Composum Assets - Configuration",
        description = "the configuration service for all servlets in the nodes bundles",
        immediate = true,
        metatype = true
)
@Service
public class AssetsConfigImpl implements AssetsConfiguration {

    @Property(
            name = ASSETS_CATEGORIES_KEY,
            label = "Assets Categories",
            description = "the list of categories to determine the views in the assets manager",
            value = {
                    "assets"
            }
    )
    private String[] assetsCategories;

    @Property(
            name = ASSET_NODE_FILTER_KEY,
            label = "Asset Resource Filter",
            description = "the filter configuration to set the scope to the content assets",
            value = "or{PrimaryType(+'^cpa:Asset$'),and{PrimaryType(+'^nt:file$'),MimeType(+'^(image|video)/.*$')}}"
    )
    private ResourceFilter assetNodeFilter;

    @Property(
            name = ASSET_PATH_FILTER_KEY,
            label = "Asset Path Filter",
            description = "the path selection for the assets tree",
            value = "Path(+'^/$,^/(content|sites|test)(/.*)?$')"
    )
    private ResourceFilter assetPathFilter;

    @Property(
            name = DEFAULT_NODE_FILTER_KEY,
            label = "The default Node Filter",
            description = "the filter configuration to filter out system nodes",
            value = "and{Name(-'^rep:(repo)?[Pp]olicy$'),Path(-'^/bin(/.*)?$,^/services(/.*)?$,^/servlet(/.*)?$,^/(jcr:)?system(/.*)?$')}"
    )
    private ResourceFilter defaultNodeFilter;

    @Property(
            name = TREE_INTERMEDIATE_FILTER_KEY,
            label = "Tree Intermediate (Folder) Filter",
            description = "the filter configuration to determine all intermediate nodes in the tree view",
            value = "Folder()"
    )
    private ResourceFilter treeIntermediateFilter;

    private Map<String, Boolean> enabledServlets;

    @Override
    public boolean isEnabled(AbstractServiceServlet servlet) {
        Boolean result = enabledServlets.get(servlet.getClass().getSimpleName());
        return result != null ? result : false;
    }

    public String[] getAssetsCategories() {
        return assetsCategories;
    }

    @Override
    public ResourceFilter getAssetNodeFilter() {
        return assetNodeFilter;
    }

    @Override
    public ResourceFilter getAssetPathFilter() {
        return assetPathFilter;
    }

    @Override
    public ResourceFilter getDefaultNodeFilter() {
        return defaultNodeFilter;
    }

    @Override
    public ResourceFilter getTreeIntermediateFilter() {
        return treeIntermediateFilter;
    }

    public Dictionary getProperties() {
        return properties;
    }

    protected Dictionary properties;

    @Activate
    @Modified
    protected void activate(ComponentContext context) {
        this.properties = context.getProperties();
        assetsCategories = PropertiesUtil.toStringArray(properties.get(ASSETS_CATEGORIES_KEY));
        assetNodeFilter = ResourceFilterMapping.fromString(
                (String) properties.get(ASSET_NODE_FILTER_KEY));
        assetPathFilter = ResourceFilterMapping.fromString(
                (String) properties.get(ASSET_PATH_FILTER_KEY));
        defaultNodeFilter = ResourceFilterMapping.fromString(
                (String) properties.get(DEFAULT_NODE_FILTER_KEY));
        treeIntermediateFilter = ResourceFilterMapping.fromString(
                (String) properties.get(TREE_INTERMEDIATE_FILTER_KEY));
        enabledServlets = new HashMap<>();
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        this.properties = null;
    }
}
