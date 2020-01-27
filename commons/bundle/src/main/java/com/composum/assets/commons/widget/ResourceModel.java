package com.composum.assets.commons.widget;

import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public class ResourceModel extends AbstractServletBean {

    public ResourceModel() {
        super();
    }

    public ResourceModel(BeanContext context, Resource resource) {
        super(context, resource);
    }
}
