package com.composum.assets.commons.widget;

import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class NavigatorModel extends FieldModel {

    public static final String PROP_FILTER_WIDGET = "filterWidget";
    public static final String DROPDOWN_FILTER_FLAG = "dropdownFilter";
    public static final String SELECT_FILTER_FLAG = "selectFilter";

    private transient String filterWidget;
    private transient String filter;

    public NavigatorModel() {
        super();
    }

    public NavigatorModel(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public boolean getShowFilter() {
        return StringUtils.isNotBlank(getFilterType());
    }

    public String getFilterType() {
        if (filterWidget == null) {
            filterWidget = retrieveFlag(DROPDOWN_FILTER_FLAG, false, 0) ? "dropdown"
                    : (retrieveFlag(SELECT_FILTER_FLAG, false, 0) ? "select"
                    : getResource().getValueMap().get(PROP_FILTER_WIDGET, ""));
        }
        return filterWidget;
    }

    public String getFilter(){
        if (filter == null) {
            filter = retrieveFilter(null,0);
        }
        return filter;
    }
}
