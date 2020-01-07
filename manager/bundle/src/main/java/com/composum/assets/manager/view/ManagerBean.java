package com.composum.assets.manager.view;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.MimeTypeUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

public class ManagerBean extends AbstractServletBean {

    private transient String viewType;

    public ManagerBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public ManagerBean(BeanContext context) {
        super(context);
    }

    public ManagerBean() {
        super();
    }

    public String getViewType() {
        if (viewType == null) {
            String resourceType = resource.getResourceType();
            if (StringUtils.isNotBlank(resourceType)) {
                String type = resourceType.substring(resourceType.lastIndexOf('/') + 1);
                type = type.substring(type.lastIndexOf(':') + 1).toLowerCase();
                switch (type) {
                    case "asset":
                    case "image":
                        viewType = "asset";
                        break;
                    case "file":
                        viewType = "preview";
                        String mimeType = MimeTypeUtil.getMimeType(resource, null);
                        if (StringUtils.isNotBlank(mimeType)) {
                            switch (StringUtils.substringBefore(mimeType, "/")) {
                                case "image":
                                    viewType = "image";
                                    break;
                            }
                        }
                        break;
                }
            }
            if (StringUtils.isBlank(viewType)) {
                Resource content = null;
                if (!resource.getName().equals(JcrConstants.JCR_CONTENT)) {
                    content = resource.getChild(JcrConstants.JCR_CONTENT);
                }
                if (content !=null) {
                    Resource config = content.getChild(AssetConfig.CHILD_NAME);
                    if (ResourceUtil.isResourceType(config,
                            AssetsConstants.NODE_TYPE_ASSET_CONFIG)) {
                        viewType = "config";
                    }
                }
            }
            if (StringUtils.isBlank(viewType)) {
                viewType = "folder";
            }
        }
        return viewType;
    }

    public String getTabType() {
        String selector = getRequest().getSelectors(new StringFilter.BlackList("^tab$"));
        return StringUtils.isNotBlank(selector) ? selector.substring(1) : "general";
    }
}
