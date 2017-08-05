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
import java.util.regex.Pattern;

public class AssetConfig extends ConfigHandle {

    public static final String CHILD_NAME = "assetconfig";
    public static final Pattern NAME_PATTERN = Pattern.compile("^" + CHILD_NAME + "$");
    public static final String NODE_TYPE = AssetsConstants.NODE_TYPE_ASSET_CONFIG;
    public static final String RESOURCE_TYPE = AssetsConstants.RESOURCE_TYPE_CONFIG;

    private transient Boolean extension;

    public AssetConfig(Resource resource) {
        this(Collections.singletonList(ResourceHandle.use(resource)));
    }

    public AssetConfig(List<ResourceHandle> cascade) {
        super(cascade);
    }

    public AssetConfig getAssetConfig() {
        return this;
    }

    public Boolean isExtension() {
        if (extension == null) {
            extension = getResource().getProperty(EXTENSION, Boolean.FALSE);
        }
        return extension;
    }

    public RenditionConfig getOriginal() {
        VariationConfig variation = retrieveVariation(ORIGINAL, DEFAULT);
        return variation != null ? variation.getOriginal() : null;
    }

    public VariationConfig getVariation(String key) {
        return retrieveVariation(key);
    }

    public VariationConfig findVariation(String... keyChain) {
        VariationConfig config = retrieveVariation(keyChain);
        if (config == null) {
            config = retrieveVariation(DEFAULT, ORIGINAL);
        }
        return config;
    }

    public VariationConfig retrieveVariation(String... keyChain) {
        List<ResourceHandle> variationCascade = findCascadeByCategoryOrName(VariationConfig.NODE_TYPE, keyChain);
        return variationCascade != null && variationCascade.size() > 0 ? new VariationConfig(this, variationCascade) : null;
    }

    public List<VariationConfig> getVariationList() {
        List<VariationConfig> result = new ArrayList<>();
        for (List<ResourceHandle> variations : getChildren(VariationConfig.NODE_TYPE).values()) {
            result.add(new VariationConfig(this, variations));
        }
        return result;
    }
}
