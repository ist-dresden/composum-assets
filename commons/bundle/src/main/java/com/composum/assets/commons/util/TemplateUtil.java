package com.composum.assets.commons.util;

import com.composum.sling.core.util.ResourceUtil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import java.io.InputStream;

public class TemplateUtil {

    public static final String TEMPLATE_ENCODING = "UTF-8";

    public static InputStream getTemplate(ResourceResolver resolver, String path) throws RepositoryException {
        return getTemplate(resolver.getResource(path));

    }

    public static InputStream getTemplate(Resource resource) throws RepositoryException {
        Binary binary = null;
        if (resource != null) {
            binary = ResourceUtil.getBinaryData(resource);
        }
        return binary != null ? binary.getStream() : null;
    }
}
