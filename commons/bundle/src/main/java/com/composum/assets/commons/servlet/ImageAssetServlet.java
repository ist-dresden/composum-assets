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
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@SlingServlet(
        name = "Composum Assets Image Servlet",
        resourceTypes = "sling/servlet/default",
        methods = {"GET"},
        selectors = {"asset"},
        extensions = {"jpg", "jpeg", "png", "gif"}
)
public class ImageAssetServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ImageAssetServlet.class);

    protected BundleContext bundleContext;

    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response)
            throws ServletException, IOException {

        Resource resource = AdaptiveUtil.retrieveResource(request);
        if (!ResourceUtil.isNonExistingResource(resource)) {
            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            ImageAsset imageAsset = new ImageAsset(context, resource);
            AssetRendition original = imageAsset.getOriginal();
            if (original != null) {
                FileHandle file = original.getFile();
                if (file.isValid()) {
                    try {
                        response.setContentType(original.getMimeType());
                        InputStream imageStream = file.getStream();
                        try {
                            AdaptiveUtil.sendImageStream(response, original, imageStream);
                        } finally {
                            imageStream.close();
                        }
                        return;
                    } catch (RepositoryException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
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
