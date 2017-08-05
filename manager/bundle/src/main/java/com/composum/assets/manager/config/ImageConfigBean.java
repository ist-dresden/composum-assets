package com.composum.assets.manager.config;

import com.composum.assets.commons.config.ImageConfig;
import com.composum.assets.commons.util.AssetConfigUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import org.apache.sling.api.resource.Resource;

public class ImageConfigBean extends AbstractConfigBean {

    public ImageConfigBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public ImageConfigBean(BeanContext context) {
        super(context);
    }

    public ImageConfigBean() {
        super();
    }

    public void initialize(BeanContext context, Resource resource) {
        super.initialize(context, resource);
        config = new ImageConfig(AssetConfigUtil.assetConfigCascade(ResourceHandle.use(resource)));
    }

    @Override
    public ImageConfig getConfig() {
        return (ImageConfig) config;
    }
}
