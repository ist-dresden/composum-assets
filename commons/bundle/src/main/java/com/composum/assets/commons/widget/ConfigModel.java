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
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.assets.commons.AssetsConstants.PATH_ASSET_CONFIG;
import static com.composum.assets.commons.AssetsConstants.PATH_IMAGE_CONFIG;
import static com.composum.assets.commons.AssetsConstants.RESOURCE_TYPE_CONFIG;
import static com.composum.assets.commons.config.ConfigHandle.CATEGORIES;
import static com.composum.assets.commons.config.ConfigHandle.DEFAULT;
import static com.composum.assets.commons.config.ConfigHandle.ORIGINAL;

/**
 * the model for each resource which can be used as a reference to an asset configuration; a resource can be:
 * - an asset od image configuration parent (asset image or configuration folder)
 * - an asset or image configuration resource ('assetconfig' or 'imageconfig')
 * - a variation or rendition configuration resource (a child of a configuration resource)
 * - each resource which is a child of a configuration folder (only the 'base' scope is usable)
 */
public class ConfigModel extends AbstractServletBean {

    public static final Pattern HOLDER_PATH = Pattern.compile("^(.*)/" + JcrConstants.JCR_CONTENT + "(/.*)?$");

    public static final ValueMap INHERITED_ONLY = new ValueMapDecorator(Collections.emptyMap());

    enum Scope {base, node, variation, rendition}

    private transient Size size;
    private transient Crop crop;
    private transient File file;
    private transient Watermark watermark;
    private transient Example example;
    private transient Blur blur;
    private transient List<String> categories;
    private transient ResourceFilter filter;

    private ConfigHandle base;
    private ConfigHandle handle;
    private VariationConfig variation;
    private RenditionConfig rendition;
    private ValueMap values;
    private Scope scope;
    private boolean valid = true;

    private transient List<VariationConfig> variations;
    private transient List<RenditionConfig> renditions;

    private transient String contentRoot;

    public ConfigModel() {
        super();
    }

    public ConfigModel(BeanContext context, Resource resource) {
        super(context, resource);
    }

