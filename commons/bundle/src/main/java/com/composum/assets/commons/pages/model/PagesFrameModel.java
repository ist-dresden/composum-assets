/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.pages.model;

import com.composum.sling.core.AbstractSlingBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

public class PagesFrameModel extends AbstractSlingBean {

    private transient ResourceHandle resource;

    public PagesFrameModel(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public PagesFrameModel() {
    }

    public ResourceHandle getResource() {
        if (resource == null) {
            String path = getTargetPath(context);
            resource = ResourceHandle.use(context.getResolver().getResource(path));
        }
        return resource;
    }

    /**
     * uses the requests suffix to determin the target resource of the Pages edit frame
     */
    public String getTargetPath(BeanContext context) {
        SlingHttpServletRequest request = context.getRequest();
        String targetPath = request.getRequestPathInfo().getSuffix();
        if (StringUtils.isBlank(targetPath)) {
            targetPath = "/";
        }
        return targetPath;
    }
}
