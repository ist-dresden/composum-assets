/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.servlet;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.service.AdaptiveImageService;
import com.composum.assets.commons.util.AdaptiveUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.concurrent.SequencerService;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.HttpUtil;
import com.composum.sling.core.util.LinkUtil;
import com.composum.sling.core.util.MimeTypeUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

/**
 * The Composum Assets - Adaptive Image Servlet delivers renditions of image assets for the configured variations.
 * If the referenced resource is a simple nt:file image we also serve that unmodified.
 */
@Component(
        service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Assets - Adaptive Image Servlet: delivers renditions of " +
                        "image assets for the configured variations",
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=" + ServletResolverConstants.DEFAULT_RESOURCE_TYPE,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_SELECTORS + "=adaptive",
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=jpg",
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=jpeg",
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=png",
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=gif",
        }
)
@Designate(ocd = AdaptiveImageServlet.Configuration.class)
public class AdaptiveImageServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AdaptiveImageServlet.class);

    public static final StringFilter.BlackList SELECTORS_FILTER = new StringFilter.BlackList("adaptive");

    protected BundleContext bundleContext;
    protected Configuration config;

    @Reference
    protected AdaptiveImageService adaptiveImageService;

    @Reference
    protected SequencerService<SequencerService.Token> sequencer;

    @Override
    protected void doGet(@Nonnull SlingHttpServletRequest request,
                         @Nonnull SlingHttpServletResponse response) throws ServletException,
            IOException {

        BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
        Resource resource = AdaptiveUtil.retrieveResource(request);

        if (ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET)) {
            ImageAsset asset;

            String assetConfigParam = request.getParameter("config"); // explicit config for the manager UI
            Resource assetConfigRes;
            if (StringUtils.isNotBlank(assetConfigParam) &&
                    (assetConfigRes = request.getResourceResolver().getResource(assetConfigParam)) != null) {
                asset = new ImageAsset(context, resource, new AssetConfig(assetConfigRes));
            } else {
                asset = new ImageAsset(context, resource);
            }

            if (asset.isValid()) {

                handleAsset(request, response, asset);
                return;
            }

        } else if (config.deliverSimpleImages() &&
                ResourceUtil.isResourceType(resource, JcrConstants.NT_FILE) &&
                MimeTypeUtil.isMimeType(resource, AssetsConstants.IMAGE_MIME_TYPE_PATTERN)) {

            handleSimpleImage(request, response, resource);
            return;
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Handle a real asset.
     */
    protected void handleAsset(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response,
                               @Nonnull ImageAsset asset) throws IOException, ServletException {
        AssetRendition rendition = null;
        String[] selectors = parseSelectors(request);
        String variationName = selectors[0];
        String renditionName = selectors[1];

        try {
            // determine the rendition if configured selectors are requested
            rendition = adaptiveImageService.getOrCreateRendition(asset, variationName, renditionName);

            if (rendition == null) {
                // requested pattern not configured...

                RenditionConfig renditionConfig = adaptiveImageService
                        .findRenditionConfig(asset, variationName, renditionName);

                if (renditionConfig != null) {
                    VariationConfig variationConfig = renditionConfig.getVariation();

                    if (config.redirectUnwanted()) {
                        String matchingUrl = asset.getImageUri(variationConfig.getName(), renditionConfig.getName());
                        matchingUrl = LinkUtil.getUrl(request, matchingUrl);
                        response.setHeader(HttpUtil.HEADER_LOCATION, matchingUrl);
                        if (LOG.isInfoEnabled()) {
                            LOG.info("no rendition found for '{}' redirecting to '{}'...", request.getRequestURI(), matchingUrl);
                        }

                        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                        return;
                    }

                    if (LOG.isInfoEnabled()) {
                        LOG.info("no rendition found for '{}' delivering '{}/{}'...", request.getRequestURI(), variationConfig.getName(), renditionConfig.getName());
                    }

                    rendition = adaptiveImageService.getOrCreateRendition(
                            asset, renditionConfig.getVariation().getName(), renditionConfig.getName());
                }
            }
        } catch (RepositoryException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if (rendition != null) {
            sendRenditionContent(request, response, rendition);
        } else {
            String message = ("No rendition rule found for asset '" + asset.getPath()
                    + "' and selectors '" + request.getRequestPathInfo().getSelectorString() + "'.");
            LOG.error(message);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
        }
    }

    /**
     * Returns array of variation name and rendition name, as determined from selectors.
     */
    @Nonnull
    protected String[] parseSelectors(@Nonnull SlingHttpServletRequest request) {
        RequestPathInfo pathInfo = request.getRequestPathInfo();
        String[] selectors = SELECTORS_FILTER.getFiltered(pathInfo.getSelectors());
        switch (selectors.length) {
            case 0:
                selectors = new String[]{ConfigHandle.DEFAULT, ConfigHandle.DEFAULT};
                break;
            case 1:
                selectors = new String[]{selectors[0], ConfigHandle.DEFAULT};
                break;
        }
        return selectors;
    }

    protected void sendRenditionContent(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response,
                                        @Nonnull AssetRendition rendition)
            throws IOException, ServletException {
        Calendar lastModified = rendition.getLastModified();

        if (HttpUtil.notModifiedSince(request.getDateHeader(HttpUtil.HEADER_IF_MODIFIED_SINCE), lastModified)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            LOG.debug("Sending {} 'not modified'", HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        boolean cacheHashCorrect = StringUtils.contains(request.getRequestPathInfo().getSuffix(),
                '/' + rendition.getCacheHash() + '/');

        if (config.redirectToCachefriendlyUrl() && !cacheHashCorrect) {
            String cachefriendlyUrl = LinkUtil.getUrl(request, rendition.getImageUrl());
            LOG.debug("Cache hash not found - rediecting to {}", cachefriendlyUrl);
            response.sendRedirect(cachefriendlyUrl);
            return;
        }

        if (cacheHashCorrect) { // unchangeable content - sending the recommended 1 year expiration
            response.setHeader(HttpUtil.HEADER_CACHE_CONTROL, "max-age=31536000");
        }

        try {
            try (InputStream imageStream = rendition.getStream()) {
                if (imageStream != null) {
                    try {
                        AdaptiveUtil.sendImageStream(response, rendition, imageStream);
                    } finally {
                        imageStream.close();
                    }
                } else {
                    String message = ("Can't load data for rendition '" + rendition.getPath() + "'!");
                    LOG.error(message);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
                }
            }
        } catch (RepositoryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServletException(ex);
        }
    }

    /**
     * If the referenced resource is just a simple nt:file we want to serve the file directly as an image.
     */
    protected void handleSimpleImage(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response,
                                     Resource resource) throws IOException {
        Resource content = ResourceUtil.getDataResource(resource);
        if (content != null) {
            ValueMap contentValues = content.getValueMap();

            Calendar lastModified = contentValues.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);

            if (HttpUtil.notModifiedSince(request.getDateHeader(HttpUtil.HEADER_IF_MODIFIED_SINCE), lastModified)) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            Binary binary = ResourceUtil.getBinaryData(resource);

            try (InputStream imageStream = binary != null ? binary.getStream() : null) {
                if (imageStream != null) {

                    response.setContentType(MimeTypeUtil.getMimeType(resource, ""));
                    if (lastModified != null) {
                        response.setDateHeader(HttpUtil.HEADER_LAST_MODIFIED, lastModified.getTimeInMillis());
                    }
                    response.setStatus(HttpServletResponse.SC_OK);

                    OutputStream outputStream = response.getOutputStream();
                    IOUtils.copy(imageStream, outputStream);

                    return;
                }
            } catch (RepositoryException ignore) {
            }
        }
        String message = ("Can't load data for image '" + resource.getPath() + "'!");
        LOG.error(message);
        response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
    }

    @Activate
    @Modified
    protected void activate(ComponentContext context, Configuration config) {
        this.bundleContext = context.getBundleContext();
        this.config = config;
    }

    @ObjectClassDefinition(
            name = "Composum Assets - Adaptive Image Servlet",
            description = "delivers renditions of image assets for the configured variations"
    )
    public @interface Configuration {

        @AttributeDefinition(
                name = "Simple Images",
                description = "delivers the content of simple images if set to 'true'"
        )
        boolean deliverSimpleImages() default true;

        @AttributeDefinition(
                name = "Redirect",
                description = "redirects unwanted requests to the most similar supported URL if set to 'true'"
        )
        boolean redirectUnwanted() default true;

        @AttributeDefinition(
                name = "Redirect to Cachefriendly",
                description = "Redirects asset requests that do not contain a hash in the suffix that ensures that " +
                        "the this URL always delivers the same picture to one that does."
        )
        boolean redirectToCachefriendlyUrl() default true;

    }

}
