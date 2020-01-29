package com.composum.assets.commons.util;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ImageConfig;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.composum.assets.commons.config.ConfigHandle.DISABLED;
import static com.composum.assets.commons.config.ConfigHandle.EXTENSION;

public class AssetConfigUtil {

    public static boolean isConfigResource(@Nonnull final Resource configResource) {
        return ResourceUtil.isResourceType(configResource, ImageConfig.NODE_TYPE)
                || ResourceUtil.isResourceType(configResource, AssetConfig.NODE_TYPE);
    }

    public static boolean isConfigExtension(@Nonnull final Resource configResource) {
        return (ResourceUtil.isResourceType(configResource, ImageConfig.NODE_TYPE)
                && configResource.getValueMap().get(EXTENSION, Boolean.TRUE)) ||
                (ResourceUtil.isResourceType(configResource, AssetConfig.NODE_TYPE)
                        && configResource.getValueMap().get(EXTENSION, Boolean.FALSE));
    }

    /**
     * @return the configuration cascade according to the configuration type od the given resource
     */
    @Nonnull
    public static List<ResourceHandle> configCascade(@Nonnull final Resource reference) {
        return ResourceUtil.isResourceType(reference, ImageConfig.NODE_TYPE)
                ? imageConfigCascade(ResourceHandle.use(reference))
                : assetConfigCascade(ResourceHandle.use(reference));
    }

    /**
     * @return the configuration cascade of an image configuration resource
     */
    @Nonnull
    public static List<ResourceHandle> imageConfigCascade(@Nonnull final Resource reference) {
        List<ResourceHandle> cascade = new ArrayList<>();
        assetConfigCascade(cascade, reference, ImageConfig.NAME_PATTERN, ImageConfig.NODE_TYPE);
        return cascade;
    }

    /**
     * @return the configuration cascade of an asset configuration resource
     */
    @Nonnull
    public static List<ResourceHandle> assetConfigCascade(@Nonnull final Resource reference) {
        List<ResourceHandle> cascade = new ArrayList<>();
        assetConfigCascade(cascade, reference, AssetConfig.NAME_PATTERN, AssetConfig.NODE_TYPE);
        return cascade;
    }

    /**
     * adds the configuration cascade with the given start point resource and the expected start point type and name
     */
    public static void assetConfigCascade(@Nonnull final List<ResourceHandle> cascade, @Nullable final Resource reference,
                                          @Nonnull final Pattern pattern, @Nonnull final String nodeType) {
        ResourceHandle configNode = ResourceHandle.use(reference).findUpwards(
                ResourceUtil.CONTENT_NODE, pattern, nodeType);
        if (configNode != null && configNode.isValid()) {
            if (isConfigExtension(configNode)) {
                // fill the cascae with all base configurations
                assetConfigCascade(cascade, configNode.getParent(3), AssetConfig.NAME_PATTERN, AssetConfig.NODE_TYPE);
            }
            // add the found configuration on to of the cascade
            if (!configNode.getProperty(DISABLED, Boolean.FALSE)) {
                cascade.add(0, configNode);
            }
        } else if (ImageConfig.NODE_TYPE.equals(nodeType)) {
            assetConfigCascade(cascade, reference, AssetConfig.NAME_PATTERN, AssetConfig.NODE_TYPE);
        }
    }
}
