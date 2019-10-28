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
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.SyntheticResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model of a stored variation. The resource for this might point both to the original location in /content if there
 * is an original stored for the variation, or to the transient location below {@value AssetsConstants#PATH_TRANSIENTS}
 * if not.
 */
public class AssetVariation extends AssetHandle<VariationConfig> {

    public static final String NODE_TYPE = AssetsConstants.NODE_TYPE_VARIATION;
    public static final String RESOURCE_TYPE = AssetsConstants.RESOURCE_TYPE_VARIATION;

    public static final Map<String, Object> RENDITION_PROPERTIES;

    protected transient String transientsPath;

    static {
        RENDITION_PROPERTIES = new HashMap<>();
        RENDITION_PROPERTIES.put(ResourceUtil.PROP_PRIMARY_TYPE, AssetsConstants.NODE_TYPE_RENDITION);
        RENDITION_PROPERTIES.put(ResourceUtil.PROP_RESOURCE_TYPE, AssetsConstants.RESOURCE_TYPE_RENDITION);
    }

    @Nonnull
    protected final AbstractAsset asset;
    protected final VariationConfig config;

    public AssetVariation(@Nonnull BeanContext context, @Nonnull Resource resource, @Nonnull AbstractAsset asset) {
        super(context, resource);
        this.asset = asset;
        this.config = asset.getChildConfig(resource.getName());
    }

    @Override
    public VariationConfig getConfig() {
        return config;
    }

    @Override
    public RenditionConfig getChildConfig(String targetPath) {
        List<ResourceHandle> renditionCascade = asset.getConfigCascade(targetPath, RenditionConfig.NODE_TYPE);
        return renditionCascade != null && renditionCascade.size() > 0 ? new RenditionConfig(getConfig(), renditionCascade) : null;
    }

    @Override
    protected String getConfigTargetPath() {
        return getName();
    }

    /**
     * The folder where the transients are stored. For Variations, this is a subfolder {@link #getName()}/{originalversion} of
     * {@link #getAsset()}. {@link Asset#getTransientsPath()} which stores the renditions dependent on that original
     * version. *
     */
    @Override
    @Nonnull
    public String getTransientsPath() {
        if (transientsPath == null) {
            AssetRendition original = getOriginal();
            Resource fileresource = original.getFile() != null ? original.getFile().getResource() : null;
            String originalversion = fileresource != null ? versionMarker(fileresource) : null;
            transientsPath =
                    getAsset().getTransientsPath() + "/" + getName() + "/" + StringUtils.defaultIfBlank(originalversion,
                            AssetsConstants.NODE_WORKSPACECONFIGURED);
        }
        return transientsPath;
    }

    @Nonnull
    public AbstractAsset getAsset() {
        return asset;
    }

    @Nullable
    public AssetRendition getOriginal() {
        Resource originalResource = findChildByCategoryOrName(resource, AssetRendition.NODE_TYPE, ConfigHandle.ORIGINAL);
        AssetRendition original = originalResource != null ? new AssetRendition(context, originalResource, this) : null;
        if (original == null) {
            original = getAsset().retrieveOriginal(this);
        }
        return original;
    }

    /**
     * Returns the rendition according to the keys if there is one. If this is not an original, it's resource
     * can be a {@link NonExistingResource}.
     */
    @Nullable
    public AssetRendition getRendition(String... keyChain) {
        AssetRendition rendition = null;
        Resource renditionResource = findChildByCategoryOrName(resource, AssetRendition.NODE_TYPE, keyChain);
        if (renditionResource == null) {
            Resource transientsResource = getResolver().getResource(getTransientsPath());
            if (transientsResource != null) {
                renditionResource = findChildByCategoryOrName(transientsResource, AssetRendition.NODE_TYPE,
                        keyChain);
            }
        }
        if (renditionResource != null) {
            rendition = new AssetRendition(context, renditionResource, this);
        }
        return rendition;
    }

    /**
     * Returns the rendition according to the config. If this is not an original, it's resource
     * can be a {@link SyntheticResource} - mainly useful during the transients creation process.
     */
    @Nonnull
    public AssetRendition giveRendition(@Nonnull RenditionConfig renditionConfig) {
        AssetRendition rendition = null;
        Resource renditionResource = resource.getChild(renditionConfig.getName());
        if (renditionResource != null) {
            rendition = new AssetRendition(context, renditionResource, this);
        } else {
            SyntheticResource syntheticResource = new SyntheticResource(getResolver(),
                    getPath() + "/" + renditionConfig.getName(), AssetsConstants.NODE_TYPE_RENDITION);
            rendition = new AssetRendition(context, syntheticResource, this);
        }
        return rendition;
    }

    /** Creates an original rendition - which is stored in the /content resource tree. */
    public AssetRendition getOrCreateOriginal() throws PersistenceException {
        AssetRendition rendition = getOriginal();
        if (rendition == null) {
            Resource variationResource = getResource();
            Resource renditionResource = getResolver().create(variationResource, "original", RENDITION_PROPERTIES);
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
