package com.composum.assets.manager.servlet;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.service.AssetsService;
import com.composum.assets.commons.service.MetaPropertiesService;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.NodeTreeServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.ResponseUtil;
import com.composum.sling.nodes.NodesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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
import javax.jcr.RepositoryException;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
        tree, get, reload, refreshMeta
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
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.get, new GetOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.reload, new ReloadOperation());

        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.tree, new TreeOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.refreshMeta, new RefreshMetaDataOperation());

        // POST
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.createImage, new CreateImageAssetOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.toImageAsset, new TransformToImageAssetOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.toSimpleImage, new TransformToSimpleImageOperation());

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
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
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
                LOG.error("can't retrieve dispatcher");
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
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
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
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
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
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
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
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
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
    // Assets Tree
    //

    @Override
    protected ResourceFilter getNodeFilter(SlingHttpServletRequest request) {
        return assetsConfig.getRequestNodeFilter(request, PARAM_FILTER, AssetsConfiguration.ASSET_FILTER_ALL);
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
