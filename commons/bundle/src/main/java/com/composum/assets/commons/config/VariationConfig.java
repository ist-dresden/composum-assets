/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.config;

import com.composum.assets.commons.AssetsConstants;
import com.composum.sling.core.ResourceHandle;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.composum.assets.commons.AssetsConstants.VARIATION;

public class VariationConfig extends ConfigHandle implements Comparable<VariationConfig> {

    public static final String NODE_TYPE = AssetsConstants.NODE_TYPE_VARIATION_CONFIG;
    public static final String RESOURCE_TYPE = AssetsConstants.RESOURCE_TYPE_VARIATION_CONFIG;

    protected final AssetConfig assetConfig;

    public VariationConfig(AssetConfig assetConfig, Resource resource) {
        this(assetConfig, Collections.singletonList(ResourceHandle.use(resource)));
    }

    public VariationConfig(AssetConfig assetConfig, List<ResourceHandle> cascade) {
        super(cascade);
        this.assetConfig = assetConfig;
    }

    @Override
    public String getConfigType() {
        return VARIATION;
    }

    @Override
    public AssetConfig getAssetConfig() {
        return assetConfig;
    }

    @Override
    public RenditionConfig getOriginal() {
        return getRendition(ORIGINAL);
    }

    public RenditionConfig getRendition(String key) {
        return retrieveRendition(false, key);
    }

    public RenditionConfig findRendition(String... keyChain) {
        RenditionConfig config = retrieveRendition(true, keyChain);
        if (config == null) {
            config = retrieveRendition(false, ORIGINAL);
        }
        return config;
    }

    public RenditionConfig retrieveRendition(boolean fallbackToDefault, String... keyChain) {
        List<ResourceHandle> renditionCascade = findCascadeByCategoryOrName(fallbackToDefault, RenditionConfig.NODE_TYPE, keyChain);
        return renditionCascade != null && renditionCascade.size() > 0 ? new RenditionConfig(this, renditionCascade) : null;
    }

    public List<RenditionConfig> getRenditionList(boolean cumulated) {
        List<RenditionConfig> result = new ArrayList<>();
        for (List<ResourceHandle> renditionCascade : getChildren(RenditionConfig.NODE_TYPE, cumulated).values()) {
            result.add(new RenditionConfig(this, renditionCascade));
        }
        return result;
    }

    @Override
    public int compareTo(@NotNull VariationConfig other) {
        return getName().compareTo(other.getName());
    }
}
