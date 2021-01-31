package com.composum.assets.manager.view;

import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.LinkUtil;
import com.composum.sling.core.util.MimeTypeUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public class NavigatorBean extends AbstractServletBean {

    public class ResourceItem {

        protected Resource resource;

        protected String name;
        protected String nameEscaped;

        protected String path;
        protected String pathEncoded;

        protected String mimeType;

        public ResourceItem(Resource resource) {
            this.resource = resource;
        }

        public Resource getResource() {
            return resource;
        }

        public String getName() {
            if (name == null) {
                if (resource != null) {
                    name = resource.getName();
                    if (StringUtils.isBlank(name)) {
                        name = "jcr:root";
                    }
                } else {
                    name = "";
                }
            }
            return name;
        }

        public String getNameEscaped() {
            if (nameEscaped == null) {
                nameEscaped = StringEscapeUtils.escapeHtml4(getName());
            }
            return nameEscaped;
        }

        public String getPath() {
            if (path == null) {
                path = resource != null ? resource.getPath() : "";
            }
            return path;
        }

        public String getPathEncoded() {
            if (pathEncoded == null) {
                pathEncoded = LinkUtil.encodePath(getPath());
            }
            return pathEncoded;
        }

        /**
         * the content mime type declared for the current resource
         */
        public String getMimeType() {
            if (mimeType == null) {
                mimeType = resource != null ? MimeTypeUtil.getMimeType(resource, "") : "";
            }
            return mimeType;
        }
    }

    private transient ResourceItem current;
    private transient ResourceItem parent;
    private transient List<ResourceItem> parents;

    public NavigatorBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public NavigatorBean(BeanContext context) {
        super(context);
    }

    public NavigatorBean() {
        super();
    }

    public ResourceItem getCurrent() {
        if (current == null) {
            current = new ResourceItem(getResource());
        }
        return current;
    }

    public ResourceItem getParent() {
        if (parent == null) {
            parent = new ResourceItem(getResource().getParent());
        }
        return parent;
    }

    public List<ResourceItem> getParents() {
        if (parents == null) {
            parents = new ArrayList<>();
            Resource resource = getResource();
            String parentPath;
            Resource parent;
            while ((parent = resource.getParent()) != null && !"/".equals(parent.getPath())) {
                parents.add(0, new ResourceItem(parent));
                resource = parent;
            }
        }
        return parents;
    }
}
