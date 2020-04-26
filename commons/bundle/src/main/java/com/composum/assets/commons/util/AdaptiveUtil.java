package com.composum.assets.commons.util;

import com.composum.assets.commons.handle.AssetRendition;
import com.composum.sling.clientlibs.handle.FileHandle;
import com.composum.sling.core.util.HttpUtil;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.XSS;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class AdaptiveUtil {

    private static final Logger LOG = LoggerFactory.getLogger(AdaptiveUtil.class);

    public static boolean sendRendition(@Nonnull final HttpServletResponse response,
                                        @Nullable final AssetRendition rendition)
            throws IOException {
        if (rendition != null) {
            FileHandle file = rendition.getFile();
            if (file.isValid()) {
                response.setContentType(rendition.getMimeType());
                try (InputStream imageStream = file.getStream()) {
                    AdaptiveUtil.sendImageStream(response, rendition, imageStream);
                }
                return true;
            }
        }
        return false;
    }

    public static void sendImageStream(@Nonnull final HttpServletResponse response,
                                       @Nonnull final AssetRendition rendition,
                                       @Nonnull final InputStream imageStream)
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
            String suffix = XSS.filter(pathInfo.getSuffix());

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
