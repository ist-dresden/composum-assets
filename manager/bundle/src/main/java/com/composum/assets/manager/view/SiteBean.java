package com.composum.assets.manager.view;

import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

/**
 * Created by rw on 20.02.16.
 */
public class SiteBean extends AbstractServletBean {

    public SiteBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public SiteBean(BeanContext context) {
        super(context);
    }

    public SiteBean() {
        super();
    }
}
