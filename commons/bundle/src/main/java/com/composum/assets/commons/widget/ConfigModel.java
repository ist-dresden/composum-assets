package com.composum.assets.commons.widget;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.ImageConfig;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.config.aspect.Crop;
import com.composum.assets.commons.config.aspect.Example;
import com.composum.assets.commons.config.aspect.File;
import com.composum.assets.commons.config.aspect.Size;
import com.composum.assets.commons.config.aspect.Watermark;
import com.composum.assets.commons.config.transform.Blur;
import com.composum.assets.commons.util.AssetConfigUtil;
import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.resource.ValueMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.assets.commons.config.ConfigHandle.CATEGORIES;
import static com.composum.assets.commons.config.ConfigHandle.DEFAULT;
import static com.composum.assets.commons.config.ConfigHandle.ORIGINAL;

public class ConfigModel extends AbstractServletBean {

    public static final Pattern HOLDER_PATH = Pattern.compile("^(.*)/" + JcrConstants.JCR_CONTENT + "(/.*)?$");

    enum Scope {base, node, variation, rendition}

    private transient Size size;
    private transient Crop crop;
    private transient File file;
    private transient Watermark watermark;
    private transient Example example;
    private transient Blur blur;
    private transient List<String> categories;
    private transient ResourceFilter filter;

    private ConfigHandle handle;
    private VariationConfig variation;
    private RenditionConfig rendition;
    private ValueMap values;
    private Scope scope;

    private transient List<VariationConfig> variations;
    private transient List<RenditionConfig> renditions;

    private transient String contentRoot;

    public ConfigModel() {
        super();
    }

    public ConfigModel(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public void initialize(BeanContext context, Resource resource) {
        SlingHttpServletRequest request = context.getRequest();
        RequestPathInfo pathInfo = request.getRequestPathInfo();
        String[] selectors = pathInfo.getSelectors();
        if (!isConfigResource(resource)) {
            String suffix = context.getRequest().getRequestPathInfo().getSuffix();
            if (StringUtils.isNotBlank(suffix)) {
                Resource config = context.getResolver().getResource(suffix);
                if (isConfigResource(config)) {
                    resource = config;
                }
            }
        }
        if (!isConfigResource(resource) && resource != null) {
            Resource config = resource.getChild(JcrConstants.JCR_CONTENT + "/assetconfig");
            if (isConfigResource(config)) {
                resource = config;
            }
        }
        if (ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET_CONFIG)) {
            handle = new AssetConfig(AssetConfigUtil.assetConfigCascade(resource));
        } else if (ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_IMAGE_CONFIG)
                || ResourceUtil.isResourceType(resource, JcrConstants.NT_FILE)
                || ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET)) {
            handle = new ImageConfig(AssetConfigUtil.imageConfigCascade(resource));
        }
        if (!isConfigResource(resource) && resource != null) {
            resource = new SyntheticResource(resource.getResourceResolver(),
                    resource.getPath() + "/" + JcrConstants.JCR_CONTENT + "/assetconfig",
                    "composum/assets/config");
        }
        super.initialize(context, resource);
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
        try {
            scope = Scope.valueOf(selectors.length > 0 ? selectors[0] : "node");
        } catch (IllegalArgumentException ex) {
            scope = Scope.node;
        }
        switch (scope) {
            case variation:
                if (variation != null) {
                    values = variation.getResource().getValueMap();
                    handle = new VariationConfig(handle.getAssetConfig(), variation.getResource());
                }
                break;
            case rendition:
                if (rendition != null) {
                    values = rendition.getResource().getValueMap();
                    handle = new RenditionConfig(
                            new VariationConfig(handle.getAssetConfig(), variation.getResource()),
                            variation.getResource());
                }
                break;
        }
        if (values == null && resource != null) {
            values = resource.getValueMap();
        }
    }

    public boolean isConfigResource(Resource resource) {
        return ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET_CONFIG)
                || ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_IMAGE_CONFIG);
    }

    public boolean isDisabled() {
        return true;
    }

    public Scope getScope() {
        return scope;
    }

    public String getContentRoot() {
        if (contentRoot == null) {
            contentRoot = context.getService(AssetsConfiguration.class).getContentRoot();
        }
        return contentRoot;
    }

    public String getHolderPath() {
        String path = getPath();
        Matcher matcher = HOLDER_PATH.matcher(path);
        return matcher.matches() ? matcher.group(1) : path;
    }

    public ConfigHandle getHandle() {
        return handle;
    }

    public List<String> getCategories() {
        if (categories == null) {
            categories = Arrays.asList(values.get(CATEGORIES, new String[0]));
        }
        return categories;
    }

    public List<String> getCategoriesSet() {
        List<String> categories = getCategories();
        return categories.size() > 0 ? categories : Collections.singletonList("");
    }

    public boolean isDefault() {
        return getCategories().contains(DEFAULT);
    }

    public boolean isOriginal() {
        return getCategories().contains(ORIGINAL);
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

    public Example getExample() {
        if (example == null) {
            example = new Example(getValues(), false);
        }
        return example;
    }

    public Blur getBlur() {
        if (blur == null) {
            blur = new Blur(getValues(), false);
        }
        return blur;
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
