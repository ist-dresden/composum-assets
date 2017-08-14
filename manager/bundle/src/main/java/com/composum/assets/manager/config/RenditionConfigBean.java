package com.composum.assets.manager.config;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.config.aspect.Example;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.util.AdaptiveUtil;
import com.composum.assets.commons.util.AssetConfigUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.LinkUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.ArrayList;
import java.util.List;

public class RenditionConfigBean extends AbstractConfigBean {

    public static final String EXAMPLE_IMAGE = "/libs/composum/assets/manager/example/image.jpg";

    public static class RenditionValue {

        private String key;
        private Object found;

        public RenditionValue(String key, Object found) {
            this.key = key;
            this.found = found;
        }

        public String getKey() {
            return key;
        }

        public String getLabel() {
            return getKey();
        }

        public Object getFound() {
            return found;
        }
    }

    private transient ImageAsset imageAsset;

    private transient List<RenditionValue> values;

    protected RenditionConfig config;

    public RenditionConfigBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public RenditionConfigBean(BeanContext context) {
        super(context);
    }

    public RenditionConfigBean() {
        super();
    }

    public void initialize(BeanContext context, Resource resource) {
        super.initialize(context, resource);
        Resource variationConfigRes = resource.getParent();
        Resource assetConfigRes = variationConfigRes.getParent();
        List<ResourceHandle> assetConfigCascade = AssetConfigUtil.assetConfigCascade(ResourceHandle.use(assetConfigRes));
        List<ResourceHandle> variationConfigCascade = new ArrayList<>(assetConfigCascade);
        variationConfigCascade.add(0, ResourceHandle.use(variationConfigRes));
        List<ResourceHandle> reditionConfigCascade = new ArrayList<>(variationConfigCascade);
        reditionConfigCascade.add(0, ResourceHandle.use(resource));
        config = new RenditionConfig(
                new VariationConfig(
                        new AssetConfig(assetConfigCascade),
                        variationConfigCascade),
                reditionConfigCascade);
    }

    @Override
    public ConfigHandle getConfig() {
        return config;
    }

    public String getTabCssClass() {
        return StringUtils.isNotBlank(getRequest().getSelectors(
                new StringFilter.WhiteList(AssetsConstants.PROP_VARIATION, AssetsConstants.PROP_RENDITION)))
                ? "in" : "";
    }

    public ImageAsset getImage() {
        if (imageAsset == null) {
            Example image = config.getExampleImage();
            if (image.isValid()) {
                ResourceResolver resolver = getResolver();
                Resource imageResource = resolver.getResource(image.path);
                if (imageResource != null) {
                    imageAsset = new ImageAsset(context, imageResource);
                }
            }
        }
        return imageAsset;
    }

    public String getImageUri() {
        String uri = "";
        ImageAsset image = getImage();
        if (image != null) {
            uri = AdaptiveUtil.getImageUri(image, config.getVariation().getName(), config.getName());
        }
        return uri;
    }

    public String getImageUrl() {
        String url = getImageUri();
        url = LinkUtil.getUrl(getRequest(), url);
        return url;
    }

    public List<RenditionValue> getValues() {
        if (values == null) {
            values = getValues(config);
        }
        return values;
    }

    public static List<RenditionValue> getValues(RenditionConfig config) {

        List<RenditionValue> values = new ArrayList<>();
        values.add(new RenditionValue(ConfigHandle.WIDTH, config.getSize().width));
        values.add(new RenditionValue(ConfigHandle.HEIGHT, config.getSize().height));
        values.add(new RenditionValue(ConfigHandle.ASPECT_RATIO, config.getSize().aspectRatio));
        if (!config.getCrop().isDefault()) {
            values.add(new RenditionValue(ConfigHandle.CROP_HORIZONTAL, config.getCrop().horizontal));
            values.add(new RenditionValue(ConfigHandle.CROP_VERTICAL, config.getCrop().vertical));
            values.add(new RenditionValue(ConfigHandle.CROP_SCALE, config.getCrop().scale));
        }
        if (config.getWatermark().isValid()) {
            values.add(new RenditionValue(ConfigHandle.WATERMARK_TEXT, config.getWatermark().text));
            values.add(new RenditionValue(ConfigHandle.WATERMARK_FONT_FAMILY, config.getWatermark().font.family));
            values.add(new RenditionValue(ConfigHandle.WATERMARK_FONT_BOLD, config.getWatermark().font.bold));
            values.add(new RenditionValue(ConfigHandle.WATERMARK_FONT_ITALIC, config.getWatermark().font.italic));
            values.add(new RenditionValue(ConfigHandle.WATERMARK_FONT_SIZE, config.getWatermark().font.size));
            values.add(new RenditionValue(ConfigHandle.WATERMARK_POS_HORIZONTAL, config.getWatermark().horizontal));
            values.add(new RenditionValue(ConfigHandle.WATERMARK_POS_VERTICAL, config.getWatermark().vertical));
            values.add(new RenditionValue(ConfigHandle.WATERMARK_COLOR, config.getWatermark().getColorCode()));
            values.add(new RenditionValue(ConfigHandle.WATERMARK_ALPHA, config.getWatermark().alpha));
        }
        if (config.getBlur().isValid()) {
            values.add(new RenditionValue(ConfigHandle.TRANSFORMATION_BLUR_FACTOR, config.getBlur().getFactor()));
        }
        values.add(new RenditionValue(ConfigHandle.FILE_QUALITY, config.getFile().quality));

        return values;
    }
}
