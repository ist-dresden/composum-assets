package com.composum.assets.commons.handle;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public class SimpleDocument extends SimpleFile {

    public SimpleDocument(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public SimpleDocument(BeanContext context) {
        super(context);
    }

    public SimpleDocument() {
        super();
    }
}
