/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.mapping.jcr.ResourceFilterMapping;
import com.composum.sling.core.util.RequestUtil;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The configuration service for the integration of Assets into Pages.
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Assets Module Configuration"
        }
)
@Designate(ocd = AssetsConfigImpl.Configuration.class)
public class AssetsConfigImpl implements AssetsConfiguration {

    public static final String ASSETS_MODULE_CONFIG_CLASS = "com.composum.assets.commons.AssetsConfiguration";

    @ObjectClassDefinition(
            name = "Composum Assets Module Configuration"
    )
    public @interface Configuration {

        @AttributeDefinition(
                description = "the filter configuration to set the scope to the Composum Assets objects"
        )
        String assetNodeFilterRule() default "PrimaryType(+'^cpa:(Asset)$')";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to image files"
        )
        String imageNodeFilterRule() default "and{PrimaryType(+'^nt:(file)$'),MimeType(+'^image/')}";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to video files"
        )
        String videoNodeFilterRule() default "and{PrimaryType(+'^nt:(file)$'),MimeType(+'^video/')}";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to audio files"
        )
        String audioNodeFilterRule() default "and{PrimaryType(+'^nt:(file)$'),MimeType(+'^audio/')}";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to document files"
        )
        String documentNodeFilterRule() default "and{PrimaryType(+'^nt:(file)$'),MimeType(+'^application/(pdf)')}";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to binary files"
        )
        String binaryNodeFilterRule() default "and{PrimaryType(+'^nt:(file)$'),MimeType(+'^application/(zip)')}";

        @AttributeDefinition(
                description = "the filter configuration to set the scope all files"
        )
        String fileNodeFilterRule() default "PrimaryType(+'^nt:(file)$')";

        @AttributeDefinition(
                description = "the filter configuration to restrict Assets content paths"
        )
        String contentRoot() default "/content";

        @AttributeDefinition(
                description = "the filter configuration for tree itermediate nodes (folders, sites, ...)"
        )
        String treeIntermediateFilterRule() default "or{Folder(),PrimaryType(+'^cpp:(Site)$')}";
    }

    private ResourceFilter imageAssetNodeFilter;
    private ResourceFilter imageSimpleNodeFilter;
    private ResourceFilter imageNodeFilter;
    private ResourceFilter videoNodeFilter;
    private ResourceFilter audioNodeFilter;
    private ResourceFilter documentNodeFilter;
    private ResourceFilter binaryNodeFilter;
    private ResourceFilter anyNodeFilter;

    private ResourceFilter imageAssetFileFilter;
    private ResourceFilter imageAssetOriginalFilter;
    private ResourceFilter imageSimpleFileFilter;
    private ResourceFilter imageFileFilter;
    private ResourceFilter videoFileFilter;
    private ResourceFilter audioFileFilter;
    private ResourceFilter documentFileFilter;
    private ResourceFilter binaryFileFilter;
    private ResourceFilter anyFileFilter;

    private Map<String, ConfigurableFilter> availableFilters;
    private Map<String, ResourceFilter> fileFilters;

    private String contentRoot;
    private ResourceFilter contentRootFilter;
    private ResourceFilter treeIntermediateFilter;
    private ResourceFilter assetContentFilter = new AssetContentFilter();

    protected Configuration config;

    // asset resource filters (file filters)

    @Nonnull
    @Override
    public Set<String> getFileFilterKeys() {
        return fileFilters.keySet();
    }

    @Nullable
    @Override
    public ResourceFilter getFileFilter(@Nonnull final BeanContext context, @Nonnull String key) {
        return fileFilters.get(key);
    }

    @Nonnull
    @Override
    public ResourceFilter getImageAssetFileFilter() {
        return imageAssetFileFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getImageAssetOriginalFilter() {
        return imageAssetOriginalFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getImageSimpleFileFilter() {
        return imageSimpleFileFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getImageFileFilter() {
        return imageFileFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getVideoFileFilter() {
        return videoFileFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getAudioFileFilter() {
        return audioFileFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getDocumentFileFilter() {
        return documentFileFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getBinaryFileFilter() {
        return binaryFileFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getAnyFileFilter() {
        return anyFileFilter;
    }

    // tree node filters

    @Nonnull
    @Override
    public Set<String> getNodeFilterKeys() {
        return availableFilters.keySet();
    }

    @Nonnull
    @Override
    public Collection<ConfigurableFilter> getNodeFilters() {
        return availableFilters.values();
    }

    @Nonnull
    @Override
    public ResourceFilter getNodeFilter(@Nonnull SlingHttpServletRequest request, @Nonnull String key) {
        ConfigurableFilter filter = availableFilters.get(key);
        return filter != null ? filter.filter
                : ASSET_FILTER_IMG_ASSET.equals(key) ? imageSimpleNodeFilter : anyNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getRequestNodeFilter(@Nonnull SlingHttpServletRequest request,
                                               @Nonnull String paramName, @Nonnull String defaultFilter) {
        String filter = RequestUtil.getParameter(request, paramName, defaultFilter);
        return getNodeFilter(request, filter);
    }

    @Nonnull
    @Override
    public ResourceFilter getImageAssetNodeFilter() {
        return imageAssetNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getImageSimpleNodeFilter() {
        return imageSimpleNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getImageNodeFilter() {
        return imageNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getVideoNodeFilter() {
        return videoNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getAudioNodeFilter() {
        return audioNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getDocumentNodeFilter() {
        return documentNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getBinaryNodeFilter() {
        return binaryNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getAnyNodeFilter() {
        return anyNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getContentRootFilter() {
        return contentRootFilter;
    }

    @Nonnull
    @Override
    public String getContentRoot() {
        return contentRoot;
    }

    @Nonnull
    @Override
    public ResourceFilter getTreeIntermediateFilter() {
        return treeIntermediateFilter;
    }

    /**
     * Creates a 'tree filter' as combination with the configured filter and the rules for the
     * 'intermediate' nodes (folders) to traverse up to the target nodes.
     *
     * @param configuredFilter the filter for the target nodes
     */
    protected ResourceFilter buildTreeFilter(ResourceFilter configuredFilter,
                                             ResourceFilter intermediateFilter) {
        return new ResourceFilter.FilterSet(
                ResourceFilter.FilterSet.Rule.tree, configuredFilter, intermediateFilter);
    }

    @Activate
    @Modified
    public void activate(Configuration config) {
        this.config = config;
        this.fileFilters = new LinkedHashMap<>();
        this.availableFilters = new LinkedHashMap<>();
        contentRoot = config.contentRoot();
        contentRootFilter = new ResourceFilter.PathFilter(new StringFilter.WhiteList("^" + contentRoot));
        treeIntermediateFilter = new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                contentRootFilter, ResourceFilterMapping.fromString(config.treeIntermediateFilterRule()));
        imageSimpleFileFilter = new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                ResourceFilterMapping.fromString(config.imageNodeFilterRule()),
                assetContentFilter);
        imageAssetFileFilter = ResourceFilterMapping.fromString(config.assetNodeFilterRule());
        imageAssetOriginalFilter = new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                ResourceFilterMapping.fromString(config.imageNodeFilterRule()), new ImageRenditionFileFilter());
        imageFileFilter = new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.or,
                imageSimpleFileFilter, imageAssetFileFilter);
        videoFileFilter = ResourceFilterMapping.fromString(config.videoNodeFilterRule());
        audioFileFilter = ResourceFilterMapping.fromString(config.audioNodeFilterRule());
        documentFileFilter = ResourceFilterMapping.fromString(config.documentNodeFilterRule());
        binaryFileFilter = ResourceFilterMapping.fromString(config.binaryNodeFilterRule());
        imageSimpleNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                contentRootFilter, imageSimpleFileFilter), treeIntermediateFilter);
        imageAssetNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                contentRootFilter, imageAssetFileFilter), treeIntermediateFilter);
        imageNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.or,
                imageSimpleFileFilter, imageAssetFileFilter), treeIntermediateFilter);
        videoNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                contentRootFilter, videoFileFilter), treeIntermediateFilter);
        audioNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                contentRootFilter, audioFileFilter), treeIntermediateFilter);
        documentNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                contentRootFilter, documentFileFilter), treeIntermediateFilter);
        binaryNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                contentRootFilter, binaryFileFilter), treeIntermediateFilter);
        anyFileFilter = new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.or,
                imageFileFilter, videoFileFilter, audioFileFilter,
                documentFileFilter, binaryFileFilter);
        anyNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                contentRootFilter, anyFileFilter), treeIntermediateFilter);
        fileFilters.put(ASSET_FILTER_ALL, anyFileFilter);
        availableFilters.put(ASSET_FILTER_ALL, new ConfigurableFilter(anyNodeFilter,
                ASSET_FILTER_ALL, "All", "show all available asset object types"));
        fileFilters.put(ASSET_FILTER_IMG_ASSET, imageAssetFileFilter);
        availableFilters.put(ASSET_FILTER_IMG_ASSET, new ConfigurableFilter(imageAssetNodeFilter,
                ASSET_FILTER_IMG_ASSET, "Image Asset", "restrict to 'Composum Assets' image objects"));
        fileFilters.put(ASSET_FILTER_IMG_SIMPLE, imageSimpleFileFilter);
        availableFilters.put(ASSET_FILTER_IMG_SIMPLE, new ConfigurableFilter(imageSimpleNodeFilter,
                ASSET_FILTER_IMG_SIMPLE, "Simple Image", "restrict to simple image files"));
        fileFilters.put(ASSET_FILTER_IMAGE, imageFileFilter);
        availableFilters.put(ASSET_FILTER_IMAGE, new ConfigurableFilter(imageNodeFilter,
                ASSET_FILTER_IMAGE, "Image", "restrict to image asset or image file resources"));
        fileFilters.put(ASSET_FILTER_VIDEO, videoFileFilter);
        availableFilters.put(ASSET_FILTER_VIDEO, new ConfigurableFilter(videoNodeFilter,
                ASSET_FILTER_VIDEO, "Video", "restrict to video file objects"));
        fileFilters.put(ASSET_FILTER_AUDIO, audioFileFilter);
        availableFilters.put(ASSET_FILTER_AUDIO, new ConfigurableFilter(audioNodeFilter,
                ASSET_FILTER_AUDIO, "Audio", "restrict to audio file objects"));
        fileFilters.put(ASSET_FILTER_DOCUMENT, documentFileFilter);
        availableFilters.put(ASSET_FILTER_DOCUMENT, new ConfigurableFilter(documentNodeFilter,
                ASSET_FILTER_DOCUMENT, "Document", "restrict to document file objects"));
        fileFilters.put(ASSET_FILTER_BINARY, binaryFileFilter);
        availableFilters.put(ASSET_FILTER_BINARY, new ConfigurableFilter(binaryNodeFilter,
                ASSET_FILTER_BINARY, "Binary", "restrict to binary file objects"));
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        this.availableFilters = null;
        this.config = null;
    }

    /**
     * the filter to ignore assets content elements (originals)
     */
    public static class AssetContentFilter implements ResourceFilter {

        @Override
        public boolean accept(@Nullable Resource resource) {
            if (resource != null) {
                Resource parent = resource.getParent();
                return parent == null ||
                        !AssetsConstants.NODE_TYPE_RENDITION.equals(
                                parent.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, ""));
            }
            return false;
        }

        @Override
        public boolean isRestriction() {
            return true;
        }

        @Override
        public void toString(@Nonnull StringBuilder builder) {
            builder.append("AssetContent()");
        }
    }

    protected class ImageRenditionFileFilter implements ResourceFilter {

        @Override
        public boolean accept(@Nullable Resource resource) {
            ResourceHandle handle = ResourceHandle.use(resource);
            return getImageAssetFileFilter().accept(handle.getParent(3));
        }

        @Override
        public boolean isRestriction() {
            return true;
        }

        @Override
        public void toString(@Nonnull StringBuilder builder) {
            builder.append("ImageAssetOriginal()");
        }
    }
}
