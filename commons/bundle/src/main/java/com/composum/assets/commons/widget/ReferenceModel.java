package com.composum.assets.commons.widget;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class ReferenceModel extends FieldModel {

    private transient Thumbnail content;
    private transient ResourceHandle reference;

    private transient String referenceType;
    private transient String mimeType;

    public ReferenceModel() {
        super();
    }

    public ReferenceModel(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public Thumbnail getContent() {
        if (content == null) {
            content = Thumbnail.create(context, getReference());
        }
        return content;
    }

    public Resource getReference() {
        if (reference == null) {
            String path = (String) getValue();
            reference = StringUtils.isNotBlank(path)
                    ? ResourceHandle.use(context.getResolver().getResource(path))
                    : ResourceHandle.use(null);
        }
        return reference.isValid() ? reference : null;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public String getMimeType() {
        if (mimeType == null) {
            mimeType = getContent().getMimeType();
        }
        return mimeType;
    }
}
