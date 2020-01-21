package com.composum.assets.commons.widget;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.ImageConfig;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.config.aspect.Crop;
import com.composum.assets.commons.config.aspect.File;
import com.composum.assets.commons.config.aspect.Size;
import com.composum.assets.commons.config.aspect.Watermark;
import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.Collections;
import java.util.List;

public class ConfigModel extends AbstractServletBean {

    private transient Size size;
    private transient Crop crop;
    private transient File file;
    private transient Watermark watermark;
    private transient ResourceFilter filter;

    private transient ValueMap values;

    private ConfigHandle handle;
    private VariationConfig variation;
    private RenditionConfig rendition;

    private transient List<VariationConfig> variations;
    private transient List<RenditionConfig> renditions;

    public ConfigModel() {
        super();
    }

    public ConfigModel(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public void initialize(BeanContext context, Resource resource) {
        if (!isConfigResource(resource)) {
            String suffix = context.getRequest().getRequestPathInfo().getSuffix();
            if (StringUtils.isNotBlank(suffix)) {
                Resource config = context.getResolver().getResource(suffix);
                if (isConfigResource(config)) {
                    resource = config;
                }
            }
        }
        super.initialize(context, resource);
        if (ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET_CONFIG)) {
            handle = new AssetConfig(resource);
        } else if (ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_IMAGE_CONFIG)) {
            handle = new ImageConfig(resource);
        }
        if (handle != null) {
            String[] key = context.getRequest().getParameterValues(AssetsConstants.VARIATION);
            variation = handle.getAssetConfig().findVariation(key != null ? key : new String[0]);
            if (variation == null) {
                List<VariationConfig> set = getVariations();
                if (set.size() > 0) {
                    variation = set.get(0);
                }
            }
            if (variation != null) {
                key = context.getRequest().getParameterValues(AssetsConstants.RENDITION);
                rendition = variation.findRendition(key != null ? key : new String[0]);
                if (rendition == null) {
                    List<RenditionConfig> set = getRenditions();
                    if (set.size() > 0) {
                        rendition = set.get(0);
                    }
                }
            }
        }
    }

    public boolean isConfigResource(Resource resource) {
        return ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET_CONFIG)
                || ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_IMAGE_CONFIG);
    }

    public ConfigHandle getHandle() {
        return handle;
    }

    public Size getSize() {
        if (size == null) {
            size = new Size(getValues(), false);
        }
        return size;
    }

    public Crop getCrop() {
        if (crop == null) {
            crop = new Crop(getValues(), false);
        }
        return crop;
    }

    public File getFile() {
        if (file == null) {
            file = new File(getValues(), false);
        }
        return file;
    }

    public Watermark getWatermark() {
        if (watermark == null) {
            watermark = new Watermark(getValues(), false);
        }
        return watermark;
    }

    public ValueMap getValues() {
        if (values == null) {
            values = resource.getValueMap();
        }
        return values;
    }

    public VariationConfig getVariation() {
        return variation;
    }

    public List<VariationConfig> getVariations() {
        if (variations == null) {
            if (handle != null) {
                variations = handle.getAssetConfig().getVariationList();
            } else {
                variations = Collections.emptyList();
            }
        }
        return variations;
    }

    public RenditionConfig getRendition() {
        return rendition;
    }

    public List<RenditionConfig> getRenditions() {
        if (renditions == null) {
            if (variation != null) {
                renditions = variation.getRenditionList();
            } else {
                renditions = Collections.emptyList();
            }
        }
        return renditions;
    }
}
