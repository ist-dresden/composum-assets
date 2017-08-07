/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.handle;

import com.composum.assets.commons.config.ConfigHandle;
import com.composum.sling.core.AbstractSlingBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.List;

public abstract class AssetHandle<Config extends ConfigHandle> extends AbstractSlingBean {

    public static final String IMAGE_RESOURCE_TYPE = "composum/assets/image";

    public static final String VALID = "valid";

    protected AssetHandle() {
    }

    protected AssetHandle(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public abstract Config getConfig();

    public abstract ConfigHandle getChildConfig(Resource asset);

    public ResourceHandle getResource() {
        return resource;
    }

    public boolean isValid() {
        return resource != null && resource.isValid();
    }

    public String getName() {
        return resource.getName();
    }

    public String getPath() {
        return resource.getPath();
    }

    public Resource findChildByCategoryOrName(String type, String... key) {
        Resource result = null;
        for (int i = 0; result == null && i < key.length; i++) {
            result = retrieveChildByCategoryOrName(type, key[i]);
        }
        return result;
    }

    public Resource retrieveChildByCategoryOrName(String type, String key) {
        Resource byName = null;
        for (ResourceHandle child : resource.getChildrenByType(type)) {
            ConfigHandle childConfig = getChildConfig(child);
            if (childConfig != null) {
                List<String> categories = childConfig.getCategories();
                for (String category : categories) {
                    if (category.equals(key)) {
                        return child;
                    }
                }
            }
            String[] categories = child.adaptTo(ValueMap.class)
                    .get(ConfigHandle.CATEGORIES, new String[0]);
            for (String category : categories) {
                if (category.equals(key)) {
                    return child;
                }
            }
            if (child.getName().equals(key)) {
                byName = child;
            }
        }
        return byName;
    }
}
