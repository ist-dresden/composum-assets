package com.composum.assets.commons.handle;

import com.composum.assets.commons.util.ImageUtil;
import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.MimeTypeUtil;
import org.apache.sling.api.resource.Resource;

public class SimpleFile extends AbstractServletBean {

    public static final ResourceFilter FILTER =
            new ResourceFilter.PrimaryTypeFilter(new StringFilter.WhiteList("^nt:file$"));

    private transient String mimeType;

    public SimpleFile(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public SimpleFile(BeanContext context) {
        super(context);
    }

    public SimpleFile() {
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
