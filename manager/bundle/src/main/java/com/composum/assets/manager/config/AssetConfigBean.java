package com.composum.assets.manager.config;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.util.AssetConfigUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import org.apache.sling.api.resource.Resource;

public class AssetConfigBean extends AbstractConfigBean {

    public AssetConfigBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public AssetConfigBean(BeanContext context) {
        super(context);
    }

    public AssetConfigBean() {
        super();
    }

    public void initialize(BeanContext context, Resource resource) {
        super.initialize(context, resource);
        config = new AssetConfig(AssetConfigUtil.assetConfigCascade(ResourceHandle.use(resource)));
    }

    @Override
    public AssetConfig getConfig() {
        return (AssetConfig) config;
    }
}
