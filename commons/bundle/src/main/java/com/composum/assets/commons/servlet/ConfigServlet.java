package com.composum.assets.commons.servlet;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.service.AssetsService;
import com.composum.assets.commons.util.AssetConfigUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.servlet.AbstractServiceServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.servlet.Status;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
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
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.composum.assets.commons.AssetsConstants.PATH_ASSET_CONFIG;
import static com.composum.assets.commons.AssetsConstants.PATH_IMAGE_CONFIG;
import static com.composum.assets.commons.AssetsConstants.RENDITION;
import static com.composum.assets.commons.AssetsConstants.VARIATION;
import static com.composum.assets.commons.util.AssetConfigUtil.isAssetConfigResource;
import static com.composum.assets.commons.util.AssetConfigUtil.isConfigResource;
import static com.composum.assets.commons.util.AssetConfigUtil.isRenditionConfigResource;
import static com.composum.assets.commons.util.AssetConfigUtil.isVariationConfigResource;

/**
 * The servlet to provide changes of the Asset configuratioon resources.
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
    protected AssetsService assetsService;

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
        json
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

    /**
     * answers with the set of available variations of the configuration requestd by the suffix
     */
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
                config = new AssetConfig(AssetConfigUtil.assetConfigCascade(resource.getParent(2)));
            } else if (isVariationConfigResource(resource)) {
                config = new AssetConfig(AssetConfigUtil.assetConfigCascade(resource.getParent()));
            }
            if (config != null) {
                String param = request.getParameter("cumulated");
                boolean cumulated = param != null && (StringUtils.isBlank(param) || Boolean.parseBoolean(param));
                Map<String, Object> configuration = status.data("configuration");
                Map<String, Object> variationSet = status.data("variations");
                List<Map<String, Object>> variationList = status.list("variations");
                configuration.put("path", config.getPath());
                List<VariationConfig> list = config.getVariationList(cumulated);
                Collections.sort(list);
                for (VariationConfig variation : list) {
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

    /**
     * answers with the set of available renditions of the configuration requestd by the suffix and
     * the 'variation' parameter
     */
    public class GetRenditionsOperation implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            AssetConfig assetConfig = null;
            VariationConfig config = null;
            if (isAssetConfigResource(resource)) {
                String name = request.getParameter(VARIATION);
                assetConfig = new AssetConfig(AssetConfigUtil.assetConfigCascade(resource));
                if (StringUtils.isNotBlank(name)) {
                    config = assetConfig.getVariation(name);
                }
                if (config == null) {
                    config = assetConfig.findVariation(ConfigHandle.DEFAULT);
                }
            } else if (isVariationConfigResource(resource)) {
                assetConfig = new AssetConfig(AssetConfigUtil.assetConfigCascade(resource.getParent()));
                config = new VariationConfig(assetConfig, resource);
            } else if (isRenditionConfigResource(resource)) {
                assetConfig = new AssetConfig(AssetConfigUtil.assetConfigCascade(resource.getParent(2)));
                config = new VariationConfig(assetConfig, resource.getParent());
            }
            if (assetConfig != null) {
                String param = request.getParameter("cumulated");
                boolean cumulated = param != null && (StringUtils.isBlank(param) || Boolean.parseBoolean(param));
                Map<String, Object> configuration = status.data("configuration");
                configuration.put("path", assetConfig.getPath());
                Map<String, Object> renditionSet = status.data("renditions");
                List<Map<String, Object>> renditionList = status.list("renditions");
                if (config != null) {
                    Map<String, Object> variation = status.data("variation");
                    variation.put("name", config.getName());
                    List<RenditionConfig> list = config.getRenditionList(cumulated);
                    Collections.sort(list);
                    for (RenditionConfig rendition : list) {
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

    /**
     * creates a new configuration resource as child of the resource referenced in the suffix
     * the new nodes name is specified by the 'name' parameter
     * the parent specified in the suffix can be a configuration node or a variation node within;
     * a variation of a configuration as parent can also be specified by its name
     * in the 'variation' parameter
     * a new configuration node is created as child of the 'jcr:content' node of the specified parent
     */
    public class ConfigCreateOperation implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            String name = request.getParameter(PARAM_NAME);

            if (!isAssetConfigResource(resource) || StringUtils.isNotBlank(name)) {
                String param;

                if (StringUtils.isNotBlank(param = request.getParameter(VARIATION))) {
                    if (AssetConfigUtil.isVariationConfigResource(resource) && !resource.getName().equals(param)) {
                        resource = ResourceHandle.use(Objects.requireNonNull(resource.getParent()).getChild(param));
                    } else if (AssetConfigUtil.isAssetConfigResource(resource)) {
                        resource = ResourceHandle.use(resource.getChild(param));
                    }
                }

                if (resource.isValid()) {
                    try {
                        Resource configResource = assetsService.createConfigNode(context, resource, name, false);

                        ModifiableValueMap values;
                        if (configResource != null && (values = configResource.adaptTo(ModifiableValueMap.class)) != null) {

                            if (StringUtils.isNotBlank(param = request.getParameter(ResourceUtil.JCR_TITLE))) {
                                values.put(ResourceUtil.JCR_TITLE, param);
                            }
                            if (StringUtils.isNotBlank(param = request.getParameter(ResourceUtil.JCR_DESCRIPTION))) {
                                values.put(ResourceUtil.JCR_DESCRIPTION, param);
                            }
                            if ((param = request.getParameter(ConfigHandle.EXTENSION)) != null &&
                                    Boolean.parseBoolean(param) || "on".equalsIgnoreCase(param)) {
                                values.put(ConfigHandle.EXTENSION, true);
                            }

                            context.getResolver().commit();

                        } else {
                            status.error("can't determine parent: '{}:{}'", resource.getPath(), name);
                        }

                    } catch (PersistenceException ex) {
                        LOG.error(ex.getMessage(), ex);
                        status.error("server error: '{}'" + ex.getMessage());

                    }
                } else {
                    status.error("configuration exists already: '{}'", resource.getPath());
                }
            }

            status.sendJson();
        }
    }

    /**
     * copies the configuration resource specified in the suffix the the new holder specified in the 'path'
     * parameter; the suffix can be a configuration resource or a holder of such a resource; the 'path' must
     * be a potentially holder for a configuration
     */
    public class ConfigCopyOperation implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

            String path = request.getParameter(PARAM_PATH);

            if (StringUtils.isNotBlank(path) && (context.getResolver().getResource(path)) != null) {

                if (!isAssetConfigResource(resource)) {
                    Resource config;
                    if (isAssetConfigResource(config = resource.getChild(PATH_ASSET_CONFIG))) {
                        resource = ResourceHandle.use(config);
                    } else if (isAssetConfigResource(config = resource.getChild(PATH_IMAGE_CONFIG))) {
                        resource = ResourceHandle.use(config);
                    }
                }

                if (isAssetConfigResource(resource)) {

                    try {
                        Resource configResource = assetsService.copyConfigNode(context, path, resource, true);
                        if (configResource == null) {
                            status.error("can't copy configuration: '{}'", resource.getPath());
                        }

                    } catch (PersistenceException ex) {
                        LOG.error(ex.getMessage(), ex);
                        status.error("server error: '{}'" + ex.getMessage());
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

    /**
     * deletes a configuration resource or an element (variation or rendition) of a configuration
     * the resource specified in the suffix is deleted; if this resource is a configuration node and
     * a 'variation' / 'rendition' parameter is present the specified variation or rendition is deleted
     */
    public class ConfigDeleteOperation implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);

            if (isAssetConfigResource(resource)) {
                String param;

                if (StringUtils.isNotBlank(param = request.getParameter(VARIATION))) {
                    resource = ResourceHandle.use(resource.getChild(param));
                    if (StringUtils.isNotBlank(param = request.getParameter(RENDITION))) {
                        resource = ResourceHandle.use(resource.getChild(param));
                    }
                }
            }

            if (isConfigResource(resource)) {
                try {
                    BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

                    assetsService.deleteConfigNode(context, resource, true);

                } catch (PersistenceException ex) {
                    LOG.error(ex.getMessage(), ex);
                    status.error("server error: '{}'" + ex.getMessage());
                }

            } else {
                status.error("configuration node not found or not of the right type: '{}'",
                        (resource != null ? resource.getPath() : "<null>"));
            }

            status.sendJson();
        }
    }
}
