/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.handle;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.util.ImageUtil;
import com.composum.sling.clientlibs.handle.FileHandle;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.util.LinkUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

/**
 * Models a rendition of an asset variation. The resource can be at /content if it's an original, or at
 * {@value AssetsConstants#PATH_TRANSIENTS} if it's a transient rendering.
 */
public class AssetRendition extends AssetHandle<RenditionConfig> {

    public static final String NODE_TYPE = AssetsConstants.NODE_TYPE_RENDITION;
    public static final String RESOURCE_TYPE = AssetsConstants.RESOURCE_TYPE_RENDITION;

    @Nonnull
    protected AssetVariation variation;
    protected RenditionConfig config;

    private transient String mimeType;
    private transient FileHandle file;
    private transient String transientsPath;

    public AssetRendition(@Nonnull BeanContext context, @Nonnull Resource resource, @Nonnull AssetVariation variation) {
        super(context, resource);
        this.variation = variation;
        this.config = variation.getChildConfig(getConfigTargetPath());
    }

    @Override
    public RenditionConfig getConfig() {
        return config;
    }

    @Override
    public ConfigHandle getChildConfig(String targetPath) {
        return null;
    }

    @Override
    protected String getConfigTargetPath() {
        return getVariation().getName() + "/" + getName();
    }

    @Nonnull
    public AbstractAsset getAsset() {
        return getVariation().getAsset();
    }

    @Override
    @Nonnull
    public String getTransientsPath() {
        return getVariation().getTransientsPath() + "/" + getName();
    }

    /** The original of the rendition - which is the original of the variation. */
    @Nullable
    public AssetRendition getOriginal() {
        return getVariation().getOriginal();
    }

    public boolean isOriginal() {
        return this.equals(getOriginal());
    }

    public boolean isTransient() {
        return getPath().startsWith(AssetsConstants.PATH_TRANSIENTS);
    }

    @Nonnull
    public AssetVariation getVariation() {
        return variation;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && resource.getProperty(VALID, Boolean.FALSE);
    }

    public String getImageCSS() {
        return ImageUtil.mimeTypeToCss(getMimeType());
    }

    /** Builds an URI that can be satisfied with the {@link com.composum.assets.commons.servlet.AdaptiveImageServlet}. */
    public String getImageUri() {
        String rendition = this.getName();
        StringBuilder builder = new StringBuilder();
        String path = getAsset().getPath();
        String ext = getMimeType().substring("image/".length());
        if (ext.equals("jpeg")) {
            ext = "jpg";
        }
        if (path.endsWith("." + ext)) {
            path = path.substring(0, path.length() - (ext.length() + 1));
        }
        String name = path.substring(path.lastIndexOf('/') + 1);
        builder.append(path);
        builder.append(".adaptive");
        builder.append('.').append(getVariation().getName());
        builder.append('.').append(rendition);
        builder.append('.').append(ext);
        builder.append('/').append(getCacheHash());
        builder.append('/').append(name);
        builder.append('.').append(ext);
        String uri = builder.toString();
        return uri;
    }

    public String getImageUrl() {
        String url = getImageUri();
        return LinkUtil.getUrl(getRequest(), url);
    }

    protected String getCacheHash() {
        String transientsPath = this.getTransientsPath();
        byte[] md5 = DigestUtils.md5(transientsPath);
        BigInteger md5Int = new BigInteger(md5);
        return md5Int.abs().toString(36);
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
