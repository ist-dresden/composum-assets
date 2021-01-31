package com.composum.assets.commons.event;

import com.composum.assets.commons.util.AssetConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.assets.commons.config.ConfigHandle.CATEGORY;
import static com.composum.assets.commons.config.ConfigHandle.DEFAULT;
import static com.composum.assets.commons.config.ConfigHandle.ORIGINAL;

/**
 *
 */
@Component
public class AdjustConfigPostProcessor implements SlingPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AdjustConfigPostProcessor.class);

    public static final Pattern CONFIG_PATH = Pattern.compile("(/(.*)?/" + JcrConstants.JCR_CONTENT
            + "/(asset|image)config)((/[^/]+(/[^/]+(/[^/]+)?)?)?)$");

    // adjustments

    protected void removeCategory(Resource target, String value) {
        if (target != null) {
            ModifiableValueMap values = target.adaptTo(ModifiableValueMap.class);
            if (values != null) {
                List<String> category = Arrays.asList(values.get(CATEGORY, new String[0]));
                if (category.contains(value)) {
                    List<String> replacement = new ArrayList<>();
                    for (String val : category) {
                        if (!value.equals(val)) {
                            replacement.add(val);
                        }
                    }
                    values.put(CATEGORY, replacement.toArray(new String[0]));
                }
            }
        }
    }

    /**
     * if a 'marker' category is set on a change this category is removed from the siblings here
     */
    protected void adjustMarkerCategory(Resource target, String value) {
        if (target != null) {
            ValueMap values = target.getValueMap();
            List<String> category = Arrays.asList(values.get(CATEGORY, new String[0]));
            if (category.contains(value)) {
                String targetPath = target.getPath();
                for (Resource sibling : Objects.requireNonNull(target.getParent()).getChildren()) {
                    if (!targetPath.equals(sibling.getPath())) {
                        removeCategory(sibling, value);
                    }
                }
            }
        }
    }

    // 'registry' - collect changes for final adjustment

    protected class Properties extends HashSet<String> {
    }

    protected class RenditionChange extends Properties {

        public final Resource resource;

        public RenditionChange(Resource resource) {
            this.resource = resource;
        }

        public void adjustChanges() {
            if (contains(CATEGORY)) {
                adjustMarkerCategory(resource, DEFAULT);
                adjustMarkerCategory(resource, ORIGINAL);
            }
        }
    }

    protected class VariationChange extends Properties {

        public final Resource resource;
        public final Map<String, RenditionChange> renditions = new HashMap<>();

        public VariationChange(Resource resource) {
            this.resource = resource;
        }

        protected Set<String> getRendition(String name) {
            RenditionChange rendition = renditions.get(name);
            if (rendition == null) {
                renditions.put(name, rendition = new RenditionChange(resource.getChild(name)));
            }
            return rendition;
        }

        public void adjustChanges() {
            for (RenditionChange rendition : renditions.values()) {
                rendition.adjustChanges();
            }
            if (contains(CATEGORY)) {
                adjustMarkerCategory(resource, DEFAULT);
                adjustMarkerCategory(resource, ORIGINAL);
            }
        }
    }

    protected class ConfigChange extends Properties {

        public final Resource resource;
        public final Map<String, VariationChange> variations = new HashMap<>();

        public ConfigChange(Resource resource) {
            this.resource = resource;
        }

        protected VariationChange getVariation(String name) {
            VariationChange variation = variations.get(name);
            if (variation == null) {
                variations.put(name, variation = new VariationChange(resource.getChild(name)));
            }
            return variation;
        }

        public void registerModify(Matcher matcher) {
            String[] path = StringUtils.split(matcher.group(4), "/");
            if (path.length == 3) {
                getVariation(path[0]).getRendition(path[1]).add(path[2]);
            } else if (path.length == 2) {
                getVariation(path[0]).add(path[1]);
            } else if (path.length == 1) {
                add(path[0]);
            }
        }

        public void adjustChanges() {
            for (VariationChange variation : variations.values()) {
                variation.adjustChanges();
            }
        }
    }

    @Override
    public void process(SlingHttpServletRequest request, @Nonnull List<Modification> changes) {
        LOG.debug("Modified: {}", changes);
        ResourceResolver resolver = request.getResourceResolver();
        Map<String, ConfigChange> modifiedConfigs = new HashMap<>();
        for (Modification modification : changes) {
            switch (modification.getType()) {
                case MODIFY:
                    String target = modification.getSource();
                    Matcher matcher = CONFIG_PATH.matcher(target);
                    if (matcher.matches()) {
                        String configPath = matcher.group(1);
                        ConfigChange configChange = modifiedConfigs.get(configPath);
                        if (configChange == null) {
                            Resource configRes = resolver.getResource(configPath);
                            if (configRes != null && AssetConfigUtil.isAssetConfigResource(configRes)) {
                                configChange = new ConfigChange(configRes);
                                modifiedConfigs.put(configPath, configChange);
                            }
                        }
                        if (configChange != null) {
                            configChange.registerModify(matcher);
                        }
                    }
                    break;
            }
        }
        for (ConfigChange configChange : modifiedConfigs.values()) {
            configChange.adjustChanges();
        }
    }
}
