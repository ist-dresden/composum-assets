package com.composum.assets.commons.widget;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.handle.MetaData;
import com.composum.assets.commons.service.AssetsService;
import com.composum.sling.clientlibs.handle.FileHandle;
import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.bean.BeanFactory;
import com.composum.sling.core.util.LinkUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.composum.assets.commons.AssetsConstants.ORIGINAL;
import static com.composum.assets.commons.AssetsConstants.THUMBNAIL;

@BeanFactory(serviceClass = AssetsService.class)
public abstract class Thumbnail extends AbstractServletBean implements Comparable<Thumbnail> {

    public static final String KEY_ASSET = "asset";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_VIDEO = "video";
    public static final String KEY_AUDIO = "audio";
    public static final String KEY_DOCUMENT = "document";
    public static final String KEY_BINARY = "binary";
    public static final String KEY_FILE = "file";
    public static final String KEY_INVALID = "invalid";

    public static final String ICON_IMAGE = "image";
    public static final String ICON_VIDEO = "video";
    public static final String ICON_AUDIO = "audio";
    public static final String ICON_PDF = "pdf";
    public static final String ICON_ARCHIVE = "archive";
    public static final String ICON_TEXT = "text";
    public static final String ICON_EXCEL = "excel";
    public static final String ICON_WORD = "word";
    public static final Map<String, String> ICONS = new HashMap<String, String>() {{
        put("pdf", ICON_PDF);
        put("xls", ICON_EXCEL);
        put("xlsx", ICON_EXCEL);
        put("doc", ICON_WORD);
        put("docx", ICON_WORD);
        put("zip", ICON_ARCHIVE);
        put("tar", ICON_ARCHIVE);
        put("gz", ICON_ARCHIVE);
        put("txt", ICON_TEXT);
        put("png", ICON_IMAGE);
        put("jpg", ICON_IMAGE);
        put("jpeg", ICON_IMAGE);
        put("gif", ICON_IMAGE);
        put("mp4", ICON_VIDEO);
    }};

    @Nonnull
    public static Thumbnail create(@Nonnull final BeanContext context, @Nullable final Resource resource) {
        AssetsConfiguration config = context.getService(AssetsConfiguration.class);
        if (resource != null) {
            if (config.getImageAssetFileFilter().accept(resource)) {
                return new Thumbnail.Asset(context, resource);
            } else if (config.getImageSimpleFileFilter().accept(resource)) {
                return new Thumbnail.Image(context, resource);
            } else if (config.getVideoFileFilter().accept(resource)) {
                return new Thumbnail.Video(context, resource);
            } else if (config.getAudioFileFilter().accept(resource)) {
                return new Thumbnail.Audio(context, resource);
            } else if (config.getDocumentFileFilter().accept(resource)) {
                return new Thumbnail.Document(context, resource);
            } else if (config.getBinaryFileFilter().accept(resource)) {
                return new Thumbnail.Binary(context, resource);
            } else if (config.getAnyFileFilter().accept(resource)) {
                return new Thumbnail.File(context, resource);
            }
        }
        // an invalid instance for unsupported types to avoid 'null' results (BeanFactory)
        return new Thumbnail.Invalid(context, resource);
    }

    public static void add(@Nonnull final BeanContext context, @Nonnull final Resource resource,
                           @Nonnull final List<Thumbnail> collection) {
        Thumbnail instance = create(context, resource);
        if (instance.isValid()) {
            collection.add(instance);
        }
    }

    protected Thumbnail() {
    }