    /**
     * determines the configuration resource and the requested variation and rendition
     */
    public void initialize(BeanContext context, Resource resource) {
        if (resource != null) {
            ResourceResolver resolver = context.getResolver();
            SlingHttpServletRequest request = context.getRequest();
            RequestPathInfo pathInfo = request.getRequestPathInfo();
            String[] selectors = pathInfo.getSelectors(); // the scope is driven by selectors
            List<String> keys = new ArrayList<>(); // [variation [, rendition]] extracted from path
            Resource config = getConfigResource(resource, keys); // check given resource...
            if (config == null) {
                String suffix = pathInfo.getSuffix();
                if (StringUtils.isNotBlank(suffix)) { // use suffix as requestd resource...
                    Resource requested = resolver.getResource(suffix);
                    if (requested != null) {
                        resource = requested;
                        config = getConfigResource(requested, keys);
                    }
                }
            }
            if (config == null) {
                // if resource itself is not a configuration - check for a resource with a configuration child
                Resource configChild;
                if (isConfigResource(configChild = resource.getChild(PATH_ASSET_CONFIG))) {
                    config = configChild;
                } else if (isConfigResource(configChild = resource.getChild(PATH_IMAGE_CONFIG))) {
                    config = configChild;
                }
            }
            // retrieve the base configuration resource strting from the parent of the configuration holder...
            Resource baseConfig = findBaseConfig(getConfigHolder(config != null ? config : resource).getParent());
            if (baseConfig != null) {
                base = new AssetConfig(AssetConfigUtil.assetConfigCascade(baseConfig));
            }
            // build the handle to access the inherited values of the requested configuration resource
            if (ResourceUtil.isResourceType(config, AssetsConstants.NODE_TYPE_ASSET_CONFIG)) {
                handle = new AssetConfig(AssetConfigUtil.assetConfigCascade(config));
            } else if (ResourceUtil.isResourceType(config, AssetsConstants.NODE_TYPE_IMAGE_CONFIG)) {
                handle = new ImageConfig(AssetConfigUtil.imageConfigCascade(config));
            } else if (ResourceUtil.isResourceType(resource, JcrConstants.NT_FILE)
                    || ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET)) {
                handle = new ImageConfig(AssetConfigUtil.imageConfigCascade(resource));
            } else {
                handle = base;
                valid = false; // if the base is the only available configuration no editing is possible
            }
            if (config == null) {
                // the final fallback is a synthetic configuration child of the requested resource
                config = handle != null ? handle.getAssetConfig().getResource()
                        : new SyntheticResource(resource.getResourceResolver(),
                        resource.getPath() + "/" + PATH_ASSET_CONFIG,
                        RESOURCE_TYPE_CONFIG);
                valid = false; // in this case no real configuration is present - no editing possible
            }
            super.initialize(context, config);
            if (handle != null) {
                // determine the requested variation and rendition configuration of the used configuration
                String[] key = request.getParameterValues(AssetsConstants.VARIATION);
                if (key == null && keys.size() > 0) {
                    key = new String[]{keys.get(0)};
                }
                variation = handle.getAssetConfig().findVariation(key != null ? key : new String[0]);
                if (variation == null) {
                    List<VariationConfig> set = getVariations();
                    if (set.size() > 0) {
                        variation = set.get(0);
                    }
                }
                if (variation != null) {
                    key = request.getParameterValues(AssetsConstants.RENDITION);
                    if (key == null && keys.size() > 1) {
                        key = new String[]{keys.get(1)};
                    }
                    rendition = variation.findRendition(key != null ? key : new String[0]);
                    if (rendition == null) {
                        List<RenditionConfig> set = getRenditions();
                        if (set.size() > 0) {
                            rendition = set.get(0);
                        }
                    }
                }
            }
            if (scope == null) {
                try {
                    scope = Scope.valueOf(selectors.length > 0 ? selectors[0] : "node");
                } catch (IllegalArgumentException ex) {
                    scope = Scope.node;
                }
            }
            // build the 'values' (for editing) and the final 'handle' (for inheritance)
            switch (scope == Scope.base ? Scope.rendition : scope) {
                case variation:
                    if (variation != null) {
                        values = variation.getResource().getValueMap();
                        handle = new VariationConfig(handle.getAssetConfig(), variation.getResource());
                    }
                    break;
                case rendition:
                    if (rendition != null && variation != null) {
                        // in the case of the 'base' scope - nothing can be edited / all values are inherited
                        values = scope == Scope.base ? INHERITED_ONLY : rendition.getResource().getValueMap();
                        handle = new RenditionConfig(
                                new VariationConfig(variation.getAssetConfig(), variation.getResource()),
                                rendition.getResource());
                    }
                    break;
            }
            if (values == null) {
                // in the 'node' scope the values of the configuration node itself are edited
                values = config.getValueMap();
            }
        }
    }

    /**
     * determines a configuration resource of the given resource and fills the keys with
     * the variation and rendition names if they can be derived from the given resource
     *
     * @param resource the reference to find the configuration resource
     * @param keys     an empty list filled id a configuration found and variation/rendition can be determined
     * @return the configuration found or 'null'
     */
    public Resource getConfigResource(Resource resource, List<String> keys) {
        if (isConfigResource(resource)) {
            return resource;
        }
        Resource parent = resource.getParent();
        if (isConfigResource(parent)) {
            keys.add(resource.getName());
            return parent;
        } else if (parent != null) {
            Resource pparent = parent.getParent();
            if (isConfigResource(pparent)) {
                keys.add(parent.getName());
                keys.add(resource.getName());
                return pparent;
            }
        }
        return null;
    }

    public Resource findBaseConfig(Resource resource) {
        while (resource != null && !isConfigResource(resource)) {
            Resource configChild;
            if (isConfigResource(configChild = resource.getChild(PATH_ASSET_CONFIG))) {
                return configChild;
            } else if (isConfigResource(configChild = resource.getChild(PATH_IMAGE_CONFIG))) {
                return configChild;
            }
            resource = resource.getParent();
        }
        return resource;
    }

    public boolean isConfigResource(Resource resource) {
        return ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET_CONFIG)
                || ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_IMAGE_CONFIG);
    }

    public Resource getConfigHolder(Resource resource) {
        if (resource != null) {
            String path = resource.getPath();
            Matcher matcher = HOLDER_PATH.matcher(path);
            return resource.getResourceResolver().getResource(matcher.matches() ? matcher.group(1) : path);
        }
        return null;
    }

    public boolean isValid() {
        return valid;
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

    public String getBasePath() {
        return base != null ? base.getPath() : "";
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
