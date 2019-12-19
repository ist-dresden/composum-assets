package com.composum.assets.commons.widget;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.util.List;

public abstract class NavigatorBase extends AbstractServletBean {

    public static final StringFilter TYPE_FILTER = new StringFilter.WhiteList("small", "large", "list");

    public static final String PARAM_FILTER = "filter";

    protected Resource selected;

    private transient String viewType;
    private transient String filterKey;

    private transient List<Thumbnail> thumbnails;

    public NavigatorBase() {
        super();
    }

    public NavigatorBase(BeanContext context, Resource resource) {
        super(context, resource);
    }

    @Override
    public void initialize(BeanContext context, Resource resource) {
        selected = resource;
        ResourceFilter folderFilter = context.getService(AssetsConfiguration.class).getAssetFolderFilter();
        while (resource != null && !"/".equals(resource.getPath()) && !folderFilter.accept(resource)) {
            resource = resource.getParent();
        }
        super.initialize(context, resource);
    }

    public Resource getSelected() {
        return selected;
    }

    public String getSelectedPath() {
        return selected != null ? selected.getPath() : "";
    }

    public List<Thumbnail> getThumbnails() {
        if (thumbnails == null) {
            thumbnails = findThumbnails();
        }
        return thumbnails;
    }

    protected abstract List<Thumbnail> findThumbnails();

    public String getViewType() {
        if (viewType == null) {
            SlingHttpServletRequest request = getRequest();
            if (request != null) {
                for (String selector : request.getRequestPathInfo().getSelectors()) {
                    if (TYPE_FILTER.accept(selector)) {
                        viewType = selector;
                        break;
                    }
                }
            }
            if (StringUtils.isBlank(viewType)) {
                viewType = "small";
            }
        }
        return viewType;
    }

    public String getFilterKey() {
        if (filterKey == null) {
            SlingHttpServletRequest request = getRequest();
            if (request != null) {
                filterKey = RequestUtil.getParameter(request, PARAM_FILTER, "");
            }
        }
        return filterKey;
    }
}
