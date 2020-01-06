package com.composum.assets.commons.widget;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.handle.MetaData;
import com.composum.assets.commons.service.AssetsService;
import com.composum.sling.clientlibs.handle.FileHandle;
import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.bean.BeanFactory;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

@BeanFactory(serviceClass = AssetsService.class)
public abstract class Thumbnail extends AbstractServletBean implements Comparable<Thumbnail> {

    public static final String KEY_ASSET = "asset";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_VIDEO = "video";
    public static final String KEY_DOCUMENT = "document";
    public static final String KEY_FILE = "file";
    public static final String KEY_INVALID = "invalid";

    @Nonnull
    public static Thumbnail create(@Nonnull final BeanContext context, @Nullable final Resource resource) {
        AssetsConfiguration config = context.getService(AssetsConfiguration.class);
        if (resource != null) {
            if (config.getAssetFileFilter().accept(resource)) {
                return new Thumbnail.Asset(context, resource);
            } else if (config.getImageFileFilter().accept(resource)) {
                return new Thumbnail.Image(context, resource);
            } else if (config.getVideoFileFilter().accept(resource)) {
                return new Thumbnail.Video(context, resource);
            } else if (config.getDocumentFileFilter().accept(resource)) {
                return new Thumbnail.Document(context, resource);
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

    public abstract boolean isValid();

    public abstract String getKey();

    public abstract String getContent();

    public abstract Date getLastModified();

    public abstract boolean isMetaAvailable();

    public abstract MetaData getMetaData();

    @Override
    public int compareTo(Thumbnail other) {
        CompareToBuilder builder = new CompareToBuilder();
        builder.append(getName(), other.getName());
        builder.append(getPath(), other.getPath());
        return builder.toComparison();
    }

    public static class File extends Thumbnail {

        public FileHandle file;

        public File() {
        }

        public File(BeanContext context, Resource resource) {
            super(context, resource);
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public String getKey() {
            return KEY_FILE;
        }

        @Override
        public String getContent() {
            return "<div class=\"thumbnail-file\" data-path=\"" + getPath() + "\"></div>";
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

        @Override
        public String getKey() {
            return KEY_IMAGE;
        }

        @Override
        public String getContent() {
            return "<img class=\"thumbnail-image\" src=\"" + getPath() + "\"/>";
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

        @Override
        public String getKey() {
            return KEY_ASSET;
        }

        @Override
        public String getContent() {
            return "<img class=\"thumbnail-image\" src=\"" +
                    asset.getImageUri("thumbnail", "large") +
                    "\"/>";
        }

        @Override
        public Date getLastModified() {
            return asset.getLastModified().getTime();
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

        @Override
        public String getKey() {
            return KEY_VIDEO;
        }

        @Override
        public String getContent() {
            return "<video class=\"thumbnail-video\">" +
                    "<source type=\"" + file.getMimeType() + "\" src=\"" + getPath() + "\"/></video>";
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

        @Override
        public String getKey() {
            return KEY_DOCUMENT;
        }

        @Override
        public String getContent() {
            return "<div class=\"thumbnail-document\" data-path=\"" + getPath() + "\"></div>";
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

        @Override
        public String getKey() {
            return KEY_INVALID;
        }

        @Override
        public String getContent() {
            return null;
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
