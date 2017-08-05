/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.handle;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetVariation extends AssetHandle<VariationConfig> {

    public static final String NODE_TYPE = AssetsConstants.NODE_TYPE_VARIATION;
    public static final String RESOURCE_TYPE = AssetsConstants.RESOURCE_TYPE_VARIATION;

    public static final Map<String, Object> RENDITION_PROPERTIES;

    static {
        RENDITION_PROPERTIES = new HashMap<>();
        RENDITION_PROPERTIES.put(ResourceUtil.PROP_PRIMARY_TYPE, AssetsConstants.NODE_TYPE_RENDITION);
        RENDITION_PROPERTIES.put(ResourceUtil.PROP_RESOURCE_TYPE, AssetsConstants.RESOURCE_TYPE_RENDITION);
    }

    protected final AbstractAsset asset;
    protected final VariationConfig config;

    public AssetVariation(BeanContext context, Resource resource, AbstractAsset asset) {
        super(context, resource);
        this.asset = asset;
        this.config = asset.getChildConfig(resource);
    }

    public VariationConfig getConfig() {
        return config;
    }

    public RenditionConfig getChildConfig(Resource resource) {
        AbstractAsset asset = getAsset();
        List<ResourceHandle> renditionCascade = asset.getConfigCascade(resource, RenditionConfig.NODE_TYPE);
        return renditionCascade != null && renditionCascade.size() > 0 ? new RenditionConfig(getConfig(), renditionCascade) : null;
    }

    public AbstractAsset getAsset() {
        return asset;
    }

    public AssetRendition getOriginal() {
        AssetRendition original = getRendition(ConfigHandle.ORIGINAL);
        if (original == null) {
            original = getAsset().retrieveOriginal(this);
        }
        return original;
    }

    public AssetRendition getRendition(String... keyChain) {
        AssetRendition rendition = null;
        Resource renditionResource = findChildByCategoryOrName(AssetRendition.NODE_TYPE, keyChain);
        if (renditionResource != null) {
            rendition = new AssetRendition(context, renditionResource, this);
        }
        return rendition;
    }

    public AssetRendition getOrCreateOriginal() throws PersistenceException {
        AssetRendition rendition = getOriginal();
        if (rendition == null) {
            Resource variationResource = getResource();
            ResourceResolver resolver = variationResource.getResourceResolver();
            Resource renditionResource = resolver.create(variationResource,"original", RENDITION_PROPERTIES);
            rendition = new AssetRendition(context, renditionResource, this);
        }
        return rendition;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AssetVariation && getPath().equals(((AssetVariation) other).getPath());
    }

    @Override
    public int hashCode() {
        return getPath().hashCode();
    }
}
