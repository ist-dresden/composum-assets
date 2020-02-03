/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.service;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ImageConfig;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.AssetVariation;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.widget.Thumbnail;
import com.composum.sling.clientlibs.handle.FileHandle;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.SlingBean;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.MimeTypeUtil;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.StagingConstants;
import com.composum.sling.platform.staging.search.SearchService;
import com.composum.sling.platform.staging.search.SearchTermParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.tika.mime.MimeType;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.composum.assets.commons.AssetsConstants.ASSET_CONFIG;
import static com.composum.assets.commons.AssetsConstants.IMAGE_CONFIG;
import static com.composum.assets.commons.AssetsConstants.NODE_TYPE_ASSET;
import static com.composum.assets.commons.AssetsConstants.NODE_TYPE_ASSET_CONFIG;
import static com.composum.assets.commons.AssetsConstants.NODE_TYPE_IMAGE_CONFIG;
import static com.composum.assets.commons.service.AssetsSearchPlugin.SELECTOR_ASSET;
import static javax.jcr.nodetype.NodeType.MIX_CREATED;
import static javax.jcr.nodetype.NodeType.MIX_LAST_MODIFIED;
import static javax.jcr.nodetype.NodeType.MIX_VERSIONABLE;

@Component(
        service = AssetsService.class
)
public class DefaultAssetsService implements AssetsService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAssetsService.class);

    public static final String PATH_SEP = "/";

    public static final Map<String, Object> FOLDER_PROPERTIES = new HashMap<String, Object>() {{
        put(ResourceUtil.PROP_PRIMARY_TYPE, ResourceUtil.TYPE_SLING_FOLDER);
    }};
    public static final Map<String, Object> CONTENT_PROPERTIES = new HashMap<String, Object>() {{
        put(ResourceUtil.PROP_PRIMARY_TYPE, JcrConstants.NT_UNSTRUCTURED);
        put(ResourceUtil.PROP_MIXINTYPES, new String[]{MIX_VERSIONABLE, MIX_CREATED, MIX_LAST_MODIFIED});
    }};

    public static final Map<String, Object> ASSET_CONFIG_PROPERTIES = new HashMap<String, Object>() {{
        put(ResourceUtil.PROP_PRIMARY_TYPE, AssetsConstants.NODE_TYPE_ASSET_CONFIG);
        put(ResourceUtil.PROP_RESOURCE_TYPE, AssetsConstants.RESOURCE_TYPE_CONFIG);
    }};
    public static final Map<String, Object> IMAGE_CONFIG_PROPERTIES = new HashMap<String, Object>() {{
        put(ResourceUtil.PROP_PRIMARY_TYPE, AssetsConstants.NODE_TYPE_IMAGE_CONFIG);
        put(ResourceUtil.PROP_RESOURCE_TYPE, AssetsConstants.RESOURCE_TYPE_CONFIG);
    }};
    public static final Map<String, Object> VARIATION_CONFIG_PROPERTIES = new HashMap<String, Object>() {{
        put(ResourceUtil.PROP_PRIMARY_TYPE, AssetsConstants.NODE_TYPE_VARIATION_CONFIG);
        put(ResourceUtil.PROP_RESOURCE_TYPE, AssetsConstants.RESOURCE_TYPE_VARIATION_CONFIG);
    }};
    public static final Map<String, Object> RENDITION_CONFIG_PROPERTIES = new HashMap<String, Object>() {{
        put(ResourceUtil.PROP_PRIMARY_TYPE, AssetsConstants.NODE_TYPE_RENDITION_CONFIG);
        put(ResourceUtil.PROP_RESOURCE_TYPE, AssetsConstants.RESOURCE_TYPE_RENDITION_CONFIG);
    }};

    public static final Map<String, Map<String, Object>> CONFIG_PROPERTIES = new HashMap<String, Map<String, Object>>() {{
        put(AssetsConstants.NODE_TYPE_VARIATION_CONFIG, VARIATION_CONFIG_PROPERTIES);
        put(AssetsConstants.NODE_TYPE_RENDITION_CONFIG, RENDITION_CONFIG_PROPERTIES);
    }};
    public static final Map<String, String> CONFIG_CHILD_TYPE = new HashMap<String, String>() {{
        put(AssetsConstants.NODE_TYPE_ASSET_CONFIG, AssetsConstants.NODE_TYPE_VARIATION_CONFIG);
        put(AssetsConstants.NODE_TYPE_VARIATION_CONFIG, AssetsConstants.NODE_TYPE_RENDITION_CONFIG);
    }};

    public static final Map<String, Object> IMAGE_PROPERTIES = new HashMap<String, Object>() {{
        put(ResourceUtil.PROP_PRIMARY_TYPE, NODE_TYPE_ASSET);
        put(ResourceUtil.PROP_RESOURCE_TYPE, ImageAsset.RESOURCE_TYPE);
    }};
    public static final Map<String, Object> IMAGE_CONTENT_PROPERTIES = new HashMap<String, Object>() {{
        put(ResourceUtil.PROP_PRIMARY_TYPE, AssetsConstants.NODE_TYPE_ASSET_CONTENT);
    }};
    public static final Map<String, Object> IMAGE_META_PROPERTIES = new HashMap<String, Object>() {{
        put(ResourceUtil.PROP_PRIMARY_TYPE, StagingConstants.TYPE_METADATA);
        put(ResourceUtil.PROP_RESOURCE_TYPE, AssetsConstants.RESOURCE_TYPE_META);
    }};

    public static final Map<String, Object> IMAGE_FILE_PROPERTIES = new HashMap<String, Object>() {{
        put(ResourceUtil.PROP_PRIMARY_TYPE, ResourceUtil.TYPE_FILE);
    }};
    public static final Map<String, Object> IMAGE_FILE_CONTENT_PROPERTIES = new HashMap<String, Object>() {{
        put(ResourceUtil.PROP_PRIMARY_TYPE, ResourceUtil.TYPE_RESOURCE);
    }};

    @Reference
    protected AdaptiveImageService adaptiveImageService;

    @Reference
    protected MetaPropertiesService metaPropertiesService;

    @Reference
    protected SearchService searchService;

    @Override
    @Nonnull
    public Iterable<SearchService.Result> search(@Nonnull final BeanContext context, @Nonnull final String searchRoot,
                                                 @Nonnull final String searchTerm, @Nullable final ResourceFilter searchFilter,
                                                 int offset, @Nullable final Integer limit)
            throws SearchTermParseException, RepositoryException {
        return searchService.search(context, SELECTOR_ASSET, searchRoot, searchTerm, searchFilter, offset, limit);
    }

    @Override
    @Nonnull
    public Resource uploadImageAsset(@Nonnull final BeanContext context,
                                     @Nonnull final String assetOrParentPath, @Nullable final String name,
                                     @Nonnull final String variation, @Nonnull final InputStream imageData)
            throws Exception {
        ResourceResolver resolver = context.getResolver();
        String pathAndName = assetOrParentPath + (StringUtils.isNotBlank(name) ? ("/" + name) : "");
        Resource assetResource = resolver.getResource(pathAndName);
        if (!ResourceUtil.isNodeType(assetResource, NODE_TYPE_ASSET)) {
            if (StringUtils.isNotBlank(name)) {
                return createImageAsset(context, assetOrParentPath, name, variation, imageData);
            } else {
                throw new PersistenceException("name missed for asset creation");
            }
        } else {
            changeImageAsset(context, assetResource, variation, imageData);
            return assetResource;
        }
    }

    @Override
    @Nonnull
    public Resource createImageAsset(@Nonnull final BeanContext context,
                                     @Nonnull final String parentPath, @Nonnull final String name,
                                     @Nonnull final String variation, @Nonnull final InputStream imageData)
            throws Exception {
        ResourceResolver resolver = context.getResolver();
        String pathAndName = parentPath + "/" + name;
        Resource assetResource = resolver.getResource(pathAndName);
        deleteAsset(assetResource);
        Resource parent = getOrCreateFolder(resolver, parentPath);
        LOG.info("image.create: {}", pathAndName);
        assetResource = resolver.create(parent, name, IMAGE_PROPERTIES);
        Resource content = resolver.create(assetResource, ResourceUtil.CONTENT_NODE, IMAGE_CONTENT_PROPERTIES);
        ImageAsset imageAsset = new ImageAsset(context, assetResource);
        storeImageOriginal(imageAsset, variation, imageData);
        return assetResource;
    }

    @Override
    public void transformToImageAsset(@Nonnull final BeanContext context, @Nonnull final Resource imageResource)
            throws PersistenceException, RepositoryException {
        if (!ResourceUtil.isNodeType(imageResource, NODE_TYPE_ASSET)) {
            ResourceResolver resolver = context.getResolver();
            Resource parent = Objects.requireNonNull(imageResource.getParent());
            String assetPath = imageResource.getPath();
            String name = imageResource.getName();
            String tmpPath = getAssetTmpPath(resolver, name);
            // CRUD not possible because the resolvers 'move' performs 'copy' internally
            // and the file is referenceable... - exception on copying the 'uuid'!
            Session session = Objects.requireNonNull(resolver.adaptTo(Session.class));
            session.move(assetPath, tmpPath);
            resolver.refresh();
            Resource assetResource = resolver.create(parent, name, IMAGE_PROPERTIES);
            Resource content = resolver.create(assetResource, ResourceUtil.CONTENT_NODE, IMAGE_CONTENT_PROPERTIES);
            ImageAsset imageAsset = new ImageAsset(context, assetResource);
            AssetVariation variation = imageAsset.getOrCreateVariationForOriginal(null);
            AssetRendition rendition = variation.getOrCreateOriginal(null);
            session.move(tmpPath, rendition.getPath() + "/" + name);
            resolver.refresh();
        }
    }

    @Override
    public void transformToSimpleImage(@Nonnull final BeanContext context, @Nonnull final Resource assetResource)
            throws PersistenceException, RepositoryException {
        if (!ResourceUtil.isNodeType(assetResource, JcrConstants.NT_FILE)) {
            ResourceResolver resolver = context.getResolver();
            String assetPath = assetResource.getPath();
            ImageAsset imageAsset = new ImageAsset(context, assetResource);
            AssetRendition original = imageAsset.getOriginal();
            FileHandle file = original.getFile();
            String filePath = file.getPath();
            String tmpPath = getAssetTmpPath(resolver, assetResource.getName());
            // CRUD not possible because - see above...
            Session session = Objects.requireNonNull(resolver.adaptTo(Session.class));
            session.move(filePath, tmpPath);
            session.removeItem(assetPath);
            session.save(); // FIXME - workaround for an OAK exception (change of protected version history property...)
            session.move(tmpPath, assetPath);
            resolver.refresh();
        }
    }

    @Override
    public void changeImageAsset(@Nonnull final BeanContext context, @Nonnull final Resource assetResource,
                                 @Nonnull final String variation, @Nonnull final InputStream imageData)
            throws Exception {
        ImageAsset imageAsset = new ImageAsset(context, assetResource);
        storeImageOriginal(imageAsset, variation, imageData);
    }

    protected void storeImageOriginal(@Nonnull final ImageAsset imageAsset, @Nonnull final String variationKey,
                                      @Nonnull final InputStream imageData)
            throws Exception {
        AssetVariation variation = imageAsset.getOrCreateVariationForOriginal(variationKey);
        AssetRendition rendition = variation.getOrCreateOriginal(variationKey);
        FileHandle file = rendition.getFile();
        if (file == null) {
            Resource renditionResource = rendition.getResource();
            ResourceResolver resolver = renditionResource.getResourceResolver();
            Resource fileResource = resolver.create(renditionResource, imageAsset.getName(), IMAGE_FILE_PROPERTIES);
            resolver.create(fileResource, ResourceUtil.CONTENT_NODE, IMAGE_FILE_CONTENT_PROPERTIES);
            file = new FileHandle(fileResource);
        }
        file.storeContent(imageData);
        file.updateLastModified();
        ResourceHandle fileContent = file.getContent();
        MimeType mimeType = MimeTypeUtil.getMimeType(fileContent);
        if (mimeType != null) {
            fileContent.setProperty(ResourceUtil.PROP_MIME_TYPE, mimeType.toString());
        }
        adaptiveImageService.dropRenditions(imageAsset.getPath(), variationKey, null);
        metaPropertiesService.adjustMetaProperties(file.getResource().getResourceResolver(), file.getResource());
    }

    @Override
    public void deleteAsset(@Nullable final Resource assetResource)
            throws PersistenceException {
        if (assetResource != null && !ResourceUtil.isNonExistingResource(assetResource)) {
            LOG.info("asset.delete: " + assetResource.getPath());
            assetResource.getResourceResolver().delete(assetResource);
        }
    }

    @Override
    @Nullable
    public Resource getOrCreateConfiguration(@Nonnull final BeanContext context,
                                             @Nonnull final String path, boolean commit)
            throws PersistenceException {
        Resource config = null;
        ResourceResolver resolver = context.getResolver();
        Resource holder = resolver.getResource(path);
        if (holder != null) {
            String configName = AssetConfig.CHILD_NAME;
            String configType = AssetsConstants.NODE_TYPE_ASSET_CONFIG;
            Map<String, Object> configProps = ASSET_CONFIG_PROPERTIES;
            if (ResourceUtil.isResourceType(holder, NODE_TYPE_ASSET)) {
                configType = AssetsConstants.NODE_TYPE_IMAGE_CONFIG;
                configName = ImageConfig.CHILD_NAME;
                configProps = IMAGE_CONFIG_PROPERTIES;
            }
            Resource content = holder.getChild(JcrConstants.JCR_CONTENT);
            if (content == null) {
                LOG.info("createContent: " + path);
                content = resolver.create(holder, JcrConstants.JCR_CONTENT, CONTENT_PROPERTIES);
            } else {
                List<Resource> configList = ResourceUtil.getChildrenByType(content, configType);
                if (configList.size() > 0) {
                    config = configList.get(0);
                }
            }
            if (config == null) {
                LOG.info("createConfig: " + path);
                config = resolver.create(content, configName, configProps);
                if (commit) {
                    resolver.commit();
                } else {
                    resolver.refresh();
                }
            }
        }
        return config;
    }

    @Override
    @Nullable
    public Resource copyConfigNode(@Nonnull final BeanContext context, @Nonnull final String holderPath,
                                   @Nonnull final Resource template, boolean commit)
            throws PersistenceException {
        Resource config = null;
        ResourceResolver resolver = context.getResolver();
        Resource holder = resolver.getResource(holderPath);
        if (holder != null) {
            String name = ASSET_CONFIG;
            String type = NODE_TYPE_ASSET_CONFIG;
            if (ResourceUtil.isResourceType(holder, NODE_TYPE_ASSET)) {
                name = IMAGE_CONFIG;
                type = NODE_TYPE_IMAGE_CONFIG;
            }
            Resource content = holder.getChild(JcrConstants.JCR_CONTENT);
            if (content == null) {
                LOG.info("createContent: '{}'", holderPath);
                content = resolver.create(holder, JcrConstants.JCR_CONTENT, CONTENT_PROPERTIES);
                resolver.commit(); // necessary for usage by the workspace
            }
            String path = content.getPath() + "/" + name;
            Resource toReplace = content.getChild(name);
            if (toReplace != null) {
                resolver.delete(toReplace);
            }
            resolver.refresh();
            LOG.info("copy template '{}': '{}' ({})", template.getPath(), path, type);
            try {
                Workspace workspace = Objects.requireNonNull(resolver.adaptTo(Session.class)).getWorkspace();
                workspace.copy(template.getPath(), path);
            } catch (RepositoryException ex) {
                throw new PersistenceException(ex.getMessage(), ex);
            }
            resolver.refresh();
            config = resolver.getResource(path);
            ModifiableValueMap values = Objects.requireNonNull(config.adaptTo(ModifiableValueMap.class));
            values.put(JcrConstants.JCR_PRIMARYTYPE, type);
            if (commit) {
                resolver.commit();
            } else {
                resolver.refresh();
            }
        }
        return config;
    }

    @Override
    @Nullable
    public Resource createConfigNode(@Nonnull final BeanContext context,
                                     @Nonnull Resource parent, @Nullable final String name, boolean commit)
            throws PersistenceException {
        Resource configNode = null;
        String parentType = ResourceUtil.getPrimaryType(parent);
        String childType = CONFIG_CHILD_TYPE.get(parentType);
        if (childType == null) {
            if (AssetsConstants.NODE_TYPE_RENDITION_CONFIG.equals(parentType)) {
                // if the parent is a rendition config create a sibling
                if (StringUtils.isNotBlank(name)) {
                    parent = Objects.requireNonNull(parent.getParent());
                    configNode = parent.getChild(name);
                    parentType = ResourceUtil.getPrimaryType(parent);
                    childType = CONFIG_CHILD_TYPE.get(parentType);
                }
            } else {
                // otherwise we assume that the parent is a folder
                // which should be transformed into a configuration node
                return getOrCreateConfiguration(context, parent.getPath(), commit);
            }
        }
        if (StringUtils.isNotBlank(name)) {
            configNode = parent.getChild(name);
            if (configNode == null && StringUtils.isNotBlank(childType)) {
                ResourceResolver resolver = context.getResolver();
                LOG.info("createConfigNode: " + parent.getPath() + ":" + name);
                configNode = resolver.create(parent, name, CONFIG_PROPERTIES.get(childType));
                if (commit) {
                    resolver.commit();
                } else {
                    resolver.refresh();
                }
            }
        }
        return configNode;
    }

    @Override
    public void deleteConfigNode(@Nonnull final BeanContext context, @Nullable Resource configNode, boolean commit)
            throws PersistenceException {
        if (configNode != null) {
            ResourceResolver resolver = context.getResolver();
            String configPath = configNode.getPath();
            String configType = ResourceUtil.getPrimaryType(configNode);
            Resource content = null;
            if (AssetsConstants.NODE_TYPE_ASSET_CONFIG.equals(configType)) {
                LOG.info("delete assetConfig: " + configPath);
                content = configNode.getParent();
            } else if (AssetsConstants.NODE_TYPE_IMAGE_CONFIG.equals(configType)) {
                LOG.info("delete imageConfig: " + configPath);
            } else {
                LOG.info("config.deleteAspect: " + configPath);
            }
            resolver.delete(configNode);
            if (content != null) { // asset config removed from a folder...
                if (!content.getChildren().iterator().hasNext()) {
                    // remove 'jcr:content' from asset configuration folder if content has no children
                    resolver.delete(content);
                }
            }
            if (commit) {
                resolver.commit();
            } else {
                resolver.refresh();
            }
        }
    }

    protected Resource getOrCreateFolder(@Nonnull final ResourceResolver resolver, @Nonnull final String path)
            throws PersistenceException {
        Resource resource = resolver.getResource(path);
        if (resource == null) {
            String parentPath = StringUtils.substringBeforeLast(path, PATH_SEP);
            String name = StringUtils.substringAfterLast(path, PATH_SEP);
            Resource parent = getOrCreateFolder(resolver, parentPath);
            LOG.info("folder.create: " + path);
            resource = resolver.create(parent, name, FOLDER_PROPERTIES);
        }
        return resource;
    }

    protected String getAssetTmpPath(@Nonnull final ResourceResolver resolver, @Nonnull final String name)
            throws PersistenceException {
        return getAssetTmpFolder(resolver).getPath() + "/" + name;
    }

    protected Resource getAssetTmpFolder(@Nonnull final ResourceResolver resolver)
            throws PersistenceException {
        return getOrCreateFolder(resolver, "/var/tmp/assets");
    }

    /**
     * the bean / model factory implementation for abstract base model instances
     */
    @Nonnull
    @Override
    public SlingBean createBean(@Nonnull final BeanContext context, @Nonnull final Resource resource,
                                @Nonnull final Class<? extends SlingBean> type)
            throws InstantiationException {
        if (type.isAssignableFrom(Thumbnail.class)) {
            return Thumbnail.create(context, resource);
        }
        throw new InstantiationException("requested type not supported (" + type.getName() + ")");
    }
}
