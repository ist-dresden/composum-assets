package com.composum.assets.manager.config;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.util.AssetConfigUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public class VariationConfigBean extends AbstractConfigBean {

    protected VariationConfig config;

    public VariationConfigBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public VariationConfigBean(BeanContext context) {
        super(context);
    }

    public VariationConfigBean() {
        super();
    }

    public void initialize(BeanContext context, Resource resource) {
        super.initialize(context, resource);
        Resource assetConfigRes = resource.getParent();
        List<ResourceHandle> assetConfigCascade = AssetConfigUtil.assetConfigCascade(ResourceHandle.use(assetConfigRes));
        List<ResourceHandle> variationConfigCascade = new ArrayList<>(assetConfigCascade);
        variationConfigCascade.add(0, ResourceHandle.use(resource));
        config = new VariationConfig(
                new AssetConfig(assetConfigCascade),
                variationConfigCascade);
    }

    @Override
    public ConfigHandle getConfig() {
        return config;
    }

    public List<RenditionConfig> getRenditionList() {
        return config.getRenditionList(true);
    }
}
