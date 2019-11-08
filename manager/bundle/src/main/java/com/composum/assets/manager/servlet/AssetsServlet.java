package com.composum.assets.manager.servlet;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.service.AssetsService;
import com.composum.assets.commons.service.MetaPropertiesService;
import com.composum.assets.manager.config.AssetConfigBean;
import com.composum.assets.manager.config.ConfigBean;
import com.composum.assets.manager.config.ImageConfigBean;
import com.composum.assets.manager.config.RenditionConfigBean;
import com.composum.assets.manager.config.VariationConfigBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.NodeTreeServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.ResponseUtil;
import com.composum.sling.nodes.NodesConfiguration;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
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

import javax.jcr.RepositoryException;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * The servlet to provide changes of the Asset Managers UI.
 */
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Assets Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/assets/assets",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_PUT,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_DELETE
        })
public class AssetsServlet extends NodeTreeServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AssetsServlet.class);

    @Reference
    protected NodesConfiguration nodesConfig;

    @Reference
    protected AssetsConfiguration assetsConfig;

    @Reference
    protected AssetsService assetsService;

    @Reference
    protected MetaPropertiesService metaPropertiesService;

    protected BundleContext bundleContext;

    //
    // Servlet operations
    //

    public enum Extension {
        html, json
    }

    public enum Operation {
        createImage, uploadImage,
        toImageAsset, toSimpleImage,
        tree, config, createConfig, copyConfig, deleteConfig, configDefault,
        get, reload, refreshMeta
    }

    protected AssetsOperationSet operations = new AssetsOperationSet();

    @Override
    protected ServletOperationSet getOperations() {
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
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.get, new GetOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.reload, new ReloadOperation());

        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.tree, new TreeOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.config, new ConfigGetJsonOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.createConfig, new ConfigCreateJsonOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.refreshMeta, new RefreshMetaDataOperation());

        // POST
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.createImage, new CreateImageAssetOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.toImageAsset, new TransformToImageAssetOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.toSimpleImage, new TransformToSimpleImageOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.createConfig, new ConfigCreateJsonOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.copyConfig, new ConfigCopyJsonOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.deleteConfig, new ConfigDeleteJsonOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.configDefault, new ChangeDefaultConfigOperation());

        // PUT

        // DELETE
    }

    public class AssetsOperationSet extends ServletOperationSet<Extension, Operation> {

        public AssetsOperationSet() {
            super(Extension.json);
        }
    }

    //
    // operation implementations
    //

    public class GetOperation implements ServletOperation {

        protected String getSelectors(SlingHttpServletRequest request) {
            return request.getParameter("selectors");
        }

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException, ServletException {

            String type = request.getParameter("resourceType");
            RequestDispatcherOptions options = new RequestDispatcherOptions();
            options.setForceResourceType(type);

            String selectors = getSelectors(request);
            if (StringUtils.isNotBlank(selectors)) {
                options.setReplaceSelectors(selectors);
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher(resource, options);
            if (dispatcher != null) {
                dispatcher.forward(request, response);
            } else {
                LOG.error("can't retrive dispatcher");
            }
        }
    }

    public class ReloadOperation extends GetOperation {

        @Override
        protected String getSelectors(SlingHttpServletRequest request) {
            String selectors = super.getSelectors(request);
            return StringUtils.isNotBlank(selectors) ? selectors : "reload";
        }
    }

    public class RefreshMetaDataOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            ResourceResolver resolver = resource.getResourceResolver();
            metaPropertiesService.adjustMetaProperties(resolver, resource);
            resolver.commit();

            ResponseUtil.writeEmptyObject(response);
        }
    }

    // Asset Manipulation


    public class CreateImageAssetOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            RequestParameterMap parameters = request.getRequestParameterMap();

            RequestParameter file = parameters.getValue(PARAM_FILE);
            InputStream imageStream;
            if (file != null && (imageStream = file.getInputStream()) != null) {

                String path = request.getParameter(PARAM_PATH);
                String name = request.getParameter(PARAM_NAME);
                String variation = request.getParameter("variation");

                try (ResourceResolver resolver = resource.getResourceResolver()) {

                    BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                    assetsService.createImageAsset(context, path, name, variation, imageStream);

                    resolver.commit();
                    ResponseUtil.writeEmptyObject(response);

                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                }

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "no image file");
            }
        }
    }

    public class TransformToImageAssetOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws RepositoryException, IOException {

            try (ResourceResolver resolver = resource.getResourceResolver()) {

                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                assetsService.transformToImageAsset(context, resource);

                resolver.commit();
                ResponseUtil.writeEmptyObject(response);
            }
        }
    }

    public class TransformToSimpleImageOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws RepositoryException, IOException {

            try (ResourceResolver resolver = resource.getResourceResolver()) {

                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                assetsService.transformToSimpleImage(context, resource);

                resolver.commit();
                ResponseUtil.writeEmptyObject(response);
            }
        }
    }

    //
    // Configuration
    //

    public static ConfigBean getConfigBean(BeanContext context, Resource resource) {
        ConfigBean configBean;
        if (ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_IMAGE_CONFIG)) {
            configBean = new ImageConfigBean(context, resource);
        } else if (ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET_CONFIG)) {
            configBean = new AssetConfigBean(context, resource);
        } else if (ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_VARIATION_CONFIG)) {
            configBean = new VariationConfigBean(context, resource);
        } else if (ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_RENDITION_CONFIG)) {
            configBean = new RenditionConfigBean(context, resource);
        } else {
            return null;
        }
        return configBean;
    }

    public class ConfigGetJsonOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            jsonAnswer(context, resource, "not a configuration resource: ");
        }

        protected void jsonAnswer(BeanContext context, Resource configResource, String message) throws IOException {
            SlingHttpServletResponse response = context.getResponse();
            ConfigBean configBean;
            if (configResource != null && (configBean = getConfigBean(context, configResource)) != null) {

                response.setStatus(HttpServletResponse.SC_OK);
                JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);

                configBean.toJson(jsonWriter);

            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message +
                        (configResource != null ? configResource.getPath() : "<null>"));
            }
        }
    }

    public class ConfigCreateJsonOperation extends ConfigGetJsonOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

            String name = request.getParameter(PARAM_NAME);

            ConfigBean configBean = getConfigBean(context, resource);
            if (configBean == null || StringUtils.isNotBlank(name)) {

                Resource configResource = assetsService.createConfigNode(context, resource, name, true);
                jsonAnswer(context, configResource, "can't create configuration: ");

            } else {
                response.sendError(HttpServletResponse.SC_CONFLICT,
                        "configuration exists always: " + resource.getPath());
            }
        }
    }

    public class ConfigCopyJsonOperation extends ConfigGetJsonOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

            String path = request.getParameter(PARAM_PATH);
            Resource parent;

            if (StringUtils.isNotBlank(path) && (parent = context.getResolver().getResource(path)) != null) {

                ConfigBean configBean = getConfigBean(context, resource);
                if (configBean != null) {

                    Resource configResource = assetsService.copyConfigNode(context, parent, resource, true);
                    jsonAnswer(context, configResource, "can't copy configuration: ");

                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND,
                            "configuration template doesn't exist: " + resource.getPath());
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "invalid configuration parent: " + path);
            }
        }
    }

    public class ConfigDeleteJsonOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            if (resource != null && getConfigBean(context, resource) != null) {

                assetsService.deleteConfigNode(context, resource, true);
                ResponseUtil.writeEmptyObject(response);

            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "configuration node not found or not the right type:" +
                                (resource != null ? resource.getPath() : "<null>"));
            }
        }
    }

    public class DefaultConfigSettingsOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            sendConfigSettings(context, resource);
        }

        protected void sendConfigSettings(BeanContext context, Resource configResource)
                throws IOException {

            JsonWriter jsonWriter = ResponseUtil.getJsonWriter(context.getResponse());
            jsonWriter.beginArray();

            Resource folder = configResource.getParent();
            List<Resource> configList = ResourceUtil.getChildrenByType(folder, AssetsConstants.ASSET_CONFIG_TYPE_SET);
            for (Resource sibling : configList) {
                jsonWriter.beginObject();
                ValueMap values = sibling.getValueMap();

                jsonWriter.name("name").value(sibling.getName());
                jsonWriter.name("path").value(sibling.getPath());

                List<String> categories = Arrays.asList(values.get(ConfigHandle.CATEGORIES, new String[0]));
                jsonWriter.name("isDefault").value(categories.contains(ConfigHandle.DEFAULT));

                jsonWriter.name(ConfigHandle.CATEGORIES).beginArray();
                for (String category : categories) {
                    if (!ConfigHandle.DEFAULT.equals(category)) {
                        jsonWriter.value(category);
                    }
                }

                jsonWriter.endArray();
                jsonWriter.endObject();
            }

            jsonWriter.endArray();
        }
    }

    public class ChangeDefaultConfigOperation extends DefaultConfigSettingsOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            assetsService.setDefaultConfiguration(context, resource, true);
            sendConfigSettings(context, resource);
        }
    }

    //
    // Assets Tree
    //

    @Override
    protected ResourceFilter getNodeFilter(SlingHttpServletRequest request) {
        return new ResourceFilter.FilterSet(
                ResourceFilter.FilterSet.Rule.tree, // a tree filter including intermediate folders
                new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                        assetsConfig.getAssetNodeFilter(),
                        assetsConfig.getDefaultNodeFilter()),
                new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                        assetsConfig.getAssetPathFilter(),
                        assetsConfig.getTreeIntermediateFilter()));
    }

    /**
     * sort children of orderable nodes
     */
    @Override
    @SuppressWarnings("Duplicates")
    protected List<Resource> prepareTreeItems(ResourceHandle resource, List<Resource> items) {
        if (!nodesConfig.getOrderableNodesFilter().accept(resource)) {
            items.sort(Comparator.comparing(this::getSortName));
        }
        return items;
    }

    /**
     * make folders first
     */
    @Override
    public String getSortName(Resource resource) {
        String name = resource.getName().toLowerCase();
        if (assetsConfig.getTreeIntermediateFilter().accept(resource)) {
            return "c" + name;
        }
        return super.getSortName(resource);
    }

    protected class AssetsTreeNodeStrategy extends DefaultTreeNodeStrategy {

        public AssetsTreeNodeStrategy(ResourceFilter filter) {
            super(filter);
        }

        @Override
        public String getContentTypeKey(ResourceHandle resource, String prefix) {
            Resource config = resource.getChild(AssetConfig.CHILD_NAME);
            if (ResourceUtil.isResourceType(config,
                    AssetsConstants.NODE_TYPE_ASSET_CONFIG)) {
                return AssetConfig.CHILD_NAME;
            } else {
                return super.getContentTypeKey(resource, prefix);
            }
        }
    }

    protected class TreeOperation extends NodeTreeServlet.TreeOperation {

        @Override
        protected TreeNodeStrategy getNodeStrategy(SlingHttpServletRequest request) {
            return new AssetsTreeNodeStrategy(getNodeFilter(request));
        }
    }

    @Activate
    @Modified
    protected void activate(ComponentContext context) {
        this.bundleContext = context.getBundleContext();
    }
}
