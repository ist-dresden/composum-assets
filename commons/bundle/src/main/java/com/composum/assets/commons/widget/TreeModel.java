package com.composum.assets.commons.widget;

import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public class TreeModel extends AbstractServletBean {

    public TreeModel() {
        super();
    }

    public TreeModel(BeanContext context, Resource resource) {
        super(context, resource);
    }
}
