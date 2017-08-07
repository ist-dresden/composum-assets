package com.composum.assets.commons.widget;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.handle.SimpleImage;
import com.composum.assets.commons.handle.VideoAsset;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.nodes.NodesConfiguration;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Folder extends Finder {

    private transient List<Resource> subfolders;

    public Folder(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public Folder(BeanContext context) {
        super(context);
    }

    public Folder() {
        super();
    }

    @Override
    public void initialize(BeanContext context, Resource resource) {
        while (resource != null && !"/".equals(resource.getPath()) && !ResourceFilter.FOLDER.accept(resource)) {
            resource = resource.getParent();
        }
        super.initialize(context, resource);
    }

    @Override
    protected List<Thumbnail> findThumbnails() {
        List<Thumbnail> thumbnails = new ArrayList<>();
            for (Resource child : resource.getChildren()) {
                if (ImageAsset.FILTER.accept(child)) {
                    thumbnails.add(new AssetThumbnail(child));
                } else if (SimpleImage.FILTER.accept(child)) {
                    thumbnails.add(new ImageThumbnail(child));
                } else if (VideoAsset.FILTER.accept(child)) {
                    thumbnails.add(new VideoThumbnail(child));
                }
            }
            if (!context.getService(NodesConfiguration.class).getOrderableNodesFilter().accept(resource)) {
                Collections.sort(thumbnails);
            }
        return thumbnails;
    }

    public List<Resource> getSubfolders() {
        if (subfolders == null) {
            subfolders = new ArrayList<>();
            AssetsConfiguration assetsConfig = context.getService(AssetsConfiguration.class);
            ResourceFilter folderFilter = new ResourceFilter.FilterSet(
                    ResourceFilter.FilterSet.Rule.and,
                    assetsConfig.getAssetPathFilter(),
                    assetsConfig.getTreeIntermediateFilter());
            for (Resource child : resource.getChildren()) {
                if (folderFilter.accept(child)) {
                    subfolders.add(child);
                }
            }
            if (!context.getService(NodesConfiguration.class).getOrderableNodesFilter().accept(resource)) {
                Collections.sort(subfolders, new Comparator<Resource>() {
                    @Override
                    public int compare(Resource res1, Resource res2) {
                        return res1.getName().compareTo(res2.getName());
                    }
                });
            }
        }
        return subfolders;
    }
}
