/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons;

import com.composum.sling.core.util.ResourceUtil;
import org.apache.jackrabbit.JcrConstants;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class AssetsConstants {

    public static final String CPA_NAMESPACE = "cpa";
    public static final String CPA_PREFIX = CPA_NAMESPACE + ":";
    public static final String COMPOSUM_PREFIX = "composum-";
    public static final String ASSETS_PREFIX = COMPOSUM_PREFIX + "assets-";

    // property names

    public static final String VARIATION = "variation";
    public static final String RENDITION = "rendition";

    public static final String THUMBNAIL = "thumbnail";
    public static final String ORIGINAL = "original";

    /**
     * Property of a transient rendition where the time of the last rendering is saved - for
     * performance reasons this is only updated once a week (configurable).
     */
    public static final String PROP_LAST_RENDERED = "cpa:lastRendered";

    /**
     * Property on a transient rendtion that contains the original path of the asset.
     */
    public static final String PROP_ASSETPATH = "cpa:assetPath";

    /**
     * Property on a transient rendition that contains the name of the variation.
     */
    public static final String PROP_VARIATIONNAME = "cpa:assetVariation";

    /// mime types

    public static final Pattern IMAGE_MIME_TYPE_PATTERN = Pattern.compile("^image/.+$");

    /// node types

    public static final String _VARIATION = "Variation";
    public static final String _RENDITION = "Rendition";
    public static final String CONFIGURATION = "Configuration";

    /** Node type for an configuration {@value #NODE_TYPE_ASSET} */
    public static final String NODE_TYPE_ASSET = CPA_PREFIX + "Asset";
    /** Node type for an configuration {@value #NODE_TYPE_ASSET_CONTENT} */
    public static final String NODE_TYPE_ASSET_CONTENT = CPA_PREFIX + "AssetContent";
    /** Node type for an configuration {@value #NODE_TYPE_ASSET_CONFIG} */
    public static final String NODE_TYPE_ASSET_CONFIG = NODE_TYPE_ASSET + CONFIGURATION;
    /** Node type for an image asset {@value #NODE_TYPE_IMAGE_CONFIG}. */
    public static final String NODE_TYPE_IMAGE_CONFIG = CPA_PREFIX + "Image" + CONFIGURATION;
    /** Mixin type for a folder conmtent resource */
    public static final String MIX_ASSET_FOLDER = CPA_PREFIX + "assetFolderContent";

    /** Node type for a variation {@value #NODE_TYPE_VARIATION}. */
    public static final String NODE_TYPE_VARIATION = CPA_PREFIX + _VARIATION;
    /** Node type for a rendition {@value #NODE_TYPE_RENDITION}. */
    public static final String NODE_TYPE_RENDITION = CPA_PREFIX + _RENDITION;

    /** Node type for an configuration for a variation {@value #NODE_TYPE_VARIATION_CONFIG}. */
    public static final String NODE_TYPE_VARIATION_CONFIG = NODE_TYPE_VARIATION + "Config";
    /** Node type for an configuration for a variation {@value #NODE_TYPE_RENDITION_CONFIG}. */
    public static final String NODE_TYPE_RENDITION_CONFIG = NODE_TYPE_RENDITION + "Config";

    public static final List<String> ASSET_CONFIG_TYPE_SET = Arrays.asList(
            NODE_TYPE_ASSET_CONFIG, NODE_TYPE_VARIATION_CONFIG, NODE_TYPE_RENDITION_CONFIG);

    /**
     * resource types
     */

    public static final String RESOURCE_TYPE_ASSET = "composum/assets/asset";
    public static final String RESOURCE_TYPE_META = RESOURCE_TYPE_ASSET + "/meta";
    public static final String RESOURCE_TYPE_VARIATION = RESOURCE_TYPE_ASSET + "/variation";
    public static final String RESOURCE_TYPE_RENDITION = RESOURCE_TYPE_ASSET + "/rendition";

    public static final String RESOURCE_TYPE_IMAGE = "composum/assets/image";
    public static final String RESOURCE_TYPE_VIDEO = "composum/assets/video";

    public static final String RESOURCE_TYPE_CONFIG = "composum/assets/config";
    public static final String RESOURCE_TYPE_VARIATION_CONFIG = RESOURCE_TYPE_CONFIG + "/variation";
    public static final String RESOURCE_TYPE_RENDITION_CONFIG = RESOURCE_TYPE_CONFIG + "/rendition";

    /**
     * asset structure
     */

    public static final String NODE_META = "meta";
    public static final String PATH_META = JcrConstants.JCR_CONTENT + "/" + NODE_META;
    public static final String ASSET_CONFIG = "assetconfig";
    public static final String PATH_ASSET_CONFIG = JcrConstants.JCR_CONTENT + "/" + ASSET_CONFIG;
    public static final String IMAGE_CONFIG = "imageconfig";
    public static final String PATH_IMAGE_CONFIG = JcrConstants.JCR_CONTENT + "/" + IMAGE_CONFIG;

    /**
     * Path below which transient renderings are stored.
     */
    public static final String PATH_TRANSIENTS = "/var/composum/assets";

    /**
     * Path particle for transient paths when the configuration is a versionable but we render for the workspace, as
     * opposed to a checked in version. If availale, the last modification time is appended.
     */
    public static final String NODE_WORKSPACECONFIGURED = "workspace";

    /**
     * general properties
     */

    public static final String PROP_CREATION_DATE = "jcr:created";
    public static final String PROP_LAST_MODIFIED = ResourceUtil.PROP_LAST_MODIFIED;
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /*** service users */

    public static final String ASSETS_SERVICE_USER = "composum-assets-service";
}
