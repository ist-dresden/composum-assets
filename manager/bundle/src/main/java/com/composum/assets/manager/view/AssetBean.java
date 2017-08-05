package com.composum.assets.manager.view;

import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import org.apache.sling.api.resource.Resource;

public class AssetBean extends AbstractServletBean {

    public static final ResourceFilter FILTER = new ResourceFilter.PrimaryTypeFilter(
            new StringFilter.WhiteList("^cpa:Asset$"));

    public AssetBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public AssetBean(BeanContext context) {
        super(context);
    }

    public AssetBean() {
        super();
    }
}
