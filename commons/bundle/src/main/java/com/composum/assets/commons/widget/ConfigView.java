package com.composum.assets.commons.widget;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.ImageConfig;
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
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.XSS;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.assets.commons.AssetsConstants.PATH_ASSET_CONFIG;
import static com.composum.assets.commons.AssetsConstants.PATH_IMAGE_CONFIG;
import static com.composum.assets.commons.config.ConfigHandle.EXTENSION;
import static com.composum.assets.commons.util.AssetConfigUtil.isConfigExtension;

/**
 * the model for each resource which can be used as a reference to an asset configuration; a resource can be:
 * - an asset or image configuration parent (asset image or configuration folder)
 * - an asset or image configuration resource ('assetconfig' or 'imageconfig')
 * - a variation or rendition configuration resource (a child of a configuration resource)
 * - each resource which is a child of a configuration folder
 */
public class ConfigView extends AbstractServletBean {

    public static final Pattern HOLDER_PATH = Pattern.compile("^(.*)/" + JcrConstants.JCR_CONTENT + "(/.*)?$");

    protected transient Size size;
    protected transient Crop crop;
    protected transient File file;
    protected transient Watermark watermark;
    protected transient Example example;
    protected transient Blur blur;

    protected ConfigHandle handle;
    protected ValueMap values;

    protected Resource config;
    protected List<String> selection = new ArrayList<>(); // [variation [, rendition]]

    public ConfigView() {
        super();
    }

    public ConfigView(BeanContext context, Resource resource) {
        super(context, resource);
    }

    /**
     * determines the configuration resource and the requested variation and rendition
     */
    public void initialize(BeanContext context, Resource resource) {
        resource = initConfig(context, resource);
        super.initialize(context, resource);
        initHandle(context, resource);
        initScope(context, resource);
    }

    /**
     * determines the configuration resource and the requested variation and rendition
     */
    protected Resource initConfig(BeanContext context, Resource resource) {
        if (resource != null) {
            ResourceResolver resolver = context.getResolver();
            SlingHttpServletRequest request = context.getRequest();
            RequestPathInfo pathInfo = request.getRequestPathInfo();
            config = getConfigResource(resource, selection); // check given resource...
            if (config == null) {
                String suffix = XSS.filter(pathInfo.getSuffix());
                if (StringUtils.isNotBlank(suffix)) { // use suffix as requestd resource...
                    Resource requested = resolver.getResource(suffix);
                    if (requested != null) {
                        resource = requested;
                        config = getConfigResource(requested, selection);
                    }
                }
            }
            if (config == null) {
                // if resource itself is not a configuration - check for a resource with a configuration child
                Resource configChild;
                if (AssetConfigUtil.isAssetConfigResource(configChild = resource.getChild(PATH_ASSET_CONFIG))) {
                    config = configChild;
                } else if (AssetConfigUtil.isAssetConfigResource(configChild = resource.getChild(PATH_IMAGE_CONFIG))) {
                    config = configChild;
                }
            }
            // determine the requested variation and rendition
            String param = XSS.filter(request.getParameter(AssetsConstants.VARIATION));
            if (StringUtils.isNotBlank(param)) {
                if (selection.size() > 0) {
                    selection.set(0, param);
                } else {
                    selection.add(param);
                }
            }
            if (selection.size() > 0) {
                param = XSS.filter(request.getParameter(AssetsConstants.RENDITION));
                if (StringUtils.isNotBlank(param)) {
                    if (selection.size() > 1) {
                        selection.set(1, param);
                    } else {
                        selection.add(param);
                    }
                }
            }
        }
        return resource;
    }

