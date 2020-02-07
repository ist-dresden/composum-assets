/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.config;

import com.composum.sling.core.InheritedValues;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.util.PropertyUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ConfigHandle {

    public static final String EXTENSION = "extension";
    public static final String DISABLED = "disabled";

    public static final String DEFAULT = "default";
    public static final String ORIGINAL = "original";

    public static final String CATEGORY = "category";

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

    protected transient List<ResourceHandle> resourceCascade;

    private transient Boolean defaultConfig;
    private transient Boolean originalConfig;
    private transient List<String> categorySet;
    private transient List<String> nonDefaultCategory;

    private transient ValueMap inheritedMap;
    private transient ValueMap propertyMap;
    private transient ValueMap values;

    public ConfigHandle(List<ResourceHandle> resourceCascade) {
        this.resourceCascade = new ArrayList<>();
        for (ResourceHandle resource : resourceCascade) {
            this.resourceCascade.add(resource.withInheritanceType(InheritedValues.Type.sameContent));
        }
    }

    public abstract AssetConfig getAssetConfig();

    public abstract RenditionConfig getOriginal();

    public ResourceHandle getResource() {
        return resourceCascade.size() > 0 ? resourceCascade.get(0) : null;
    }

    public String getName() {
        return getResource().getName();
    }

    public String getTitle() {
        return getValues().get(ResourceUtil.JCR_TITLE, String.class);
    }

    public String getDescription() {
        return getValues().get(ResourceUtil.JCR_DESCRIPTION, String.class);
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
        return getValues().get(name, type);
    }

    public ValueMap getValues() {
        if (values == null) {
            Resource resource = getResource();
            values = resource != null ? resource.getValueMap() : new ValueMapDecorator(Collections.emptyMap());
        }
        return values;
    }

    // generic property access via generic Map for direct use in templates

    public abstract class GenericMap extends HashMap<String, Object> implements ValueMap {

        public static final String UNDEFINED = "<undefined>";

        @Override
        @Nullable
        public Object get(@Nonnull final Object key) {
            return get((String) key, Object.class);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T get(@NotNull String name, @NotNull Class<T> type) {
            Object value = super.get(name);
            if (value == null) {
                value = getValue(name, type);
                super.put(name, value != null ? value : UNDEFINED);
            }
            return value != UNDEFINED ? (T) value : null;
        }

        @SuppressWarnings("unchecked")
        @Override
        @Nonnull
        public <Type> Type get(@NotNull String name, @Nonnull Type defaultValue) {
            Type value = (Type) get(name, defaultValue.getClass());
            return value != null ? value : defaultValue;
        }

        protected abstract <T> T getValue(String key, @Nonnull Class<T> type);
    }

    public class GenericProperty extends GenericMap {

        @Override
        @Nullable
        public <T> T getValue(String key, @Nonnull Class<T> type) {
            return getProperty(key, type);
        }
    }

    public class GenericInherited extends GenericMap {

        @Override
        @Nullable
        public <T> T getValue(String key, @Nonnull Class<T> type) {
            return getInherited(key, type);
        }
    }

    @Nonnull
    public ValueMap getProperty() {
        if (propertyMap == null) {
            propertyMap = new GenericProperty();
        }
        return propertyMap;
    }

    @Nonnull
    public ValueMap getInherited() {
        if (inheritedMap == null) {
            inheritedMap = new GenericInherited();
        }
        return inheritedMap;
    }

    //

    public abstract String getConfigType();

    public boolean isExtension() {
        Boolean extension = getExtension();
        return extension != null && extension;
    }

    public Boolean getExtension() {
        return null;
    }

    public boolean isDefaultConfig() {
        if (defaultConfig == null) {
            defaultConfig = getCategory().contains(ConfigHandle.DEFAULT);
        }
        return defaultConfig;
    }

    public boolean isOriginalConfig() {
        if (originalConfig == null) {
            originalConfig = getCategory().contains(ConfigHandle.ORIGINAL);
        }
        return originalConfig;
    }

    public List<String> getCategory() {
        if (categorySet == null) {
            categorySet = Arrays.asList(getResource().getProperty(CATEGORY, new String[0]));
        }
        return categorySet;
    }

    public String getNonDefaultCategory() {
        if (nonDefaultCategory == null) {
            nonDefaultCategory = new ArrayList<>();
            for (String category : getCategory()) {
                if (!ConfigHandle.DEFAULT.equals(category)) {
                    nonDefaultCategory.add(category);
                }
            }
        }
        return StringUtils.join(nonDefaultCategory, ",");
    }

    public String getCategoryString() {
        return StringUtils.join(getCategory(), ",");
    }

    public Map<String, List<ResourceHandle>> getChildren(String type, boolean cumulated) {
        Map<String, List<ResourceHandle>> result = new LinkedHashMap<>();
        for (ResourceHandle resource : resourceCascade) {
            for (ResourceHandle child : resource.getChildrenByType(type)) {
                String name = child.getName();
                List<ResourceHandle> cascade = result.computeIfAbsent(name, k -> new ArrayList<>());
                cascade.add(child);
            }
            if (!cumulated) {
                break; // use the deepest resource (topmost in the cascade) only
            }
        }
        return result;
    }

    public List<ResourceHandle> findCascadeByCategoryOrName(boolean fallbackToDefault, String type, String... key) {
        List<ResourceHandle> result = null;
        for (int i = 0; (result == null || result.size() == 0) && i < key.length; i++) {
            result = findCascadeByCategoryOrName(fallbackToDefault, type, key[i]);
        }
        return result;
    }

    public List<ResourceHandle> findCascadeByCategoryOrName(boolean fallbackToDefault, String type, String key) {
        List<ResourceHandle> result = new ArrayList<>();
        for (ResourceHandle resource : resourceCascade) {
            boolean found = false;
            ResourceHandle byName = null;
            ResourceHandle byDefault = null;
            for (ResourceHandle child : resource.getChildrenByType(type)) {
                String[] categorySet = child.getProperty(CATEGORY, new String[0]);
                for (String category : categorySet) {
                    if (category.equals(key)) {
                        result.add(child);
                        found = true;
                        break;
                    } else if (fallbackToDefault && DEFAULT.equals(category)) {
                        byDefault = child;
                    }
                }
                if (child.getName().equals(key)) {
                    byName = child;
                }
            }
            if (!found) {
                if (byName != null) {
                    result.add(byName);
                } else if (byDefault != null) {
                    result.add(byDefault);
                }
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
            if (!child.isValid() || !child.isOfType(type)) {
                // if the child not exists (possible by a partially overlay)
                // use a synthetic resource to support property inheritance
                child = ResourceHandle.use(new SyntheticResource(
                        resource.getResourceResolver(), resource.getPath() + "/" + path, type));
            }
            result.add(child);
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
