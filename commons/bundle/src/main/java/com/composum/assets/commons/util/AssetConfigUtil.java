package com.composum.assets.commons.util;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ImageConfig;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AssetConfigUtil {

    public static List<ResourceHandle> configCascade(Resource config) {
        return ResourceUtil.isResourceType(config, ImageConfig.NODE_TYPE)
                ? imageConfigCascade(ResourceHandle.use(config))
                : assetConfigCascade(ResourceHandle.use(config));
    }

    public static List<ResourceHandle> imageConfigCascade(Resource imageConfig) {
        List<ResourceHandle> cascade = assetConfigCascade(imageConfig);
        assetConfigCascade(cascade, imageConfig, ImageConfig.NAME_PATTERN);
        return cascade;
    }

    public static List<ResourceHandle> assetConfigCascade(Resource assetConfig) {
        List<ResourceHandle> cascade = new ArrayList<>();
        assetConfigCascade(cascade, assetConfig);
        return cascade;
    }

    public static void assetConfigCascade(List<ResourceHandle> cascade, Resource assetConfig) {
        assetConfigCascade(cascade, assetConfig, AssetConfig.NAME_PATTERN);
    }

    public static void assetConfigCascade(List<ResourceHandle> cascade, Resource reference, Pattern pattern) {
        ResourceHandle configNode = ResourceHandle.use(reference).findUpwards(
                ResourceUtil.CONTENT_NODE, pattern, AssetConfig.NODE_TYPE);
        if (configNode != null && configNode.isValid()) {
            if (configNode.getProperty("extension", Boolean.FALSE)) {
                assetConfigCascade(cascade, configNode.getParent(3), pattern);
            }
            if (!configNode.getProperty("disabled", Boolean.FALSE)) {
                cascade.add(0, configNode);
            }
        }
    }
}
