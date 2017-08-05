/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.handle;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.util.AdaptiveUtil;
import com.composum.assets.commons.util.ImageUtil;
import com.composum.sling.clientlibs.handle.FileHandle;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.util.LinkUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.jcr.RepositoryException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

public class AssetRendition extends AssetHandle<RenditionConfig> {

    public static final String NODE_TYPE = AssetsConstants.NODE_TYPE_RENDITION;
    public static final String RESOURCE_TYPE = AssetsConstants.RESOURCE_TYPE_RENDITION;

    protected AssetVariation variation;
    protected RenditionConfig config;

    private transient String mimeType;
    private transient FileHandle file;

    public AssetRendition() {
    }

    public AssetRendition(BeanContext context, Resource resource, AssetVariation variation) {
        super(context, resource);
        this.variation = variation;
        this.config = variation.getChildConfig(resource);
    }

    public RenditionConfig getConfig() {
        return config;
    }

    public ConfigHandle getChildConfig(Resource resource) {
        return null;
    }

    public AbstractAsset getAsset() {
        return getVariation().getAsset();
    }

    public AssetRendition getOriginal() {
        return getVariation().getOriginal();
    }

    public AssetVariation getVariation() {
        return variation;
    }

    public boolean isValid() {
        return super.isValid() && resource.getProperty(VALID, Boolean.FALSE);
    }

    public String getImageCSS() {
        return ImageUtil.mimeTypeToCss(getMimeType());
    }

    public String getImageUri() {
        String uri = "";
        ImageAsset image = (ImageAsset) getAsset();
        if (image != null) {
            uri = AdaptiveUtil.getImageUri(this);
        }
        return uri;
    }

    public String getImageUrl() {
        String url = getImageUri();
        return LinkUtil.getUrl(getRequest(), url);
    }

    public FileHandle getFile() {
        if (file == null) {
            List<ResourceHandle> files = resource.getChildrenByResourceType(ResourceUtil.TYPE_FILE);
            if (files.size() > 0) {
                file = new FileHandle(files.get(0));
            }
        }
        return file;
    }

    public String getMimeType() {
        if (mimeType == null) {
            FileHandle file = getFile();
            if (file != null) {
                mimeType = file.getMimeType();
            }
            if (StringUtils.isBlank(mimeType)) {
                AssetRendition original = getOriginal();
                if (original != null && !original.getPath().equals(getPath())) {
                    mimeType = original.getMimeType();
                }
            }
        }
        return mimeType;
    }

    public Long getSize() {
        Long result = null;
        FileHandle file = getFile();
        if (file != null) {
            result = file.getSize();
        }
        return result;
    }

    public Calendar getLastModified() {
        Calendar result = null;
        FileHandle file = getFile();
        if (file != null) {
            result = file.getLastModified();
        }
        return result;
    }

    public InputStream getStream() throws RepositoryException {
        FileHandle file = getFile();
        if (file != null && file.isValid()) {
            return file.getStream();
        }
        return null;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AssetRendition && getPath().equals(((AssetRendition) other).getPath());
    }

    @Override
    public int hashCode() {
        return getPath().hashCode();
    }

}
