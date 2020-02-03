/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.model;

import com.composum.assets.commons.handle.AssetMetaData;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.util.ImageUtil;
import com.composum.sling.core.AbstractSlingBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.InheritedValues;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public class AdaptiveImage extends AbstractSlingBean implements AdaptiveImageComponent {

    public static final String DEFAULT_TEMPLATE = "picture";
    public static final String DEFAULT_TEMPLATE_PATH = "composum/assets/commons/templates/image/picture.html";

    public static final String PROP_ASSET = "asset";
    public static final String PROP_TEMPLATE = "template";
    public static final String SITE_TEMPLATES = "templates/image/";

    private transient String imageUri;
    private transient String title;
    private transient String altText;

    private transient ImageAsset asset;

    private transient AdaptiveTagTemplate template;
    private transient InheritedValues configuration;

    public AdaptiveImage(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public AdaptiveImage() {
    }

    protected AdaptiveTagTemplate getTemplate() {
        if (template == null) {
            ResourceResolver resolver = getResolver();
            String templatePath = getProperty(PROP_TEMPLATE, DEFAULT_TEMPLATE);
            if (!templatePath.contains("/")) { // the 'path' is a key - ask configuration
                templatePath = getConfiguration().get(SITE_TEMPLATES + templatePath, DEFAULT_TEMPLATE_PATH);
            }
            Resource templateRes = null;
            if (!templatePath.startsWith("/")) { // relative path - use resolver search paths
                for (String root : resolver.getSearchPath()) {
                    if ((templateRes = resolver.getResource(root + templatePath)) != null) {
                        break;
                    }
                }
            } else {
                templateRes = resolver.getResource(templatePath);
            }
            template = new AdaptiveTagTemplate(templateRes);
        }
        return template;
    }

    /**
     * returns the HTML code for the rendered image based on the components template
     */
    public String getImageTag() {
        return getTemplate().buildTag(this);
    }

    /**
     * Returns the URL of an original of this asset.
     */
    public String getImageUrl() {
        return getAsset().getImageUrl();
    }

    /**
     * returns the value of the optional 'title' or a 'jcr:title' property;
     * if this value is empty the 'title' value from the meta data of the asset is used
     */
    @Override
    public String getTitle() {
        if (title == null) {
            title = getProperty("title", "");
            if (StringUtils.isBlank(title)) {
                title = getProperty("jcr:title", "");
                if (StringUtils.isBlank(title)) {
                    title = getMetaData().getTitle();
                }
            }
        }
        return title;
    }

    /**
     * returns the value of the optional 'altText' property;
     * if this value is empty the 'altText' value from the meta data of the asset is used
     */
    @Override
    public String getAltText() {
        if (altText == null) {
            altText = getProperty("altText", "");
            if (StringUtils.isBlank(altText)) {
                altText = getMetaData().getAltText();
            }
        }
        return altText;
    }

    /**
     * returns the referenced image asset resource
     */
    @Override
    public ImageAsset getAsset() {
        if (asset == null) {
            asset = ImageUtil.getImageAsset(context, resource, PROP_ASSET);
        }
        return asset;
    }

    /**
     * returns the meta data of the referenced asset
     */
    public AssetMetaData getMetaData() {
        return getAsset().getMetaData();
    }

    protected InheritedValues getConfiguration() {
        if (configuration == null) {
            configuration = new InheritedValues(getResource(), InheritedValues.Type.contentBased);
        }
        return configuration;
    }
}
