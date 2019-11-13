package com.composum.assets.commons.servlet;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.service.AssetsService;
import com.composum.assets.commons.service.MetaPropertiesService;
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
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;

/**
 * The servlet to provide changes of the Asset Managers UI.
 */
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Assets Commons Operations Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/assets/commons",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST
        })
public class CommonsServlet extends AbstractServiceServlet {

    private static final Logger LOG = LoggerFactory.getLogger(CommonsServlet.class);

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
        createImage, uploadImage, deleteImage, toImageAsset, toSimpleImage,
        createConfig, copyConfig, deleteConfig, configDefault, refreshMeta
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
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.createConfig, new ConfigCreateOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.refreshMeta, new RefreshMetaDataOperation());

        // POST
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.createConfig, new ConfigCreateOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.refreshMeta, new RefreshMetaDataOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.createImage, new CreateImageAssetOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.uploadImage, new UploadImageOriginalOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.deleteImage, new DeleteImageAssetOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.toImageAsset, new TransformToImageAssetOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.toSimpleImage, new TransformToSimpleImageOperation());

        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.createConfig, new ConfigCreateOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.copyConfig, new ConfigCopyOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.deleteConfig, new ConfigDeleteOperation());
    }

    public class AssetsOperationSet extends ServletOperationSet<Extension, Operation> {

        public AssetsOperationSet() {
            super(Extension.json);
        }
    }

    //
    // operation implementations
    //

    public class RefreshMetaDataOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            ResourceResolver resolver = resource.getResourceResolver();
            metaPropertiesService.adjustMetaProperties(resolver, resource);
            resolver.commit();
            status.sendJson();
        }
    }

    // Asset Manipulation

    protected abstract class AbstractAssetUploadOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);

            RequestParameterMap parameters = request.getRequestParameterMap();

            RequestParameter file = parameters.getValue(PARAM_FILE);
            InputStream imageStream;
            if (file != null && (imageStream = file.getInputStream()) != null) {

                String path = request.getParameter(PARAM_PATH);
                String name = request.getParameter(PARAM_NAME);
                if (StringUtils.isBlank(name)) {
                    name = getDefaultName(file);
                }
                String variation = request.getParameter("variation");

                try (ResourceResolver resolver = resource.getResourceResolver()) {

                    BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                    doUpload(context, path, name, variation, imageStream);

                    resolver.commit();
                    ResponseUtil.writeEmptyObject(response);

                } catch (Exception ex) {
                    status.withLogging(LOG).error("server error: '{}'", ex);
                }

            } else {
                status.withLogging(LOG).error("no image file");
            }

            status.sendJson();
        }

        protected String getDefaultName(RequestParameter file) {
            return null;
        }

        protected abstract Resource doUpload(@Nonnull BeanContext context, @Nonnull String parentPath, @Nonnull String name,
                                             @Nonnull String variation, @Nonnull InputStream imageData)
                throws Exception;
    }

    public class CreateImageAssetOperation extends AbstractAssetUploadOperation {

        @Override
        protected String getDefaultName(RequestParameter file) {
            return file.getFileName();
        }

        @Override
        protected Resource doUpload(@Nonnull BeanContext context, @Nonnull String path, @Nonnull String name,
                                    @Nonnull String variation, @Nonnull InputStream imageData)
                throws Exception {
            return assetsService.createImageAsset(context, path, name, variation, imageData);
        }
    }

    public class UploadImageOriginalOperation extends AbstractAssetUploadOperation {

        @Override
        protected Resource doUpload(@Nonnull BeanContext context, @Nonnull String path, @Nonnull String name,
                                    @Nonnull String variation, @Nonnull InputStream imageData)
                throws Exception {
            return assetsService.uploadImageAsset(context, path, name, variation, imageData);
        }
    }

    public class DeleteImageAssetOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws RepositoryException, IOException {
            Status status = new Status(request, response);

            try (ResourceResolver resolver = resource.getResourceResolver()) {

                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                assetsService.deleteAsset(resource);
                resolver.commit();
            }

            status.sendJson();
        }
    }

    public class TransformToImageAssetOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws RepositoryException, IOException {
            Status status = new Status(request, response);

            try (ResourceResolver resolver = resource.getResourceResolver()) {

                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                assetsService.transformToImageAsset(context, resource);
                resolver.commit();
            }

            status.sendJson();
        }
    }

    public class TransformToSimpleImageOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws RepositoryException, IOException {
            Status status = new Status(request, response);

            try (ResourceResolver resolver = resource.getResourceResolver()) {

                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                assetsService.transformToSimpleImage(context, resource);
                resolver.commit();
            }

            status.sendJson();
        }
    }

    //
    // Configuration
    //

    public static boolean isConfigResource(@Nullable final Resource resource) {
        return ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_IMAGE_CONFIG) ||
                ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET_CONFIG) ||
                ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_VARIATION_CONFIG) ||
                ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_RENDITION_CONFIG);
    }

    public class ConfigCreateOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

            String name = request.getParameter(PARAM_NAME);

            if (!isConfigResource(resource) || StringUtils.isNotBlank(name)) {

                Resource configResource = assetsService.createConfigNode(context, resource, name, true);
                if (configResource == null) {
                    status.withLogging(LOG).error("can't create configuration: '{}:{}'", resource.getPath(), name);
                }

            } else {
                status.withLogging(LOG).error("configuration exists always: '{}'", resource.getPath());
            }

            status.sendJson();
        }
    }

    public class ConfigCopyOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

            String path = request.getParameter(PARAM_PATH);
            Resource parent;

            if (StringUtils.isNotBlank(path) && (parent = context.getResolver().getResource(path)) != null) {

                if (isConfigResource(resource)) {

                    Resource configResource = assetsService.copyConfigNode(context, parent, resource, true);
                    if (configResource == null) {
                        status.withLogging(LOG).error("can't copy configuration: '{}'", resource.getPath());
                    }

                } else {
                    status.withLogging(LOG).error("configuration template doesn't exist: '{}'" + resource.getPath());
                }
            } else {
                status.withLogging(LOG).error("invalid configuration parent: '{}'" + path);
            }

            status.sendJson();
        }
    }

    public class ConfigDeleteOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            if (isConfigResource(resource)) {

                assetsService.deleteConfigNode(context, resource, true);
                ResponseUtil.writeEmptyObject(response);

            } else {
                status.withLogging(LOG).error("configuration node not found or not the right type: '{}'",
                        (resource != null ? resource.getPath() : "<null>"));
            }

            status.sendJson();
        }
    }

    @Activate
    @Modified
    protected void activate(ComponentContext context) {
        this.bundleContext = context.getBundleContext();
    }
}