/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.handle;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.util.AssetConfigUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.StagingConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAsset extends AssetHandle<AssetConfig> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAsset.class);

    public static final Map<String, Object> VARIATION_PROPERTIES;

    static {
        VARIATION_PROPERTIES = new HashMap<>();
        VARIATION_PROPERTIES.put(ResourceUtil.PROP_PRIMARY_TYPE, AssetsConstants.NODE_TYPE_VARIATION);
        VARIATION_PROPERTIES.put(ResourceUtil.PROP_RESOURCE_TYPE, AssetsConstants.RESOURCE_TYPE_VARIATION);
    }

    private transient AssetConfig assetConfig;
    private transient String transientsPath;

    protected AbstractAsset() {
    }

    protected AbstractAsset(BeanContext context, Resource resource) {
        super(context, resource);
    }

    protected AbstractAsset(BeanContext context, Resource resource, AssetConfig config) {
        this(context, resource);
        assetConfig = config;
    }

    public abstract AbstractAsset getAsset();

    public String getMimeType() {
        AssetRendition original = getOriginal();
        return original != null ? original.getMimeType() : null;
    }

    public Calendar getLastModified() {
        AssetRendition original = getOriginal();
        return original != null ? original.getLastModified() : null;
    }

    public AssetRendition getOriginal() {
        return retrieveOriginal(null);
    }

    public AssetRendition retrieveOriginal(AssetVariation context) {
        AssetVariation variation = getVariation(ConfigHandle.ORIGINAL, ConfigHandle.DEFAULT);
        return variation != null && !variation.equals(context) ? variation.getOriginal() : null;
    }

    public AssetVariation getVariation(String... keyChain) {
        AssetVariation variation = null;
        Resource variationResource = findChildByCategoryOrName(AssetVariation.NODE_TYPE, keyChain);
        if (variationResource != null) {
            variation = new AssetVariation(context, variationResource, this);
        }
        return variation;
    }

    /** Retrieves or creates a variation to store an original - which is stored in the /content resource tree. */
    public AssetVariation getOrCreateVariationForOriginal(String key) throws PersistenceException {
        if (StringUtils.isBlank(key)) {
            AssetConfig assetConfig = getConfig();
            if (assetConfig != null) {
                VariationConfig variationConfig = assetConfig.findVariation();
                if (variationConfig != null) {
                    key = variationConfig.getName();
                }
            }
        }
        if (StringUtils.isBlank(key)) {
            key = "default";
        }
        AssetVariation variation = getVariation(key);
        if (variation == null) {
            Resource assetResource = getResource();
            ResourceResolver resolver = assetResource.getResourceResolver();
            Resource variationResource = resolver.create(assetResource, key, VARIATION_PROPERTIES);
            variation = new AssetVariation(context, variationResource, this);
        }
        return variation;
    }

    @Override
    public AssetConfig getConfig() {
        if (assetConfig == null) {
            List<ResourceHandle> cascade = getConfigCascade();
            if (cascade.size() > 0) {
                assetConfig = createAssetConfig(cascade);
            } else {
                assetConfig = new SelfConfig(resource);
            }
        }
        return assetConfig;
    }

    protected AssetConfig createAssetConfig(List<ResourceHandle> cascade) {
        return new AssetConfig(cascade);
    }

    protected List<ResourceHandle> getConfigCascade() {
        return AssetConfigUtil.assetConfigCascade(resource);
    }

    @Override
    public VariationConfig getChildConfig(Resource resource) {
        AbstractAsset asset = getAsset();
        List<ResourceHandle> variationCascade = asset.getConfigCascade(resource, VariationConfig.NODE_TYPE);
        return variationCascade != null && variationCascade.size() > 0 ? new VariationConfig(getConfig(), variationCascade) : null;
    }

    public List<ResourceHandle> getConfigCascade(Resource asset, String configResourceType) {
        String targetPath = asset.getPath();
        String assetPath = getPath();
        if (targetPath.startsWith(assetPath)) {
            targetPath = targetPath.substring(assetPath.length());
        }
        while (targetPath.startsWith("/")) {
            targetPath = targetPath.substring(1);
        }
        String[] segments = targetPath.split("/");
        StringBuilder path = new StringBuilder();
        path.append(segments[0]);
        if (segments.length > 1) {
            path.append("/").append(segments[1]);
        }
        targetPath = path.toString();
        return getConfig().getCascadeForPath(configResourceType, targetPath);
    }

    /**
     * The path where transient renditions will be stored. This path is below
     * {@value AssetsConstants#PATH_TRANSIENTS} and replicates the path of the asset with insertions of the version
     * of the jcr:content node whereever there is one, to make sure an asset gets a new place whenever it would be
     * rendered differently. For example, an asset
     * <code>/content/foo/bar/somesite/assets/bloom.jpg/square/original/bloom</code>.jpg could get a {@link #getTransientsPath()}
     * <code>/var/composum/assets/content/foo/bar/somesite/{sitecfgversion}/assets/{assetscfgversion}/bloom.jpg/{
     * bloomcfgversion}</code> with the UUIDs of the version of the jcr:content node in somesite, assets and bloom.jpg
     * inserted at the appropriate place. If this isn't a staged version, we try to use the last modification time
     * with prefix {@value AssetsConstants#NODE_WORKSPACECONFIGURED} instead.
     */
    public String getTransientsPath() {
        if (transientsPath == null) {
            StringBuilder transientsPathBuilder = new StringBuilder();
            Resource stepResource = resource;
            while (stepResource != null) {
                if (stepResource.getChild(ResourceUtil.CONTENT_NODE) != null) {
                    String versionnodename = null;
                    Calendar lastmodif = null;
                    for (String subpath :
                            new String[]{".", ResourceUtil.CONTENT_NODE, ResourceUtil.CONTENT_NODE + "/" + AssetConfig.CHILD_NAME}) {
                        Resource subnode = stepResource.getChild(subpath);
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
                                    stepResource.getPath(), ResourceUtil.TYPE_LAST_MODIFIED);
                        }
                    }
                    transientsPathBuilder.insert(0, versionnodename);
                    transientsPathBuilder.insert(0, "/");
                }

                transientsPathBuilder.insert(0, stepResource.getName());
                transientsPathBuilder.insert(0, "/");
                stepResource = stepResource.getParent();
            }
            transientsPathBuilder.deleteCharAt(0); // the slash coming from the root resource
            transientsPathBuilder.insert(0, AssetsConstants.PATH_TRANSIENTS);
            transientsPath = transientsPathBuilder.toString();
        }
        return transientsPath;
    }

    /**
     * the SelfConfig uses the asset itself as configuration (used if not configuration found)
     */
    public static class SelfConfig extends AssetConfig {

        public SelfConfig(Resource resource) {
            super(resource);
        }
    }
}
