package com.composum.assets.commons.handle;

import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.MimeTypeUtil;
import org.apache.sling.api.resource.Resource;

public class VideoAsset extends AbstractServletBean {

    private transient String mimeType;

    public VideoAsset(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public VideoAsset(BeanContext context) {
        super(context);
    }

    public VideoAsset() {
        super();
    }

    public String getVideoCSS() {
        final String mimeType = getMimeType();
        return mimeType.substring(mimeType.indexOf('/') + 1).replaceAll("[+]", " ");
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
