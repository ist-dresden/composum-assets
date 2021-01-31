/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.servlet;

import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.util.AdaptiveUtil;
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

/**
 * the servlet to 'render' asset resources (delivers the assets default original)
 */
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Assets Asset Node Servlet",
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=cpa:Asset",
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=composum/assets/image",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        })
public class AssetNodeServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AssetNodeServlet.class);

    protected BundleContext bundleContext;

    @Override
    protected void doGet(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response)
            throws IOException {

        Resource resource = AdaptiveUtil.retrieveResource(request);
        if (!ResourceUtil.isNonExistingResource(resource)) {
            BeanContext context = new BeanContext.Wrapper(
                    new BeanContext.Servlet(getServletContext(), bundleContext, request, response),
                    resource);
            if (AdaptiveUtil.sendRendition(response, new ImageAsset(context, resource).getOriginal())) {
                return;
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
