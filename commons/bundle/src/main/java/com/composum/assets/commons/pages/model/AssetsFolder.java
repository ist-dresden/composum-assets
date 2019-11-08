/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.pages.model;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.util.AssetConfigUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AssetsFolder extends PagesFrameModel {

    private transient Boolean assetConfigAvailable;
    private transient AssetConfig assetConfig;
    private transient List<VariationConfig> variations;

    public AssetsFolder(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public AssetsFolder() {
    }

    public boolean isAssetConfigAvailable() {
        if (assetConfigAvailable == null) {
            assetConfigAvailable = getAssetConfig() != null;
        }
        return assetConfigAvailable;
    }

    @Nullable
    public AssetConfig getAssetConfig() {
        if (assetConfig == null) {
            List<ResourceHandle> cascade = AssetConfigUtil.assetConfigCascade(getResource());
            if (cascade.size() > 0) {
                assetConfig = new AssetConfig(cascade);
            }
        }
        return assetConfig;
    }

    @Nonnull
    public List<VariationConfig> getVariations() {
        if (variations == null) {
            AssetConfig config = getAssetConfig();
            if (config != null) {
                variations = config.getVariationList();
            } else {
                variations = new ArrayList<>();
            }
        }
        return variations;
    }
}
