/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.service;

import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.platform.staging.search.AbstractSearchPlugin;
import com.composum.sling.platform.staging.search.SearchPlugin;
import org.apache.jackrabbit.JcrConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;

@Component(
        service = SearchPlugin.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Assets Search Plugin",
                Constants.SERVICE_RANKING + ":Integer=100"
        },
        immediate = true
)
public class AssetsSearchPlugin extends AbstractSearchPlugin {

    public static final String SELECTOR_ASSET = "asset";

    /**
     * target resource filter matching file resources.
     */
    public static final ResourceFilter TARGET_FILTER = new ResourceFilter.PrimaryTypeFilter(
            new StringFilter.WhiteList("^" + JcrConstants.NT_FILE + "$"));

    @Override
    public int rating(@Nonnull String selectors) {
        return selectors.startsWith(SELECTOR_ASSET) ? 9 : 0;

    }

    @Nonnull
    @Override
    protected ResourceFilter getTargetFilter() {
        return TARGET_FILTER;
    }
}
