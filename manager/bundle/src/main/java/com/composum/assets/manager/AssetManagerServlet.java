package com.composum.assets.manager;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.servlet.AbstractConsoleServlet;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.util.regex.Pattern;

import static com.composum.assets.manager.AssetManagerServlet.SERVLET_PATH;

/**
 * The general hook (servlet) for the Asset Manager feature provides the path '/bin/assets.html/...'.
 */
@Component(
        service = Servlet.class,
        name = "Composum Asset Manager Console Servlet",
        property = {
                Constants.SERVICE_DESCRIPTION + "=The general hook (servlet) for the Asset Manager feature provides " +
                        "the path '/bin/assets.html/...",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=" + SERVLET_PATH,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        }
)
public class AssetManagerServlet extends AbstractConsoleServlet {

    public static final String SERVLET_PATH = "/bin/assets";

    public static final String RESOURCE_TYPE = "composum/assets/manager";

    public static final Pattern PATH_PATTERN = Pattern.compile("^(" + SERVLET_PATH + "(\\.[^/]+)?\\.html)(/.*)?$");

    @Override
    protected String getServletPath(BeanContext context) {
        return SERVLET_PATH;
    }

    @Override
    protected Pattern getPathPattern(BeanContext context) {
        return PATH_PATTERN;
    }

    @Override
    protected String getResourceType(BeanContext context) {
        return RESOURCE_TYPE;
    }
}
