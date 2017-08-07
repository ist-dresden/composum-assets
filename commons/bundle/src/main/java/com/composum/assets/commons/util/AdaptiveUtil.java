package com.composum.assets.commons.util;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.sling.core.util.HttpUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

public class AdaptiveUtil {

    private static final Logger LOG = LoggerFactory.getLogger(AdaptiveUtil.class);

    public static String getImageUri(ImageAsset asset, String variationKey, String renditionKey) {
        if (asset != null) {
            String mimeType = asset.getMimeType();
            if (mimeType != null) {
                AssetConfig config = asset.getConfig();
                VariationConfig variation = config.findVariation(variationKey);
                if (variation != null) {
                    RenditionConfig rendition = variation.findRendition(renditionKey);
                    return getImageUri(asset,
                            rendition.getVariation().getName(),
                            rendition.getName(),
                            mimeType);
                } else {
                    // use the original if no configuration available
                    AssetRendition rendition = asset.getOriginal();
                    return getImageUri(asset,
                            rendition.getVariation().getName(),
                            rendition.getName(),
                            mimeType);
                }
            }
        }
        return "";
    }

    public static String getImageUri(AssetRendition rendition) {
        return getImageUri((ImageAsset) rendition.getAsset(),
                rendition.getVariation().getName(),
                rendition.getName(),
                rendition.getMimeType());
    }

    public static String getImageUri(ImageAsset asset, String variation, String rendition, String mimeType) {
        StringBuilder builder = new StringBuilder();
        String path = asset.getPath();
        String ext = mimeType.substring("image/".length());
        if (ext.equals("jpeg")) {
            ext = "jpg";
        }
        if (path.endsWith("." + ext)) {
            path = path.substring(0, path.length() - (ext.length() + 1));
        }
        String name = path.substring(path.lastIndexOf('/') + 1);
        builder.append(path);
        builder.append(".adaptive");
        builder.append('.').append(variation);
        builder.append('.').append(rendition);
        builder.append('.').append(ext);
        builder.append('/').append(getCacheHash(asset));
        builder.append('/').append(name);
        builder.append('.').append(ext);
        return builder.toString();
    }

    public static String getCacheHash(ImageAsset asset) {
        StringBuilder builder = new StringBuilder("T");
        Calendar lastModified = asset.getLastModified();
        if (lastModified != null) {
            builder.append(lastModified.getTimeInMillis());
        } else {
            builder.append(new Date().getTime());
        }
        return builder.toString();
    }

    public static void sendImageStream(HttpServletResponse response,
                                       AssetRendition rendition, InputStream imageStream)
            throws IOException {

        Object value;
        if ((value = rendition.getMimeType()) != null) {
            response.setContentType(value.toString());
        }
        if ((value = rendition.getLastModified()) != null) {
            response.setDateHeader(HttpUtil.HEADER_LAST_MODIFIED,
                    ((Calendar) value).getTimeInMillis());
        }
        if ((value = rendition.getSize()) != null) {
            response.setContentLength(((Long) value).intValue());
        }
        response.setStatus(HttpServletResponse.SC_OK);

        OutputStream outputStream = response.getOutputStream();
        IOUtils.copy(imageStream, outputStream);
    }

    /**
     * retrieves the target resource by rebuilding path without selectors and suffixes;
     * this can be necessary:
     * - if the name of the resource itself ends with the extension
     * - if a suffix was added to the URL for cache control
     *
     * @return the resource or 'null'
     */
    public static Resource retrieveResource(SlingHttpServletRequest request) {

        Resource resource = request.getResource();
        if (LOG.isDebugEnabled()) {
            LOG.debug("request.resource: " + resource);
        }

        if (ResourceUtil.isSyntheticResource(resource) ||
                ResourceUtil.isNonExistingResource(resource)) {

            RequestPathInfo pathInfo = request.getRequestPathInfo();
            String path = pathInfo.getResourcePath();
            String ext = pathInfo.getExtension();
            String suffix = pathInfo.getSuffix();

            if (StringUtils.isNotBlank(suffix) && path.endsWith(suffix)) {
                path = path.substring(0, path.length() - suffix.length());
            }

            int nameSeparator = path.lastIndexOf('/');
            String name = path.substring(nameSeparator + 1);
            path = path.substring(0, nameSeparator);
            name = name.replaceAll("(\\." + ext + ")?\\.(asset|adaptive)\\..*$", "");

            ResourceResolver resolver = request.getResourceResolver();
            resource = resolver.resolve(request, path + "/" + name + "." + ext);
            if (LOG.isDebugEnabled()) {
                LOG.debug("resolve: '//" + request.getServerName() + path + "/" + name + "." + ext + "': " + resource);
            }
            if (ResourceUtil.isSyntheticResource(resource) ||
                    ResourceUtil.isNonExistingResource(resource)) {
                resource = resolver.resolve(request, path + "/" + name);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("resolve: '//" + request.getServerName() + path + "/" + name + "': " + resource);
                }
            }
        }

        return resource;
    }
}
