/*
 * copyright (c) 2015.ff IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.service;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.util.TikaMetaData;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(
        name = "Composum Assets - Meta Data Extraction Service",
        immediate = true,
        metatype = true
)
@Service
public class AssetMetaPropertiesService implements MetaPropertiesService {

    private static final Logger LOG = LoggerFactory.getLogger(AssetMetaPropertiesService.class);

    public static final String IMAGE_META_DATA_FILTER = "assets.metadata.filter.image";

    @Property(
            name = IMAGE_META_DATA_FILTER,
            label = "Image Meta Data Names",
            description = "the list of patterns with the meta data property names to import",
            value = {
                    "^[Cc]ontent[ -]?[Tt]ype$",
                    "^([Ff]ile ?)?[Ss]ize$",
                    "^([Ii]mage ?)?([Ww]idth|[Hh]eight)$",
                    "^[Dd]escription$",
                    "^[Cc]opyright( ?[Uu]rl)?$",
                    "^[Aa]uthor( ?[Uu]rl)?$",
                    "^[Cc]redits( ?[Uu]rl)?$",
                    "^[Ll]icense( ?[Uu]rl)?$",
                    "^[Oo]rigin( ?[Uu]rl)?$",
                    "^[Ss]ource( ?[Uu]rl)?$",
                    "^[Ll]ocation( ?[Uu]rl)?$",
                    "^([Gg]oogle)?[Ee]arth$",
                    "^[Oo]rientation$",
                    "^([Ll]ast[ -]?)?[Mm]odified$",
                    "^[Dd]ate(/[Tt]ime)?( ?[Oo]riginal)?$",
                    "^[Dd]imension( [\\w\\s]+)?$",
                    "^[Tt]ransparency( [\\w\\s]+)?$",
                    "^.*[Cc]olor ?[Ss]pace.*$",
                    "^[Dd]ata( [\\w\\s]+)$",
                    "^[Cc]ompression( [\\w\\s]+)?$",
                    "^([\\w\\s]+ )?[Qq]uality$",
                    "^([Uu]ser ?)?[Cc]omments?$",
                    "^([XxYy][ -])?[Rr]esolution([ -][Uu]nits)?$"
            }
    )
    protected StringFilter imageMetaDataFilter;

    public static final Map<String, Object> CRUD_META_PROPERTIES;

    static {
        CRUD_META_PROPERTIES = new HashMap<>();
        CRUD_META_PROPERTIES.put(ResourceUtil.PROP_PRIMARY_TYPE, AssetsConstants.NODE_TYPE_META_DATA);
    }

    @Reference
    protected AssetsConfiguration assetsConfiguration;

    protected List<MetaStrategy> strategies;

    public interface MetaStrategy {

        boolean isMatching(Resource resource);

        void adjustMetaProperties(ResourceResolver resolver, Resource resource);
    }

    public abstract class AbstractMetaStrategy implements MetaStrategy {

        protected void prepareMetaData(Resource contentResource, TikaMetaData metadata) {
        }

        protected void addMetaData(Resource contentResource, TikaMetaData metadata)
                throws PersistenceException {
            prepareMetaData(contentResource, metadata);
            Resource metaResource = contentResource.getChild(AssetsConstants.NODE_META);
            if (metaResource == null) {
                ResourceResolver resolver = contentResource.getResourceResolver();
                metaResource = resolver.create(contentResource, AssetsConstants.NODE_META, CRUD_META_PROPERTIES);
            }
            ModifiableValueMap values = metaResource.adaptTo(ModifiableValueMap.class);
            for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                String key = entry.getKey();
                key = key.replace('/', '-').replace(':', '.');
                values.put(key, entry.getValue());
            }
        }
    }

    public abstract class FileResourceStrategy extends AbstractMetaStrategy {

        @Override
        public boolean isMatching(Resource resource) {
            Resource imageContent = getContentResource(resource);
            return imageContent != null
                    && ResourceUtil.isResourceType(imageContent, JcrConstants.NT_RESOURCE)
                    && isMatchingMimeType(getMimeType(imageContent));
        }

        protected abstract boolean isMatchingMimeType(String mimeType);

        protected void adjustMixinTypes(Resource contentResoure)
                throws RepositoryException {
            if (!ResourceUtil.isResourceType(contentResoure, AssetsConstants.MIXIN_TYPE_ASSET_RESOURCE)) {
                Node node = contentResoure.adaptTo(Node.class);
                node.addMixin(AssetsConstants.MIXIN_TYPE_ASSET_RESOURCE);
            }
        }

        protected Resource getContentResource(Resource resource) {
            if (ResourceUtil.isResourceType(resource, JcrConstants.NT_FILE)) {
                resource = resource.getChild(JcrConstants.JCR_CONTENT);
            }
            return resource;
        }

        protected String getMimeType(Resource contentResource) {
            ValueMap values = contentResource.getValueMap();
            return values.get(JcrConstants.JCR_MIMETYPE, "");
        }
    }

    public class SimpleImageStrategy extends FileResourceStrategy {

        @Override
        protected boolean isMatchingMimeType(String mimeType) {
            return mimeType.startsWith("image/");
        }

        @Override
        public void adjustMetaProperties(ResourceResolver resolver, Resource resource) {
            Resource contentResource = getContentResource(resource);
            try (InputStream content = ResourceUtil.getBinaryData(contentResource).getStream()) {
                TikaMetaData metadata = TikaMetaData.parseMetadata(content, imageMetaDataFilter);
                adjustMixinTypes(contentResource);
                addMetaData(contentResource, metadata);
            } catch (RepositoryException | IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        @Override
        protected void prepareMetaData(Resource contentResource, TikaMetaData metadata) {
            Long size = metadata.getLong("File Size", "size");
            Integer width = metadata.getInt("Image Width", "width");
            Integer height = metadata.getInt("Image Height", "height");
            Date date = metadata.getDate("Date/Time", "Date/Time Original", "date");
            if (size == null || size == 0 || width == null || width == 0 || height == null || height == 0) {
                try {
                    Binary binary = ResourceUtil.getBinaryData(contentResource);
                    if (size == null || size == 0) {
                        size = binary.getSize();
                    }
                    if (width == null || width == 0 || height == null || height == 0) {
                        try (InputStream content = ResourceUtil.getBinaryData(contentResource).getStream()) {
                            BufferedImage image = ImageIO.read(content);
                            if (image != null) {
                                width = image.getWidth();
                                height = image.getHeight();
                            }
                        } catch (IOException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    }
                } catch (RepositoryException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
            if (width != null) {
                metadata.put("width", width);
            }
            if (height != null) {
                metadata.put("height", height);
            }
            if (size != null) {
                metadata.put("size", size);
            }
            if (date != null) {
                Calendar cal = new GregorianCalendar();
                cal.setTime(date);
                metadata.put("date", cal);
            }
        }
    }

    public class VideoFileStrategy extends FileResourceStrategy {

        @Override
        protected boolean isMatchingMimeType(String mimeType) {
            return mimeType.startsWith("video/");
        }

        @Override
        public void adjustMetaProperties(ResourceResolver resolver, Resource resource) {
        }
    }

    public class ImageAssetStrategy extends FolderStrategy {

        @Override
        public boolean isMatching(Resource resource) {
            Resource assetContent = getContentResource(resource);
            return assetContent != null
                    && ResourceUtil.isResourceType(assetContent, AssetsConstants.NODE_TYPE_ASSET_CONTENT);
        }

        protected Resource getContentResource(Resource resource) {
            if (ResourceUtil.isResourceType(resource, AssetsConstants.NODE_TYPE_ASSET)) {
                resource = resource.getChild(JcrConstants.JCR_CONTENT);
            }
            return resource;
        }
    }

    public class FolderStrategy extends AbstractMetaStrategy {

        @Override
        public boolean isMatching(Resource resource) {
            return assetsConfiguration.getTreeIntermediateFilter().accept(resource);
        }

        @Override
        public void adjustMetaProperties(ResourceResolver resolver, Resource resource) {
            for (Resource child : resource.getChildren()) {
                AssetMetaPropertiesService.this.adjustMetaProperties(resolver, child);
            }
        }
    }

    public boolean adjustMetaProperties(ResourceResolver resolver, Resource resource) {
        boolean result = false;
        for (MetaStrategy strategy : strategies) {
            if (strategy.isMatching(resource)) {
                result = true;
                strategy.adjustMetaProperties(resolver, resource);
            }
        }
        return result;
    }

    @Activate
    @Modified
    protected void activate(ComponentContext context) {
        Dictionary properties = context.getProperties();
        imageMetaDataFilter = new StringFilter.WhiteList(
                PropertiesUtil.toStringArray(properties.get(IMAGE_META_DATA_FILTER)));
        strategies = new ArrayList<>();
        strategies.add(new ImageAssetStrategy());
        strategies.add(new SimpleImageStrategy());
        strategies.add(new VideoFileStrategy());
        strategies.add(new FolderStrategy());
    }
}
