package com.composum.assets.commons.widget;

import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.handle.MetaData;
import com.composum.assets.commons.handle.SimpleFile;
import com.composum.assets.commons.handle.SimpleImage;
import com.composum.assets.commons.handle.VideoAsset;
import com.composum.sling.clientlibs.handle.FileHandle;
import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

public abstract class Thumbnail extends AbstractServletBean implements Comparable<Thumbnail> {

    @Nullable
    public static Thumbnail create(@Nonnull final BeanContext context, @Nonnull final Resource resource) {
        if (ImageAsset.FILTER.accept(resource)) {
            return new Thumbnail.Asset(context, resource);
        } else if (SimpleImage.FILTER.accept(resource)) {
            return new Thumbnail.Image(context, resource);
        } else if (VideoAsset.FILTER.accept(resource)) {
            return new Thumbnail.Video(context, resource);
        } else if (SimpleFile.FILTER.accept(resource)) {
            return new Thumbnail.File(context, resource);
        }
        return null;
    }

    public static void add(@Nonnull final BeanContext context, @Nonnull final Resource resource,
                           @Nonnull final List<Thumbnail> collection) {
        Thumbnail instance = create(context, resource);
        if (instance != null) {
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
        public String getContent() {
            return null;
        }

        @Override
        public void initialize(BeanContext context, Resource resource) {
            super.initialize(context, resource);
            file = new FileHandle(resource);
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
            metaData = new MetaData(context, resource);
        }

        @Override
        public void initialize(BeanContext context, Resource resource) {
            super.initialize(context, resource);
            metaData = new MetaData(context, resource);
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
        public Date getLastModified() {
            return asset.getLastModified().getTime();
        }

        @Override
        @Nonnull
        public String getMimeType() {
            return asset.getMimeType();
        }

        @Override
        public String getContent() {
            return "<img class=\"thumbnail-image\" src=\"" +
                    asset.getImageUri("thumbnail", "large") +
                    "\"/>";
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
}
