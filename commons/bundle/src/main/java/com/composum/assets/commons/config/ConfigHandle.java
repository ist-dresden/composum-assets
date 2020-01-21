/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.config;

import com.composum.sling.core.InheritedValues;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.util.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.SyntheticResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ConfigHandle {

    public static final String EXTENSION = "extension";

    public static final String DEFAULT = "default";
    public static final String ORIGINAL = "original";

    public static final String CATEGORIES = "categories";

    public static final String FILE = "file";
    public static final String FILE_QUALITY = FILE + "_jpg_quality";

    public static final String SIZE = "size";
    public static final String WIDTH = SIZE + "_width";
    public static final String HEIGHT = SIZE + "_height";
    public static final String ASPECT_RATIO = SIZE + "_aspectRatio";

    public static final String CROP = "crop";
    public static final String CROP_VERTICAL = CROP + "_vertical";
    public static final String CROP_HORIZONTAL = CROP + "_horizontal";
    public static final String CROP_SCALE = CROP + "_scale";

    public static final String TRANSFORMATION = "transformation";
    public static final String TRANSFORMATION_BLUR = TRANSFORMATION + "_blur";
    public static final String TRANSFORMATION_BLUR_FACTOR = TRANSFORMATION_BLUR + "_factor";

    public static final String WATERMARK = "watermark";
    public static final String WATERMARK_TEXT = WATERMARK + "_text";
    public static final String WATERMARK_FONT = WATERMARK + "_font";
    public static final String WATERMARK_FONT_FAMILY = WATERMARK_FONT + "_family";
    public static final String WATERMARK_FONT_BOLD = WATERMARK_FONT + "_bold";
    public static final String WATERMARK_FONT_ITALIC = WATERMARK_FONT + "_italic";
    public static final String WATERMARK_FONT_SIZE = WATERMARK_FONT + "_size";
    public static final String WATERMARK_POS_VERTICAL = WATERMARK + "_vertical";
    public static final String WATERMARK_POS_HORIZONTAL = WATERMARK + "_horizontal";
    public static final String WATERMARK_COLOR = WATERMARK + "_color";
    public static final String WATERMARK_ALPHA = WATERMARK + "_alpha";

    public static final String EXAMPLE = "example";
    public static final String EXAMPLE_IMAGE = EXAMPLE + "_image";
    public static final String EXAMPLE_IMAGE_PATH = EXAMPLE_IMAGE + "_path";

    protected List<ResourceHandle> resourceCascade;

    private transient Boolean defaultConfig;
    private transient List<String> categories;
    private transient List<String> nonDefaultCategories;

    private transient Map<String, Object> inheritedMap;
    private transient Map<String, Object> propertyMap;

    public ConfigHandle(List<ResourceHandle> resourceCascade) {
        this.resourceCascade = resourceCascade;
        for (ResourceHandle resource : resourceCascade) {
            resource.setUseNodeInheritance(true);
        }
    }

    public abstract AssetConfig getAssetConfig();

    public abstract RenditionConfig getOriginal();

    public ResourceHandle getResource() {
        for (int i = 0; i < resourceCascade.size(); i++) {
            ResourceHandle resource = resourceCascade.get(i);
            if (!resource.isSynthetic()) {
                return resource;
            }
        }
        return resourceCascade.get(0);
    }

    public String getName() {
        return getResource().getName();
    }

    public String getPath() {
        return getResource().getPath();
    }

    public <T> T getInherited(String name, T defaultValue) {
        Class<T> type = PropertyUtil.getType(defaultValue);
        T value = getInherited(name, type);
        return value != null ? value : defaultValue;
    }

    public <T> T getInherited(String name, Class<T> type) {
        T value = null;
        for (ResourceHandle resource : resourceCascade) {
            value = resource.getInherited(name, type);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    public <T> T getProperty(String name, T defaultValue) {
        Class<T> type = PropertyUtil.getType(defaultValue);
        T value = getProperty(name, type);
        return value != null ? value : defaultValue;
    }

    public <T> T getProperty(String name, Class<T> type) {
        T value = null;
        for (ResourceHandle resource : resourceCascade) {
            value = resource.getProperty(name, type);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    // generic property access via generic Map for direct use in templates

    public abstract class GenericMap extends HashMap<String, Object> {

        public static final String UNDEFINED = "<undefined>";

        @Override
        @Nullable
        public Object get(@Nonnull final Object key) {
            Object value = super.get(key);
            if (value == null) {
                value = getValue((String) key);
                super.put((String) key, value != null ? value : UNDEFINED);
            }
            return value != UNDEFINED ? value : null;
        }

        protected abstract Object getValue(String key);
    }

    public class GenericProperty extends GenericMap {

        @Override
        @Nullable
        public Object getValue(@Nonnull final String key) {
            return getProperty(key, Object.class);
        }

    }

    public class GenericInherited extends GenericMap {

        @Override
        @Nullable
        public Object getValue(@Nonnull final String key) {
            return getInherited(key, Object.class);
        }

    }

    @Nonnull
    public Map<String, Object> getProperty() {
        if (propertyMap == null) {
            propertyMap = new GenericProperty();
        }
        return propertyMap;
    }

    @Nonnull
    public Map<String, Object> getInherited() {
        if (inheritedMap == null) {
            inheritedMap = new GenericInherited();
        }
        return inheritedMap;
    }

    //

    public Boolean isExtension() {
        return null;
    }

    public boolean isDefaultConfig() {
        if (defaultConfig == null) {
            defaultConfig = getCategories().contains(ConfigHandle.DEFAULT);
        }
        return defaultConfig;
    }

    public List<String> getCategories() {
        if (categories == null) {
            categories = Arrays.asList(getResource().getProperty(CATEGORIES, new String[0]));
        }
        return categories;
    }

    public String getNonDefaultCategories() {
        if (nonDefaultCategories == null) {
            nonDefaultCategories = new ArrayList<>();
            for (String category : getCategories()) {
                if (!ConfigHandle.DEFAULT.equals(category)) {
                    nonDefaultCategories.add(category);
                }
            }
        }
        return StringUtils.join(nonDefaultCategories, ",");
    }

    public String getCategoriesString() {
        return StringUtils.join(getCategories(), ",");
    }

    public Map<String, List<ResourceHandle>> getChildren(String type) {
        Map<String, List<ResourceHandle>> result = new LinkedHashMap<>();
        for (ResourceHandle resource : resourceCascade) {
            for (ResourceHandle child : resource.getChildrenByType(type)) {
                String name = child.getName();
                List<ResourceHandle> cascade = result.get(name);
                if (cascade == null) {
                    cascade = new ArrayList<>();
                    result.put(name, cascade);
                }
                cascade.add(child);
            }
        }
        return result;
    }

    public List<ResourceHandle> findCascadeByCategoryOrName(String type, String... key) {
        List<ResourceHandle> result = null;
        for (int i = 0; (result == null || result.size() == 0) && i < key.length; i++) {
            result = findCascadeByCategoryOrName(type, key[i]);
        }
        return result;
    }

    public List<ResourceHandle> findCascadeByCategoryOrName(String type, String key) {
        List<ResourceHandle> result = new ArrayList<>();
        for (ResourceHandle resource : resourceCascade) {
            boolean found = false;
            ResourceHandle byName = null;
            for (ResourceHandle child : resource.getChildrenByType(type)) {
                String[] categories = child.getProperty(CATEGORIES, new String[0]);
                for (String category : categories) {
                    if (category.equals(key)) {
                        result.add(child);
                        found = true;
                        break;
                    }
                }
                if (child.getName().equals(key)) {
                    byName = child;
                }
            }
            if (!found && byName != null) {
                result.add(byName);
            }
        }
        return result;
    }

    /**
     * Retrieve the configuration resource cascade for a path (variation or rendition);
     * used by an asset handle to determine the appropriate asset configuration cascade.
     */
    public List<ResourceHandle> getCascadeForPath(String type, String path) {
        List<ResourceHandle> result = new ArrayList<>();
        for (ResourceHandle resource : resourceCascade) {
            ResourceHandle child = ResourceHandle.use(resource.getChild(path));
            if (child.isValid() && child.isOfType(type)) {
                result.add(child);
            } else {
                // if the child not exists (possible by a partially overlay)
                // use a synthetic resource to support property inheritance
                child = ResourceHandle.use(new SyntheticResource(
                        resource.getResourceResolver(), resource.getPath() + "/" + path, type));
                result.add(child);
            }
        }
        return result;
    }

    //

    public static class ConfigProperty {

        public final String path;
        public final String key;
        public final Object value;
        public final boolean inherited;

        public ConfigProperty(Resource origin, String key, Object value, boolean inherited) {
            this(origin.getPath(), key, value, inherited);
        }

        public ConfigProperty(String path, String key, Object value, boolean inherited) {
            this.path = path;
            this.key = key;
            this.value = value;
            this.inherited = inherited;
        }
    }

    public ConfigProperty getOrigin(String name, Class<?> type) {
        Object value;
        InheritedValues.HierarchyScanResult found;
        for (int i = 0; i < resourceCascade.size(); i++) {
            ResourceHandle resource = resourceCascade.get(i);
            value = resource.getProperty(name, type);
            if (value != null) {
                return new ConfigProperty(resource.getPath(), name, value, i > 0);
            }
            found = resource.getInheritedValues().findOriginAndValue(name, type);
            if (found != null) {
                return new ConfigProperty(found.origin, name, found.value, true);
            }
        }
        return null;
    }
}
