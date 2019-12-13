package com.composum.assets.commons.widget;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.nodes.NodesConfiguration;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FolderModel extends NavigatorBase {

    private transient List<FolderModel> subfolders;

    public FolderModel() {
        super();
    }

    public FolderModel(BeanContext context, Resource resource) {
        super(context, resource);
    }

    @Override
    protected List<Thumbnail> findThumbnails() {
        List<Thumbnail> thumbnails = new ArrayList<>();
        for (Resource child : resource.getChildren()) {
            Thumbnail.add(context, child, thumbnails);
        }
        if (!context.getService(NodesConfiguration.class).getOrderableNodesFilter().accept(resource)) {
            Collections.sort(thumbnails);
        }
        return thumbnails;
    }

    public List<FolderModel> getSubfolders() {
        if (subfolders == null) {
            subfolders = new ArrayList<>();
            AssetsConfiguration assetsConfig = context.getService(AssetsConfiguration.class);
            ResourceFilter folderFilter = new ResourceFilter.FilterSet(
                    ResourceFilter.FilterSet.Rule.and,
                    assetsConfig.getAssetPathFilter(),
                    assetsConfig.getTreeIntermediateFilter());
            for (Resource child : resource.getChildren()) {
                if (folderFilter.accept(child)) {
                    subfolders.add(new FolderModel(context, child));
                }
            }
            if (!context.getService(NodesConfiguration.class).getOrderableNodesFilter().accept(resource)) {
                subfolders.sort(Comparator.comparing(FolderModel::getName));
            }
        }
        return subfolders;
    }
}