    /**
     * build the handle to access the inherited values of the requested configuration resource
     */
    protected void initHandle(BeanContext context, Resource resource) {
        if (ResourceUtil.isResourceType(config, AssetsConstants.NODE_TYPE_IMAGE_CONFIG)) {
            handle = new ImageConfig(AssetConfigUtil.imageConfigCascade(config));
        } else if (ResourceUtil.isResourceType(config, AssetsConstants.NODE_TYPE_ASSET_CONFIG)) {
            handle = new AssetConfig(AssetConfigUtil.assetConfigCascade(config));
        } else if (ResourceUtil.isResourceType(resource, JcrConstants.NT_FILE)
                || ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET)) {
            handle = new ImageConfig(AssetConfigUtil.imageConfigCascade(resource));
        } else {
            // retrieve the base configuration resource strting from the parent of the configuration holder...
            Resource baseConfig = findBaseConfig(getConfigHolder(config != null ? config : resource).getParent());
            if (baseConfig != null) {
                handle = new AssetConfig(AssetConfigUtil.assetConfigCascade(baseConfig));
            }
        }
    }

    /**
     * adjust the handle and reference the requested configuration aspect
     */
    protected void initScope(BeanContext context, Resource resource) {
        if (handle != null && selection.size() > 0) {
            AssetConfig assetConfig = handle.getAssetConfig();
            VariationConfig variation = assetConfig.findVariation(selection.get(0));
            if (selection.size() > 1) {
                handle = variation.findRendition(selection.get(1));
            } else {
                handle = variation;
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
        if (AssetConfigUtil.isAssetConfigResource(resource)) {
            return resource;
        }
        Resource parent = resource.getParent();
        if (AssetConfigUtil.isAssetConfigResource(parent)) {
            keys.add(resource.getName());
            return parent;
        } else if (parent != null) {
            Resource pparent = parent.getParent();
            if (AssetConfigUtil.isAssetConfigResource(pparent)) {
                keys.add(parent.getName());
                keys.add(resource.getName());
                return pparent;
            }
        }
        return null;
    }

    /**
     * determines the configuration resource which builds the base configuration of the given resource
     */
    public Resource findBaseConfig(Resource resource) {
        if (AssetConfigUtil.isAssetConfigResource(resource)) {
            resource = isConfigExtension(resource) ? getConfigHolder(resource) : null;
        }
        while (resource != null && (resource = resource.getParent()) != null) {
            Resource configChild;
            if (AssetConfigUtil.isAssetConfigResource(configChild = resource.getChild(PATH_ASSET_CONFIG))) {
                return configChild;
            } else if (AssetConfigUtil.isAssetConfigResource(configChild = resource.getChild(PATH_IMAGE_CONFIG))) {
                return configChild;
            }
        }
        return null;
    }

    public Resource getConfigHolder(Resource resource) {
        if (resource != null) {
            String path = resource.getPath();
            Matcher matcher = HOLDER_PATH.matcher(path);
            return resource.getResourceResolver().getResource(matcher.matches() ? matcher.group(1) : path);
        }
        return null;
    }

    public String getHolderPath() {
        String path = handle != null ? handle.getPath() : config != null ? config.getPath() : getResource().getPath();
        Matcher matcher = HOLDER_PATH.matcher(path);
        return matcher.matches() ? matcher.group(1) : path;
    }

    /**
     * @return the value set of this view (extension point, redefined by ConfigModel)
     */
    public ValueMap getValues() {
        if (values == null) {
            values = handle.getInherited();
        }
        return values;
    }

    /**
     * @return true, if predefined values should be used (redefined by ConfigModel)
     */
    protected boolean useDefault() {
        return true;
    }

    public ConfigHandle getHandle() {
        return handle;
    }

    public String getHandlePath() {
        return handle != null ? handle.getPath() : "";
    }

    public String getConfigPath() {
        return config != null ? config.getPath() : "";
    }

    public boolean isExtension() {
        return getValues().get(EXTENSION, AssetConfigUtil.isConfigExtension(getResource()));
    }

    public String getTitle() {
        return getValues().get(ResourceUtil.JCR_TITLE, "");
    }

    public String getDescription() {
        return getValues().get(ResourceUtil.JCR_DESCRIPTION, "");
    }

    public Size getSize() {
        if (size == null) {
            size = new Size(getValues(), useDefault());
        }
        return size;
    }

    public Crop getCrop() {
        if (crop == null) {
            crop = new Crop(getValues(), useDefault());
        }
        return crop;
    }

    public File getFile() {
        if (file == null) {
            file = new File(getValues(), useDefault());
        }
        return file;
    }

    public Watermark getWatermark() {
        if (watermark == null) {
            watermark = new Watermark(getValues(), useDefault());
        }
        return watermark;
    }

    public Example getExample() {
        if (example == null) {
            example = handle instanceof ImageConfig
                    ? ((ImageConfig) handle).getExample()
                    : new Example(getValues(), useDefault());
        }
        return example;
    }

    public Blur getBlur() {
        if (blur == null) {
            blur = new Blur(getValues(), useDefault());
        }
        return blur;
    }
}
