package com.composum.assets.commons.widget;

import com.composum.assets.commons.service.AssetsService;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.RequestUtil;
import com.composum.sling.platform.staging.search.SearchService;
import com.composum.sling.platform.staging.search.SearchTermParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

public class SearchModel extends NavigatorBase {

    private static final Logger LOG = LoggerFactory.getLogger(SearchModel.class);

    public static final String PARAM_ROOT = "root";
    public static final String PARAM_TERM = "term";

    private transient String searchRoot;
    private transient String searchTerm;

    private transient AssetsService assetsService;

    public SearchModel(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public SearchModel() {
        super();
    }

    @Override
    protected List<Thumbnail> findThumbnails() {
        List<Thumbnail> thumbnails = new ArrayList<>();
        try {
            String searchTerm = getSearchTerm();
            if (StringUtils.isNotBlank(searchTerm)) {
                for (SearchService.Result item : getAssetsService().search(context,
                        getSearchRoot(), searchTerm, null, 0, 100)) {
                    Thumbnail.add(context, item.getTarget(), thumbnails);
                }
            }
        } catch (SearchTermParseException | RepositoryException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return thumbnails;
    }

    public String getSearchRoot() {
        if (searchRoot == null) {
            SlingHttpServletRequest request = getRequest();
            if (request != null) {
                searchRoot = RequestUtil.getParameter(request, PARAM_ROOT, "/content");
            }
        }
        return searchRoot;
    }

    public String getSearchTerm() {
        if (searchTerm == null) {
            SlingHttpServletRequest request = getRequest();
            if (request != null) {
                searchTerm = RequestUtil.getParameter(request, PARAM_TERM, "");
            }
        }
        return searchTerm;
    }

    protected AssetsService getAssetsService() {
        if (assetsService == null) {
            assetsService = context.getService(AssetsService.class);
        }
        return assetsService;
    }
}
