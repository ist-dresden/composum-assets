package com.composum.assets.commons.model;

import com.composum.assets.commons.handle.ImageAsset;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * the abstraction of a image rendering component for the image template implementations
 */
public interface AdaptiveImageComponent {

    SlingHttpServletRequest getRequest();

    ImageAsset getAsset();

    String getTitle();

    String getAltText();
}
