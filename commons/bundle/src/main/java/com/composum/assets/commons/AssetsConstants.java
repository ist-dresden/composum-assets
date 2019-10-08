/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons;

import com.composum.sling.core.util.ResourceUtil;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class AssetsConstants {

    public static final String CPA_NAMESPACE = "cpa";
    public static final String CPA_PREFIX = CPA_NAMESPACE + ":";
    public static final String COMPOSUM_PREFIX = "composum-";
    public static final String ASSETS_PREFIX = COMPOSUM_PREFIX + "assets-";

    /** propertiy names */

    public static final String PROP_VARIATION = "variation";
    public static final String PROP_RENDITION = "rendition";

    /** mime types */

    public static final Pattern IMAGE_MIME_TYPE_PATTERN = Pattern.compile("^image/.+$");

    /** node types */

    public static final String VARIATION = "Variation";
    public static final String RENDITION = "Rendition";
    public static final String CONFIGURATION = "Configuration";

    public static final String NODE_TYPE_ASSET = CPA_PREFIX + "Asset";
    public static final String MIXIN_TYPE_ASSET_RESOURCE = CPA_PREFIX + "AssetResource";
    public static final String NODE_TYPE_ASSET_CONTENT = CPA_PREFIX + "AssetContent";
    public static final String NODE_TYPE_META_DATA = CPA_PREFIX + "MetaData";
    public static final String NODE_TYPE_ASSET_CONFIG = NODE_TYPE_ASSET + CONFIGURATION;
    public static final String NODE_TYPE_IMAGE_CONFIG = CPA_PREFIX + "Image" + CONFIGURATION;

    public static final String NODE_TYPE_VARIATION = CPA_PREFIX + VARIATION;
    public static final String NODE_TYPE_RENDITION = CPA_PREFIX + RENDITION;

    public static final String NODE_TYPE_VARIATION_CONFIG = NODE_TYPE_VARIATION + "Config";
    public static final String NODE_TYPE_RENDITION_CONFIG = NODE_TYPE_RENDITION + "Config";

    public static final List<String> ASSET_CONFIG_TYPE_SET = Arrays.asList(
            NODE_TYPE_ASSET_CONFIG, NODE_TYPE_VARIATION_CONFIG, NODE_TYPE_RENDITION_CONFIG);

    /** resource types */

    public static final String MANAGER_RESOURCE_BASE = "composum/assets/manager/image";
    public static final String RESOURCE_TYPE_VARIATION = MANAGER_RESOURCE_BASE + "/variation";
    public static final String RESOURCE_TYPE_RENDITION = MANAGER_RESOURCE_BASE + "/rendition";

    public static final String RESOURCE_TYPE_CONFIG = "composum/assets/manager/config";
    public static final String RESOURCE_TYPE_VARIATION_CONFIG = RESOURCE_TYPE_CONFIG + "/variation";
    public static final String RESOURCE_TYPE_RENDITION_CONFIG = RESOURCE_TYPE_CONFIG + "/rendition";

    /** asset structure */

    public static final String NODE_CONTENT = ResourceUtil.CONTENT_NODE;
    public static final String NODE_META = "meta";
    public static final String PATH_META = NODE_CONTENT + "/" + NODE_META;
    public static final String IMAGE_CONFIG = "imageconfig";
    public static final String PATH_IMAGE_CONFIG = NODE_CONTENT + "/" + IMAGE_CONFIG;

    /** general properties */

    public static final String PROP_CREATION_DATE = "jcr:created";
    public static final String PROP_LAST_MODIFIED = ResourceUtil.PROP_LAST_MODIFIED;
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /*** service users */

    public static final String ASSETS_SERVICE_USER = "composum-assets-service";
}
