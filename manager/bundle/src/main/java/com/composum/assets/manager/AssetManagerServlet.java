package com.composum.assets.manager;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.servlet.AbstractConsoleServlet;
import org.apache.felix.scr.annotations.sling.SlingServlet;

import java.util.regex.Pattern;

/**
 * The general hook (servlet) for the Asset Manager feature provides the path '/bin/assets.html/...'.
 */
@SlingServlet(
        paths = "/bin/assets",
        methods = {"GET"}
)
public class AssetManagerServlet extends AbstractConsoleServlet {

    public static final String SERVLET_PATH = "/bin/assets.html";

    public static final String RESOURCE_TYPE = "composum/assets/manager";

    public static final Pattern PATH_PATTERN = Pattern.compile("^(/bin/assets(\\.[^/]+)?\\.html)(/.*)?$");

    @Override
    protected Pattern getPathPattern(BeanContext context) {
        return PATH_PATTERN;
    }

    @Override
    protected String getResourceType(BeanContext context) {
        return RESOURCE_TYPE;
    }
}