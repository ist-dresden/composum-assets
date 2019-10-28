/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.handle;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.sling.core.AbstractSlingBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.StagingConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public abstract class AssetHandle<Config extends ConfigHandle> extends AbstractSlingBean {

    private static final Logger LOG = LoggerFactory.getLogger(AssetHandle.class);

    public static final String IMAGE_RESOURCE_TYPE = "composum/assets/image";

    public static final String VALID = "valid";

    protected AssetHandle() {
    }

    protected AssetHandle(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public abstract Config getConfig();

    /**
     * Returns the configuration for a variation (configPath = variation-name) or a rendition (configPath =
     * variation-name/rendition-name).
     */
    protected abstract ConfigHandle getChildConfig(String targetPath);

    /**
     * The configuration target path of this item - "." in case of an asset, "variation" in case of a variation,
     * "variationname/renditionname" in case of a rendition.
     */
    protected abstract String getConfigTargetPath();

    /**
     * The path where transient renditions of this will be stored. This path is below
     * {@value AssetsConstants#PATH_TRANSIENTS} and replicates the path of the asset with insertions of the version
     * of the jcr:content node whereever there is one, to make sure an asset gets a new place whenever it would be
     * rendered differently. For example, a rendition
     * <code>/content/foo/bar/somesite/assets/bloom.jpg/square/original/bloom/square/medium</code>.jpg could get a
     * {@link #getTransientsPath()}
     * <code>/var/composum/assets/content/foo/bar/somesite/{sitecfgversion}/assets/{assetscfgversion}/bloom.jpg/{
     * bloomcfgversion}/square/{squareoriginalversion}/medium</code> with the UUIDs of the version of the jcr:content
     * node in somesite, assets and bloom.jpg, and the version of the original of the square variation inserted
     * at the appropriate place. If this isn't a staged version, we try to use the last modification time
     * with prefix {@value AssetsConstants#NODE_WORKSPACECONFIGURED} instead.
     */
    @Nonnull
    public abstract String getTransientsPath();

    @Override
    public ResourceHandle getResource() {
        return resource;
    }

    public boolean isValid() {
        return resource != null && resource.isValid();
    }

    @Override
    @Nonnull
    public String getName() {
        return resource.getName();
    }

    @Override
    @Nonnull
    public String getPath() {
        return resource.getPath();
    }

    protected Resource findChildByCategoryOrName(Resource someResource, String type, String... key) {
        Resource result = null;
        for (int i = 0; result == null && i < key.length; i++) {
            result = retrieveChildByCategoryOrName(someResource, type, key[i]);
        }
        return result;
    }

    protected Resource retrieveChildByCategoryOrName(Resource someResource, String type, String key) {
        Resource byName = null;
        for (ResourceHandle child : ResourceHandle.use(someResource).getChildrenByType(type)) {
            String childCfgTargetPath = child.getName();
            if (StringUtils.isNotBlank(getConfigTargetPath())) {
                childCfgTargetPath = getConfigTargetPath() + "/" + childCfgTargetPath;
            }
            ConfigHandle childConfig = getChildConfig(childCfgTargetPath);
            if (childConfig != null) {
                List<String> categories = childConfig.getCategories();
                if (categories.contains(key)) { return child; }
            }
            String[] categories = child.adaptTo(ValueMap.class)
                    .get(ConfigHandle.CATEGORIES, new String[0]);
            if (Arrays.asList(categories).contains(key)) { return child; }
            if (child.getName().equals(key)) {
                byName = child;
            }
        }
        return byName;
    }

    /**
     * Tries to find the version UUID in the JCR version storage to which the jcr:content node if this node refers
     * to; failing that, we use {@link AssetsConstants#NODE_WORKSPACECONFIGURED} + "-" + last modification time in
     * seconds. If the node has no jcr:content, we return null.
     */
    @Nullable
    protected String versionMarker(@Nullable Resource resource) {
        String versionnodename = null;
        if (resource != null && resource.getChild(ResourceUtil.CONTENT_NODE) != null) {
            Calendar lastmodif = null;
            for (String subpath :
                    new String[]{".", ResourceUtil.CONTENT_NODE, ResourceUtil.CONTENT_NODE + "/" + AssetConfig.CHILD_NAME}) {
                Resource subnode = resource.getChild(subpath);
                if (subnode != null) {
                    versionnodename = ResourceHandle.use(subnode)
                            .getProperty(StagingConstants.PROP_REPLICATED_VERSION);
                    if (StringUtils.isNotBlank(versionnodename)) { break; }
                    if (ResourceUtil.isNodeType(subnode, ResourceUtil.MIX_LAST_MODIFIED)) {
                        lastmodif = ResourceHandle.use(subnode)
                                .getProperty(ResourceUtil.PROP_LAST_MODIFIED, Calendar.class);
                    }
                    if (lastmodif != null) { break; }
                }
            }

            if (StringUtils.isBlank(versionnodename)) {
                versionnodename = AssetsConstants.NODE_WORKSPACECONFIGURED;
                if (lastmodif != null) {
                    versionnodename = AssetsConstants.NODE_WORKSPACECONFIGURED + "-" + lastmodif.getTimeInMillis() / 1000;
                } else {
                    LOG.warn("Workspace node {} should have {} to have reliable assets rendering.",
                            resource.getPath(), ResourceUtil.TYPE_LAST_MODIFIED);
                }
            }
        }
        return versionnodename;
    }
}
