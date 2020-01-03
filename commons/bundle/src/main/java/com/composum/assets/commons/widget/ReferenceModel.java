package com.composum.assets.commons.widget;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public class ReferenceModel extends FieldModel {

    public ReferenceModel() {
        super();
    }

    public ReferenceModel(BeanContext context, Resource resource) {
        super(context, resource);
    }
}
