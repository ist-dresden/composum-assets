/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.servlet;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.AssetVariation;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.handle.SimpleImageRendition;
import com.composum.assets.commons.service.AdaptiveImageService;
import com.composum.assets.commons.util.AdaptiveUtil;
import com.composum.assets.commons.util.AssetConfigUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.XSS;
import org.apache.commons.lang3.StringUtils;
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
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.composum.assets.commons.servlet.AdaptiveImageServlet.parseSelectors;

/**
 * the servlet to deliver asset originals or volatile configuration example images
 * {/path/to/image(asset)}.asset[.{variation}[.{rendition}]].ext[{/path/to/asset/config}]
 */
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

    @Reference
    protected AssetsConfiguration assetsConfig;

    @Reference
    protected AdaptiveImageService adaptiveImageService;

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
            String[] selectors = parseSelectors(context.getRequest());

            String configPath = XSS.filter(request.getRequestPathInfo().getSuffix());
            Resource assetConfigRes;
            // explicit config suffix for a config preview and variation and rendition selectors?...
            if (StringUtils.isNotBlank(configPath) && selectors.length > 1 &&
                    (assetConfigRes = request.getResourceResolver().getResource(configPath)) != null
                    && assetsConfig.getContentRootFilter().accept(assetConfigRes)) {

                // render configuration preview of the requested example image
                AssetConfig assetConfig = new AssetConfig(AssetConfigUtil.configCascade(ResourceHandle.use(assetConfigRes)));
                VariationConfig variationConfig = assetConfig.findVariation(selectors[0]);
                RenditionConfig renditionConfig;
                if (variationConfig != null
                        && (renditionConfig = variationConfig.findRendition(selectors[1])) != null) {
                    assetPreview(context, assetConfig, selectors, renditionConfig);
                    return;
                }

            } else {

                // render asset original of the requested variation
                ImageAsset imageAsset = new ImageAsset(context, resource);
                AssetVariation variation = selectors.length > 0
                        ? imageAsset.getVariation(selectors[0]) : null;
                AssetRendition original = variation != null
                        ? variation.getOriginal() : imageAsset.getOriginal();
                if (AdaptiveUtil.sendRendition(response, original)) {
                    return;
                }
            }
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * response with a volatile asset rendition built on the image or asset resource of the request using
     * the suffix as asset configuration path and the 'variation' and 'rendition' selectors analogous to
     * the adapative image servlet
     *
     * @param context         the context (resource, resolver, request, response)
     * @param assetConfig     the asset configuration built using the request suffix
     * @param selectors       the 'adaptive' selectors for the preview image
     * @param renditionConfig the requested rendition configuration
     */
    protected void assetPreview(@Nonnull final BeanContext context, @Nonnull final AssetConfig assetConfig,
                                @Nonnull final String[] selectors, @Nonnull final RenditionConfig renditionConfig)
            throws IOException {
        Resource resource = context.getResource();
        Resource original = null;
        ImageAsset imageAsset;
        AssetVariation variation = null;
        if (resource.isResourceType(ImageAsset.RESOURCE_TYPE)) {
            // use the asset behaviour if the resource is an image asset
            imageAsset = new ImageAsset(context, resource);
            variation = imageAsset.getVariation(selectors[0]);
            AssetRendition originalRendition = variation != null
                    ? variation.getOriginal() : imageAsset.getOriginal();
            original = getReditionResource(
                    variation != null ? variation.getOriginal() : null, imageAsset.getOriginal());
        }
        // use an image asset based on the given resource (maybe not an Asset) with the requested configuration
        imageAsset = new ImageAsset(context, resource, assetConfig);
        if (variation == null) {
            // resource is not an asset or the used asset itself doesn't know the requested variation
            variation = new AssetVariation(context, resource, imageAsset, renditionConfig.getVariation());
        }
        AssetRendition rendition = original == null
                // no appropriate asset original available - use the resource as it is
                ? new SimpleImageRendition(context, resource, variation, renditionConfig)
                // build a rendition of the found usable variation or asset original
                : new AssetRendition(context, original, variation, renditionConfig);
        SlingHttpServletResponse response = context.getResponse();
        try {
            // render the volatile rendition image direct to the response...
            response.setContentType(rendition.getMimeType());
            adaptiveImageService.volatileRendition(rendition, response.getOutputStream());
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected Resource getReditionResource(AssetRendition... options) {
        for (AssetRendition rendition : options) {
            if (rendition != null && rendition.getFile() != null) {
                return rendition.getResource();
            }
        }
        return null;
    }

    @Activate
    @Modified
    protected void activate(ComponentContext context) {
        this.bundleContext = context.getBundleContext();
    }
}
