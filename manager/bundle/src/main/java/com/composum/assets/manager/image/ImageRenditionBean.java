package com.composum.assets.manager.image;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.manager.config.RenditionConfigBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.LinkUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.List;

public class ImageRenditionBean extends AbstractImageBean<RenditionConfig> {

    protected RenditionConfig config;

    private transient String imageUrl;
    private transient List<RenditionConfigBean.RenditionValue> values;

    public ImageRenditionBean(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public ImageRenditionBean(BeanContext context) {
        super(context);
    }

    public ImageRenditionBean() {
        super();
    }

    @Override
    public void initialize(BeanContext context, Resource resource) {
        super.initialize(context, resource);
        AssetConfig assetConfig = asset.getConfig();
        VariationConfig variationConfig = assetConfig.findVariation(resource.getParent().getName());
        config = variationConfig.findRendition(resource.getName());
    }

    @Override
    public RenditionConfig getConfig() {
        return config;
    }

    public String getTabCssClass() {
        return StringUtils.isNotBlank(getRequest().getSelectors(
                new StringFilter.WhiteList(AssetsConstants.PROP_VARIATION, AssetsConstants.PROP_RENDITION)))
                ? "in" : "";
    }

    public String getImageUri() {
        String uri = "";
        ImageAsset image = getAsset();
        if (image != null) {
            uri = image.getImageUri(config.getVariation().getName(), config.getName());
        }
        return uri;
    }

    public String getImageUrl() {
        String url = getImageUri();
        return LinkUtil.getUrl(getRequest(), url);
    }

    public List<RenditionConfigBean.RenditionValue> getValues() {
        if (values == null) {
            values = RenditionConfigBean.getValues(config);
        }
        return values;
    }
}
