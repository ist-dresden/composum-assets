package com.composum.assets.manager.config;

import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.sling.core.AbstractSlingBean;
import com.composum.sling.core.BeanContext;
import com.google.gson.stream.JsonWriter;
import org.apache.sling.api.resource.Resource;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractConfigBean extends AbstractSlingBean implements ConfigBean {

    private transient ConfigSet configSet;
    protected ConfigHandle config;

    protected AbstractConfigBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    protected AbstractConfigBean(BeanContext context) {
        super(context);
    }

    protected AbstractConfigBean() {
        super();
    }

    public ConfigSet getConfigSet() {
        if (configSet == null) {
            configSet = new ConfigSet(getConfig());
        }
        return configSet;
    }

    public VariationConfig getConfig(String variation) {
        ConfigHandle config = getConfig();
        return ((AssetConfig) config).findVariation(variation);
    }

    public RenditionConfig getConfig(String variation, String rendition) {
        VariationConfig config = getConfig(variation);
        return config.findRendition(rendition);
    }

    public List<VariationConfig> getVariationList() {
        return ((AssetConfig) config).getVariationList(true);
    }

    public void toJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        fillJsonObject(writer);
        writer.endObject();
    }

    public void fillJsonObject(JsonWriter writer) throws IOException {
        ConfigHandle cfHandle = getConfig();
        ConfigSet cfSet = getConfigSet();
        String path = getPath();
        writer.name("path").value(path);
        writer.name("type").value(getType());
        Boolean isExtension = cfHandle.getExtension();
        if (isExtension != null) {
            writer.name(ConfigHandle.EXTENSION).value(isExtension);
        }
        writer.name(ConfigHandle.CATEGORY).beginArray();
        for (String category: cfHandle.getCategory()) {
            writer.value(category);
        }
        writer.endArray();
        for (Map.Entry<String, LinkedHashMap<String, ConfigHandle.ConfigProperty>> aspect : cfSet.set.entrySet()) {
            writer.name(aspect.getKey()).beginObject();
            for (Map.Entry<String, ConfigHandle.ConfigProperty> entry : aspect.getValue().entrySet()) {
                ConfigHandle.ConfigProperty property = entry.getValue();
                if (property != null && property.value != null) {
                    writer.name(entry.getKey()).beginObject();
                    writer.name("path").value(property.path);
                    writer.name("value");
                    if (property.value instanceof String) {
                        writer.value((String) property.value);
                    } else if (property.value instanceof Float) {
                        writer.value((Float) property.value);
                    } else if (property.value instanceof Boolean) {
                        writer.value((Boolean) property.value);
                    } else if (property.value instanceof Number) {
                        writer.value((Number) property.value);
                    }
                    writer.name("inherited").value(property.inherited);
                    writer.endObject();
                }
            }
            writer.endObject();
        }
    }
}
