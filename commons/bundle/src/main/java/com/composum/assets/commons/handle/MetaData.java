/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.handle;

import com.composum.assets.commons.AssetsConstants;
import com.composum.sling.core.AbstractSlingBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.StagingConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

import java.util.Date;

public class MetaData extends AbstractSlingBean {

    public static final Integer UNDEFINED_INT = -1;
    public static final Long UNDEFINED_LONG = -1L;
    public static final Date UNDEFINED_DATE = new Date(0L);

    private transient Integer width;
    private transient Integer height;

    private transient Long size;
    private transient Date date;

    private transient String title;
    private transient String description;

    protected MetaData fallback;

    public static final String[] META_NODE_PATHS = new String[]{
            AssetsConstants.NODE_META,
            AssetsConstants.PATH_META
    };

    public MetaData(BeanContext context, Resource resource, MetaData fallback) {
        this(context, resource);
        setFallback(fallback);
    }

    public MetaData(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public MetaData(BeanContext context) {
        super(context);
    }

    public MetaData() {
    }

    public void initialize(BeanContext context, Resource resource) {
        Resource meta = null;
        while (meta == null && resource != null && !resource.getPath().equals("/")) {
            meta = resource;
            for (int i = 0; !ResourceUtil.isResourceType(meta,
                    StagingConstants.TYPE_METADATA) && i < META_NODE_PATHS.length; i++) {
                meta = resource.getChild(META_NODE_PATHS[i]);
            }
            if (!ResourceUtil.isResourceType(meta, StagingConstants.TYPE_METADATA)) {
                meta = null;
                if (ResourceUtil.isResourceType(resource, JcrConstants.NT_FILE)
                        || ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET)) {
                    resource = null;
                } else {
                    resource = resource.getParent();
                }
            }
        }
        super.initialize(context, meta);
    }

    public void setFallback(MetaData fallback) {
        this.fallback = fallback;
    }

    public boolean isValid() {
        return resource.isValid();
    }

    @Override
    public <T> T getProperty(String key, T defaultValue) {
        T value = super.getProperty(key, defaultValue);
        if (value == defaultValue && fallback != null) {
            value = fallback.getProperty(key, defaultValue);
        }
        return value;
    }

    public boolean getHasDimension() {
        return getWidth() != UNDEFINED_INT && getHeight() != UNDEFINED_INT;
    }

    public int getWidth() {
        if (width == null) {
            width = getProperty("width", UNDEFINED_INT);
        }
        return width;
    }

    public int getHeight() {
        if (height == null) {
            height = getProperty("height", UNDEFINED_INT);
        }
        return height;
    }

    public boolean getHasSize() {
        return getSize() != UNDEFINED_LONG;
    }

    public long getSize() {
        if (size == null) {
            size = getProperty("size", UNDEFINED_LONG);
        }
        return size;
    }

    public boolean getHasDate() {
        return getDate() != UNDEFINED_DATE;
    }

    public Date getDate() {
        if (date == null) {
            date = getProperty("date", UNDEFINED_DATE);
        }
        return date;
    }

    public String getTitle() {
        if (title == null) {
            title = getProperty("title", "");
            if (StringUtils.isBlank(description)) {
                title = getProperty(ResourceUtil.PROP_TITLE, "");
            }
        }
        return title;
    }

    public String getDescription() {
        if (description == null) {
            description = getProperty("description", "");
            if (StringUtils.isBlank(description)) {
                description = getProperty(ResourceUtil.PROP_DESCRIPTION, "");
            }
        }
        return description;
    }
}
