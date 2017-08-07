package com.composum.assets.manager.view;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ImageConfig;
import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

public class ConfigBean extends AbstractServletBean {

    public ConfigBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public ConfigBean(BeanContext context) {
        super(context);
    }

    public ConfigBean() {
        super();
    }

    /**
     * determine the configuration resource to use...
     */
    @Override
    public void initialize(BeanContext context, Resource resource) {
        if (!ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET_CONFIG) &&
                !ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_IMAGE_CONFIG)) {
            Resource content = null;
            if (!resource.getName().equals(JcrConstants.JCR_CONTENT)) {
                content = resource.getChild(JcrConstants.JCR_CONTENT);
                if (content != null) {
                    resource = content;
                }
            }
            Resource config = resource.getChild(AssetConfig.CHILD_NAME);
            if (config == null) {
                config = resource.getChild(ImageConfig.CHILD_NAME);
            }
            if (config != null && (ResourceUtil.isResourceType(config, AssetsConstants.NODE_TYPE_ASSET_CONFIG) ||
                    ResourceUtil.isResourceType(config, AssetsConstants.NODE_TYPE_IMAGE_CONFIG))) {
                resource = config;
            }
        }
        super.initialize(context, resource);
    }
}
