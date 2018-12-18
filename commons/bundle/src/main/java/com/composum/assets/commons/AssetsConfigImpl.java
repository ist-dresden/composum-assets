/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons;

import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.mapping.jcr.ResourceFilterMapping;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.annotation.Nonnull;

/**
 * The configuration service for all servlets in the Assets module.
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Assets Module Configuration"
        }
)
@Designate(ocd = AssetsConfigImpl.Configuration.class)
public class AssetsConfigImpl implements AssetsConfiguration {

    @ObjectClassDefinition(
            name = "Composum Assets Module Configuration"
    )
    public @interface Configuration {

        @AttributeDefinition(
                description = "the list of categories to determine the views in the assets manager"
        )
        String[] assetsCategories() default {
                "assets"
        };

        @AttributeDefinition(
                description = "the filter configuration to set the scope to the content assets"
        )
        String assetNodeFilterRule() default "or{PrimaryType(+'^cpa:Asset$'),and{PrimaryType(+'^nt:file$'),MimeType(+'^(image|video)/.*$')}})";

        @AttributeDefinition(
                description = "the path selection for the assets tree"
        )
        String assetPathFilterRule() default "Path(+'^/$,^/(content|test)(/.*)?$')";

        @AttributeDefinition(
                description = "the filter configuration to filter out system nodes"
        )
        String defaultNodeFilterRule() default "and{Name(-'^rep:(repo)?[Pp]olicy$'),Path(-'^/bin(/.*)?$,^/services(/.*)?$,^/servlet(/.*)?$,^/(jcr:)?system(/.*)?$')}";

        @AttributeDefinition(
                description = "the filter configuration to determine all intermediate nodes in the tree view"
        )
        String treeIntermediateFilterRule() default "Folder()";
    }

    private ResourceFilter assetNodeFilter;
    private ResourceFilter assetPathFilter;
    private ResourceFilter defaultNodeFilter;
    private ResourceFilter treeIntermediateFilter;

    protected Configuration config;

    @Nonnull
    public String[] getAssetsCategories() {
        return config != null ? config.assetsCategories() : new String[0];
    }

    @Nonnull
    @Override
    public ResourceFilter getAssetNodeFilter() {
        return assetNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getAssetPathFilter() {
        return assetPathFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getDefaultNodeFilter() {
        return defaultNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getTreeIntermediateFilter() {
        return treeIntermediateFilter;
    }

    @Activate
    @Modified
    protected void activate(Configuration config) {
        this.config = config;
        assetNodeFilter = ResourceFilterMapping.fromString(config.assetNodeFilterRule());
        assetPathFilter = ResourceFilterMapping.fromString(config.assetPathFilterRule());
        defaultNodeFilter = ResourceFilterMapping.fromString(config.defaultNodeFilterRule());
        treeIntermediateFilter = ResourceFilterMapping.fromString(config.treeIntermediateFilterRule());
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        this.config = null;
    }
}
