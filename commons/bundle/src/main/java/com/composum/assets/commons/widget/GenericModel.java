package com.composum.assets.commons.widget;

import com.composum.sling.core.AbstractSlingBean;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public class GenericModel extends AbstractSlingBean {

    public GenericModel() {
    }

    public GenericModel(BeanContext context, Resource resource) {
        super(context, resource);
    }
}
