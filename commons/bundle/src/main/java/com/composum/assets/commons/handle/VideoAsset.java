package com.composum.assets.commons.handle;

import com.composum.assets.commons.AssetsConstants;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.LinkUtil;
import com.composum.sling.core.util.MimeTypeUtil;
import org.apache.sling.api.resource.Resource;

public class VideoAsset extends AbstractAsset {

    public static final String RESOURCE_TYPE = AssetsConstants.RESOURCE_TYPE_VIDEO;

    private transient String videoUrl;
    private transient String mimeType;

    public VideoAsset(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public VideoAsset() {
        super();
    }

    @Override
    public AbstractAsset getAsset() {
        return this;
    }

    public String getVideoCSS() {
        final String mimeType = getMimeType();
        return mimeType.substring(mimeType.indexOf('/') + 1).replaceAll("[+]", " ");
    }

    /**
     * Returns the URL of this
     * asset.
     */
    public String getVideoUrl() {
        if (videoUrl == null) {
            videoUrl = LinkUtil.getUrl(context.getRequest(), getPath());
        }
        return videoUrl;
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
