package com.composum.assets.commons.widget;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public class SearchModel extends NavigatorBase {

    public SearchModel(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public SearchModel() {
        super();
    }

    @Override
    protected List<Thumbnail> findThumbnails() {
        List<Thumbnail> thumbnails = new ArrayList<>();
        for (Resource child : resource.getChildren()) {
            Thumbnail.add(context, child, thumbnails);
        }
        return thumbnails;
    }
}
