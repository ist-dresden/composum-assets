package com.composum.assets.commons.handle;

import com.composum.assets.commons.util.ImageUtil;
import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.MimeTypeUtil;
import org.apache.sling.api.resource.Resource;

public class SimpleImage extends AbstractServletBean {

    private transient String mimeType;

    public SimpleImage(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public SimpleImage(BeanContext context) {
        super(context);
    }

    public SimpleImage() {
        super();
    }

    public String getImageCSS() {
        return ImageUtil.mimeTypeToCss(getMimeType());
    }

    /**
     * the content mime type declared for the current resource
     */
    public String getMimeType() {
        if (mimeType == null) {
            mimeType = MimeTypeUtil.getMimeType(resource, "");
        }
        return mimeType;
    }
}
