/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.config;

import com.composum.assets.commons.AssetsConstants;
import com.composum.sling.core.ResourceHandle;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VariationConfig extends ConfigHandle {

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

    public AssetConfig getAssetConfig() {
        return assetConfig;
    }

    public RenditionConfig getOriginal() {
        return getRendition(ORIGINAL);
    }

    public RenditionConfig getRendition(String key) {
        return retrieveRendition(key);
    }

    public RenditionConfig findRendition(String... keyChain) {
        RenditionConfig config = retrieveRendition(keyChain);
        if (config == null) {
            config = retrieveRendition(DEFAULT, ORIGINAL);
        }
        return config;
    }

    public RenditionConfig retrieveRendition(String... keyChain) {
        List<ResourceHandle> renditionCascade = findCascadeByCategoryOrName(RenditionConfig.NODE_TYPE, keyChain);
        return renditionCascade != null && renditionCascade.size() > 0 ? new RenditionConfig(this, renditionCascade) : null;
    }

    public List<RenditionConfig> getRenditionList() {
        List<RenditionConfig> result = new ArrayList<>();
        for (List<ResourceHandle> renditionCascade : getChildren(RenditionConfig.NODE_TYPE).values()) {
            result.add(new RenditionConfig(this, renditionCascade));
        }
        return result;
    }
}
