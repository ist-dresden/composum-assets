package com.composum.assets.commons.widget;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class FilterModel extends FieldModel {

    private static final Logger LOG = LoggerFactory.getLogger(FilterModel.class);

    public FilterModel(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public FilterModel() {
        super();
    }

    public Collection<AssetsConfiguration.ConfigurableFilter> getFilterSet() {
        AssetsConfiguration config = context.getService(AssetsConfiguration.class);
        return config.getNodeFilters();
    }

    public String getDropdownType() {
        return retrieveFlag("dropup", false, 0) ? "dropup" : "dropdown";
    }

    public String getAlignment() {
        return retrieveFlag("left", false, 0) ? "left" : "right";
    }
}