    protected Thumbnail(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    @Nonnull
    public String getName() {
        return resource.getName();
    }

    @Nonnull
    public String getPath() {
        return resource.getPath();
    }

    @Nonnull
    public String getMimeType() {
        return "";
    }

    @Nonnull
    public String getMimeTypeCss() {
        String mimeType = getMimeType();
        return "mimetype-" + (StringUtils.isNotBlank(mimeType)
                ? mimeType.replace('/', '_').replace('+', '-')
                : "unknown");
    }

    public abstract boolean isValid();

    @Nonnull
    public abstract String getKey();

    @Nullable
    public abstract String getIconKey();

    @Nonnull
    public String getIconCss() {
        String iconKey = getIconKey();
        return "fa fa-file" + (iconKey != null ? ("-" + iconKey) : "") + "-o";
    }

    @Nullable
    public abstract String getUrl();

    public abstract Date getLastModified();

    public abstract boolean isMetaAvailable();

    public abstract MetaData getMetaData();

    @Override
    public int compareTo(Thumbnail other) {
        CompareToBuilder builder = new CompareToBuilder();
        builder.append(getName().toLowerCase(), other.getName().toLowerCase());
        builder.append(getPath().toLowerCase(), other.getPath().toLowerCase());
        return builder.toComparison();
    }

    public static class File extends Thumbnail {

        public FileHandle file;

        private transient String iconKey;

        public File() {
        }

        public File(BeanContext context, Resource resource) {
            super(context, resource);
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Nonnull
        @Override
        public String getKey() {
            return KEY_FILE;
        }

        @Nullable
        @Override
        public String getIconKey() {
            if (iconKey == null) {
                String type = StringUtils.substringAfterLast("/", getMimeType());
                if (StringUtils.isNotBlank(type)) {
                    iconKey = ICONS.get(type);
                }
                if (StringUtils.isBlank(iconKey)) {
                    type = file.getExtension();
                    if (StringUtils.isNotBlank(type)) {
                        iconKey = ICONS.get(type);
                    }
                }
                if (iconKey == null) {
                    iconKey = "";
                }
            }
            return StringUtils.isNotBlank(iconKey) ? iconKey : null;
        }

        @Nonnull
        @Override
        public String getUrl() {
            return LinkUtil.getUrl(context.getRequest(), getPath());
        }

        @Override
        public void initialize(BeanContext context, Resource resource) {
            super.initialize(context, resource);
            // if the resource type is wrapped the file handle doesn't work properly
            file = new FileHandle(context.getResolver().getResource(resource.getPath()));
        }

        @Override
        public Date getLastModified() {
            return file.getLastModified().getTime();
        }

        @Override
        @Nonnull
        public String getMimeType() {
            return file.getMimeType();
        }

        @Override
        public boolean isMetaAvailable() {
            return false;
        }

        @Override
        public MetaData getMetaData() {
            return null;
        }
    }

    public static class Image extends File {

        protected MetaData metaData;

        public Image() {
        }

        public Image(BeanContext context, Resource resource) {
            super(context, resource);
        }

        @Override
        public void initialize(BeanContext context, Resource resource) {
            super.initialize(context, resource);
            metaData = new MetaData(context, resource);
        }

        @Nonnull
        @Override
        public String getKey() {
            return KEY_IMAGE;
        }

        @Override
        public String getIconKey() {
            return ICON_IMAGE;
        }

        @Override
        public boolean isMetaAvailable() {
            return metaData.isValid();
        }

        @Override
        public MetaData getMetaData() {
            return metaData;
        }
    }

    public static class Asset extends Thumbnail {

        public ImageAsset asset;

        private transient String variationKey;
        private transient String renditionKey;

        public Asset() {
        }

        public Asset(BeanContext context, Resource resource) {
            super(context, resource);
        }

        @Override
        public void initialize(BeanContext context, Resource resource) {
            super.initialize(context, resource);
            asset = new ImageAsset(context, resource);
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Nonnull
        @Override
        public String getKey() {
            return KEY_ASSET;
        }

        @Nonnull
        @Override
        public String getIconKey() {
            return ICON_IMAGE;
        }

        public String getVariationKey() {
            if (variationKey == null) {
                SlingHttpServletRequest request = context.getRequest();
                if (request != null) {
                    variationKey = request.getParameter(AssetsConstants.VARIATION);
                }
                if (StringUtils.isBlank(variationKey)) {
                    variationKey = THUMBNAIL;
                }
            }
            return variationKey;
        }

        public String getRenditionKey() {
            if (renditionKey == null) {
                SlingHttpServletRequest request = context.getRequest();
                if (request != null) {
                    renditionKey = request.getParameter(AssetsConstants.RENDITION);
                }
                if (StringUtils.isBlank(renditionKey)) {
                    renditionKey = THUMBNAIL.equals(getVariationKey()) ? "large" : ORIGINAL;
                }
            }
            return renditionKey;
        }

        @Nonnull
        @Override
        public String getUrl() {
            return LinkUtil.getUrl(context.getRequest(), getImageUri());
        }

        public String getImageUri() {
            return asset.getImageUri(getVariationKey(), getRenditionKey());
        }

        @Override
        public Date getLastModified() {
            Calendar lastModified = asset.getLastModified();
            return lastModified != null ? lastModified.getTime() : null;
        }

        @Override
        @Nonnull
        public String getMimeType() {
            return asset.getMimeType();
        }

        @Override
        public boolean isMetaAvailable() {
            return false;
        }

        @Override
        public MetaData getMetaData() {
            return null;
        }
    }

    public static class Video extends File {

        public Video() {
        }

        public Video(BeanContext context, Resource resource) {
            super(context, resource);
        }

        @Nonnull
        @Override
        public String getKey() {
            return KEY_VIDEO;
        }

        @Nonnull
        @Override
        public String getIconKey() {
            return ICON_VIDEO;
        }

        @Override
        public boolean isMetaAvailable() {
            return false;
        }

        @Override
        public MetaData getMetaData() {
            return null;
        }
    }

    public static class Audio extends File {

        public Audio() {
        }

        public Audio(BeanContext context, Resource resource) {
            super(context, resource);
        }

        @Nonnull
        @Override
        public String getKey() {
            return KEY_AUDIO;
        }

        @Nonnull
        @Override
        public String getIconKey() {
            return ICON_AUDIO;
        }

        @Override
        public boolean isMetaAvailable() {
            return false;
        }

        @Override
        public MetaData getMetaData() {
            return null;
        }
    }

    public static class Document extends File {

        public Document() {
        }

        public Document(BeanContext context, Resource resource) {
            super(context, resource);
        }

        @Nonnull
        @Override
        public String getKey() {
            return KEY_DOCUMENT;
        }

        @Override
        public boolean isMetaAvailable() {
            return false;
        }

        @Override
        public MetaData getMetaData() {
            return null;
        }
    }

    public static class Binary extends File {

        public Binary() {
        }

        public Binary(BeanContext context, Resource resource) {
            super(context, resource);
        }

        @Nonnull
        @Override
        public String getKey() {
            return KEY_BINARY;
        }

        @Override
        public boolean isMetaAvailable() {
            return false;
        }

        @Override
        public MetaData getMetaData() {
            return null;
        }
    }

    public static class Invalid extends Thumbnail {

        public Invalid() {
        }

        public Invalid(BeanContext context, Resource resource) {
            super(context, resource);
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Nonnull
        @Override
        public String getKey() {
            return KEY_INVALID;
        }

        @Nullable
        @Override
        public String getIconKey() {
            return null;
        }

        @Nonnull
        @Override
        public String getUrl() {
            return "";
        }

        @Override
        public Date getLastModified() {
            return null;
        }

        @Override
        public boolean isMetaAvailable() {
            return false;
        }

        @Override
        public MetaData getMetaData() {
            return null;
        }
    }
}
