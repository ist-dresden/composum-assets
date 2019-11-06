/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.servlet;

import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.util.AdaptiveUtil;
import com.composum.sling.clientlibs.handle.FileHandle;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Component(
        service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Assets Image Servlet",
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=" + ServletResolverConstants.DEFAULT_RESOURCE_TYPE,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_SELECTORS + "=asset",
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=jpg",
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=jpeg",
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=png",
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=gif",
        }
)
public class ImageAssetServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ImageAssetServlet.class);

    protected BundleContext bundleContext;

    @Override
    protected void doGet(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response)
            throws IOException {

        Resource resource = AdaptiveUtil.retrieveResource(request);
        if (!ResourceUtil.isNonExistingResource(resource)) {
            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            ImageAsset imageAsset = new ImageAsset(context, resource);
            AssetRendition original = imageAsset.getOriginal();
            if (original != null) {
                FileHandle file = original.getFile();
                if (file.isValid()) {
                    response.setContentType(original.getMimeType());
                    try (InputStream imageStream = file.getStream()) {
                        AdaptiveUtil.sendImageStream(response, original, imageStream);
                    }
                    return;
                }
            }
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Activate
    @Modified
    protected void activate(ComponentContext context) {
        this.bundleContext = context.getBundleContext();
    }
}
