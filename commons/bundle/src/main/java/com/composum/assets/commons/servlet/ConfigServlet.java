package com.composum.assets.commons.servlet;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.service.AssetsService;
import com.composum.assets.commons.service.MetaPropertiesService;
import com.composum.assets.commons.util.AssetConfigUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.servlet.AbstractServiceServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.servlet.Status;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The servlet to provide changes of the Asset Managers UI.
 */
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Assets Configuration Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/assets/config",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST
        })
public class ConfigServlet extends AbstractServiceServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigServlet.class);

    @Reference
    protected AssetsConfiguration assetsConfiguration;

    @Reference
    protected AssetsService assetsService;

    @Reference
    protected MetaPropertiesService metaPropertiesService;

    protected BundleContext bundleContext;

    @Activate
    @Modified
    protected void activate(ComponentContext context) {
        this.bundleContext = context.getBundleContext();
    }

    //
    // Servlet operations
    //

    public enum Extension {
        html, json
    }

    public enum Operation {
        variations, renditions,
        create, copy, delete
    }

    protected AssetsOperationSet operations = new AssetsOperationSet();

    @Override
    protected AssetsOperationSet getOperations() {
        return operations;
    }

    @Override
    protected boolean isEnabled() {
        return true;
    }

    /**
     * setup of the servlet operation set for this servlet instance
     */
    @Override
    public void init() throws ServletException {
        super.init();

        // GET
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.variations, new GetVariationsOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.renditions, new GetRenditionsOperation());

        // POST
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.create, new ConfigCreateOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.copy, new ConfigCopyOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.delete, new ConfigDeleteOperation());
    }

    public class AssetsOperationSet extends ServletOperationSet<Extension, Operation> {

        public AssetsOperationSet() {
            super(Extension.json);
        }
    }

    //
    // operation implementations
    //

    public static boolean isAssetConfigResource(@Nullable final Resource resource) {
        return ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_IMAGE_CONFIG) ||
                ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET_CONFIG);
    }

    public static boolean isVariationConfigResource(@Nullable final Resource resource) {
        return ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_VARIATION_CONFIG);
    }

    public static boolean isRenditionConfigResource(@Nullable final Resource resource) {
        return ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_RENDITION_CONFIG);
    }

    public static boolean isConfigResource(@Nullable final Resource resource) {
        return isAssetConfigResource(resource) || isVariationConfigResource(resource) || isRenditionConfigResource(resource);
    }

    //
    // Configuration data
    //

    public class GetVariationsOperation implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            AssetConfig config = null;
            if (isAssetConfigResource(resource)) {
                config = new AssetConfig(AssetConfigUtil.assetConfigCascade(resource));
            } else if (isRenditionConfigResource(resource)) {
                config = new AssetConfig(AssetConfigUtil.assetConfigCascade(
                        Objects.requireNonNull(resource.getParent()).getParent()));
            } else if (isVariationConfigResource(resource)) {
                config = new AssetConfig(AssetConfigUtil.assetConfigCascade(resource.getParent()));
            }
            if (config != null) {
                Map<String, Object> configuration = status.data("configuration");
                configuration.put("path", config.getPath());
                Map<String, Object> variationSet = status.data("variations");
                List<Map<String, Object>> variationList = status.list("variations");
                for (VariationConfig variation : config.getVariationList()) {
                    RenditionConfig original = variation.getOriginal();
                    Map<String, Object> variationData = new LinkedHashMap<String, Object>() {{
                        put("name", variation.getName());
                        if (variation.isDefaultConfig()) {
                            put("default", true);
                        }
                        if (original != null) {
                            put("original", original.getName());
                        }
                    }};
                    variationList.add(variationData);
                    variationSet.put(variation.getName(), variation.getName());
                    if (variation.isDefaultConfig()) {
                        configuration.put("defaultVariation", variation.getName());
                    }
                }
            } else {
                status.error("no matching configuration resource: '{}'", resource.getPath());
            }
            status.sendJson();
        }
    }

    public class GetRenditionsOperation implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            VariationConfig config = null;
            if (isAssetConfigResource(resource)) {
                String name = request.getParameter(AssetsConstants.VARIATION);
                AssetConfig assetConfig = new AssetConfig(AssetConfigUtil.assetConfigCascade(resource));
                if (StringUtils.isNotBlank(name)) {
                    config = assetConfig.getVariation(name);
                }
                if (config == null) {
                    config = assetConfig.findVariation(ConfigHandle.DEFAULT);
                }
            } else if (isVariationConfigResource(resource)) {
                config = new VariationConfig(new AssetConfig(AssetConfigUtil.assetConfigCascade(resource.getParent())), resource);
            } else if (isRenditionConfigResource(resource)) {
                config = new VariationConfig(
                        new AssetConfig(AssetConfigUtil.assetConfigCascade(
                                Objects.requireNonNull(resource.getParent()).getParent())),
                        resource.getParent());
            }
            if (config != null) {
                Map<String, Object> configuration = status.data("configuration");
                configuration.put("path", config.getAssetConfig().getPath());
                Map<String, Object> variation = status.data("variation");
                variation.put("name", config.getName());
                Map<String, Object> renditionSet = status.data("renditions");
                List<Map<String, Object>> renditionList = status.list("renditions");
                for (RenditionConfig rendition : config.getRenditionList()) {
                    Map<String, Object> renditionData = new LinkedHashMap<String, Object>() {{
                        put("name", rendition.getName());
                        if (rendition.isDefaultConfig()) {
                            put("default", true);
                        }
                    }};
                    renditionList.add(renditionData);
                    renditionSet.put(rendition.getName(), rendition.getName());
                    if (rendition.isDefaultConfig()) {
                        variation.put("defaultRendition", rendition.getName());
                    }
                }
            } else {
                status.error("no matching configuration resource: '{}'", resource.getPath());
            }
            status.sendJson();
        }
    }

    //
    // Configuration chnanges
    //

    public class ConfigCreateOperation implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

            String name = request.getParameter(PARAM_NAME);

            if (!isConfigResource(resource) || StringUtils.isNotBlank(name)) {

                Resource configResource = assetsService.createConfigNode(context, resource, name, true);
                if (configResource == null) {
                    status.error("can't create configuration: '{}:{}'", resource.getPath(), name);
                }

            } else {
                status.error("configuration exists always: '{}'", resource.getPath());
            }

            status.sendJson();
        }
    }

    public class ConfigCopyOperation implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

            String path = request.getParameter(PARAM_PATH);
            Resource parent;

            if (StringUtils.isNotBlank(path) && (parent = context.getResolver().getResource(path)) != null) {

                if (isAssetConfigResource(resource)) {

                    Resource configResource = assetsService.copyConfigNode(context, parent, resource, true);
                    if (configResource == null) {
                        status.error("can't copy configuration: '{}'", resource.getPath());
                    }

                } else {
                    status.error("configuration template doesn't exist: '{}'" + resource.getPath());
                }
            } else {
                status.error("invalid configuration parent: '{}'" + path);
            }

            status.sendJson();
        }
    }

    public class ConfigDeleteOperation implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            if (isConfigResource(resource)) {

                assetsService.deleteConfigNode(context, resource, true);
                ResponseUtil.writeEmptyObject(response);

            } else {
                status.error("configuration node not found or not the right type: '{}'",
                        (resource != null ? resource.getPath() : "<null>"));
            }

            status.sendJson();
        }
    }
}
