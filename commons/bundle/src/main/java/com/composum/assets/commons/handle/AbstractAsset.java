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
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
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

    /**
     * Returns the variation if there is a resource for it.
     */
    @Nullable
    public AssetVariation getVariation(String... keyChain) {
        AssetVariation variation = null;
        Resource variationResource = findChildByCategoryOrName(resource, AssetVariation.NODE_TYPE, keyChain);
        if (variationResource != null) {
            variation = new AssetVariation(context, variationResource, this);
        } else {
            Resource transientsResource = getResolver().getResource(getAsset().getTransientsPath());
            if (transientsResource != null) {
                Resource transientChild = findChildByCategoryOrName(transientsResource, AssetVariation.NODE_TYPE, keyChain);
                if (transientChild != null) {
                    variation = new AssetVariation(context, transientChild, this);
                }
            }
        }
        return variation;
    }

    /** Returns a variation for the given config, possibly on a NonExistingResource if there was nothing created yet. */
    @Nonnull
    public AssetVariation giveVariation(@Nonnull VariationConfig config) {
        AssetVariation variation = getVariation(config.getName());
        if (variation == null) {
            variation = new AssetVariation(context, new NonExistingResource(getResolver(), resource.getPath() +
                    "/" + config.getName()), this);
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
    public VariationConfig getChildConfig(String targetPath) {
        AbstractAsset asset = getAsset();
        List<ResourceHandle> variationCascade = asset.getConfigCascade(targetPath, VariationConfig.NODE_TYPE);
        return variationCascade != null && variationCascade.size() > 0 ? new VariationConfig(getConfig(), variationCascade) : null;
    }

    public List<ResourceHandle> getConfigCascade(String targetPath, String configResourceType) {
        return getConfig().getCascadeForPath(configResourceType, targetPath);
    }

    @Override
    protected String getConfigTargetPath() {
        return "";
    }

    /* Doc inherited: @see AssetHandle#getTransientsPath() */
    @Override
    @Nonnull
    public String getTransientsPath() {
        if (transientsPath == null) {
            StringBuilder transientsPathBuilder = new StringBuilder();
            Resource stepResource = resource;
            while (stepResource != null && !"/".equals(stepResource.getPath())) {
                String versionnodename = versionMarker(stepResource);
                if (versionnodename != null) {
                    transientsPathBuilder.insert(0, versionnodename);
                    transientsPathBuilder.insert(0, "/");
                }

                transientsPathBuilder.insert(0, stepResource.getName());
                transientsPathBuilder.insert(0, "/");
                stepResource = stepResource.getParent();
            }
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
