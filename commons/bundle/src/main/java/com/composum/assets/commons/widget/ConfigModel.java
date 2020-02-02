package com.composum.assets.commons.widget;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ImageConfig;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.util.AssetConfigUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.I18N;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import static com.composum.assets.commons.config.ConfigHandle.CATEGORY;
import static com.composum.assets.commons.config.ConfigHandle.DEFAULT;
import static com.composum.assets.commons.config.ConfigHandle.ORIGINAL;

/**
 * the model for each resource which can be used as a reference to an asset configuration; a resource can be:
 * - an asset or image configuration parent (asset image or configuration folder)
 * - an asset or image configuration resource ('assetconfig' or 'imageconfig')
 * - a variation or rendition configuration resource (a child of a configuration resource)
 * - each resource which is a child of a configuration folder (only the 'base' scope is usable)
 */
public class ConfigModel extends ConfigView {

    public static final ValueMap INHERITED_ONLY = new ValueMapDecorator(Collections.emptyMap());

    enum Scope {base, node, variation, rendition}

    private transient List<String> categorySet;
    private transient ResourceFilter filter;

    protected AssetConfig base;
    protected VariationConfig variation;
    protected RenditionConfig rendition;

    protected Scope scope;
    protected String configType;
    protected String actionUrl;
    protected boolean valid = true;

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
     * build the handle to access the inherited values of the requested configuration resource
     */
    @Override
    protected void initHandle(BeanContext context, Resource resource) {
        // retrieve the base configuration resource strting from the parent of the configuration holder...
        Resource baseConfig = findBaseConfig(config != null ? config : resource);
        if (baseConfig != null) {
            base = new AssetConfig(AssetConfigUtil.assetConfigCascade(baseConfig));
        }
        // build the handle to access the inherited values of the requested configuration resource
        if (ResourceUtil.isResourceType(config, AssetsConstants.NODE_TYPE_IMAGE_CONFIG)) {
            // an image asset with its own image configuration extension
            handle = new ImageConfig(AssetConfigUtil.imageConfigCascade(config));
        } else if (ResourceUtil.isResourceType(config, AssetsConstants.NODE_TYPE_ASSET_CONFIG)) {
            // an asset configuration is available at the selected resource (configuration folder)
            handle = new AssetConfig(AssetConfigUtil.assetConfigCascade(config));
        } else if (ResourceUtil.isResourceType(resource, JcrConstants.NT_FILE)
                || ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET)) {
            // preview of an asset resource with its base configuration
            handle = new ImageConfig(AssetConfigUtil.imageConfigCascade(resource));
            configType = ImageConfig.CHILD_NAME;
            if (config == null) {
                scope = Scope.base;
            }
        } else {
            // an intermediate resource (e.g. folder without configuration)
            scope = Scope.base;
            handle = base;
            valid = false; // if the base is the only available configuration no editing is possible
        }
        if (config == null) {
            valid = false; // in this case no real configuration is present - no editing possible
        }
    }

    /**
     * adjust the handle and reference the requested configuration aspect
     */
    @Override
    protected void initScope(BeanContext context, Resource resource) {
        if (handle != null) {
            variation = handle.getAssetConfig().findVariation(selection.size() > 0
                    ? new String[]{selection.get(0)} : new String[0]);
            if (variation == null) {
                List<VariationConfig> set = getVariations();
                if (set.size() > 0) {
                    variation = set.get(0);
                }
            }
            if (variation != null) {
                rendition = variation.findRendition(selection.size() > 1
                        ? new String[]{selection.get(1)} : new String[0]);
                if (rendition == null) {
                    List<RenditionConfig> set = getRenditions();
                    if (set.size() > 0) {
                        rendition = set.get(0);
                    }
                }
            }
        }
        if (scope == null) {
            String[] selectors = context.getRequest().getRequestPathInfo().getSelectors();
            try {
                scope = Scope.valueOf(selectors.length > 0 ? selectors[0] : "node");
            } catch (IllegalArgumentException ex) {
                try {
                    scope = Scope.valueOf(selectors.length > 1 ? selectors[1] : "node");
                } catch (IllegalArgumentException exx) {
                    scope = Scope.node;
                }
            }
        }
        // build the 'values' (for editing) and the final 'handle' (for inheritance)
        switch (scope) {
            case base:
                if (base != null) {
                    config = base.getResource();
                    if (variation != null && rendition != null) {
                        variation = base.findVariation(variation.getName());
                        if (variation != null) {
                            handle = rendition = variation.findRendition(rendition.getName());
                        }
                    }
                } else {
                    if (rendition != null) {
                        handle = rendition;
                    }
                }
                values = INHERITED_ONLY;
                break;
            case node:
                actionUrl = config.getPath();
                break;
            case variation:
                if (variation != null) {
                    handle = variation;
                    values = variation.getResource().getValueMap();
                    actionUrl = variation.getPath();
                }
                break;
            case rendition:
                if (rendition != null) {
                    handle = rendition;
                    values = rendition.getResource().getValueMap();
                    actionUrl = rendition.getPath();
                }
                break;
        }
        if (values == null) {
            // in the 'node' scope the values of the configuration node itself are edited
            values = config != null ? config.getValueMap() : INHERITED_ONLY;
        }
    }

    @Override
    public ValueMap getValues() {
        if (values == null) {
            values = resource.getValueMap();
        }
        return values;
    }

    @Override
    protected boolean useDefault() {
        return false;
    }

    public boolean isValid() {
        return valid;
    }

    public String getConfigType() {
        return configType != null ? configType : handle != null ? handle.getConfigType() : "";
    }

    public String getConfigLabel() {
        switch (scope) {
            case variation:
                return " - " + I18N.get(getRequest(), "Variation") + ": "
                        + variation.getName();
            case rendition:
                return " - " + I18N.get(getRequest(), "Rendition") + ": "
                        + rendition.getVariation().getName() + " / " + rendition.getName();
            default:
                return "";
        }
    }

    public String getCurrentPath() {
        String path = getPath();
        Matcher matcher = HOLDER_PATH.matcher(path);
        return matcher.matches() ? matcher.group(1) : path;
    }

    public String getActionUrl() {
        return actionUrl;
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

    public boolean getHasBase() {
        return base != null;
    }

    public String getBaseHolder() {
        String path = getBasePath();
        Matcher matcher = HOLDER_PATH.matcher(path);
        return matcher.matches() ? matcher.group(1) : path;
    }

    public String getBasePath() {
        return base != null ? base.getPath() : "";
    }

    public List<String> getCategory() {
        if (categorySet == null) {
            categorySet = Arrays.asList(getValues().get(CATEGORY, new String[0]));
        }
        return categorySet;
    }

    public List<String> getCategorySet() {
        List<String> categorySet = getCategory();
        return categorySet.size() > 0 ? categorySet : Collections.singletonList("");
    }

    public boolean isDefault() {
        return getCategory().contains(DEFAULT);
    }

    public boolean isOriginal() {
        return getCategory().contains(ORIGINAL);
    }

    public VariationConfig getVariation() {
        return variation;
    }

    public List<VariationConfig> getVariations() {
        if (variations == null) {
            if (handle != null) {
                variations = handle.getAssetConfig().getVariationList(false);
                Collections.sort(variations);
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
                renditions = variation.getRenditionList(false);
                Collections.sort(renditions);
            } else {
                renditions = Collections.emptyList();
            }
        }
        return renditions;
    }
}
