package com.composum.assets.commons.widget;

import com.composum.assets.commons.AssetsConfiguration;
import com.composum.sling.core.AbstractServletBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.XSS;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FieldModel extends AbstractServletBean {

    public static final String REQUIRED = "required";
    public static final String BLANK = "blank";

    public static final String DEFAULT_NAME = "asset";

    public static final String RA_BASE = FieldModel.class.getName() + "#";
    public static final String RA_LABEL = RA_BASE + "label";
    public static final String RA_NAME = RA_BASE + "name";

    protected static final Object NULL_VALUE = "null";

    private transient ValueMap properties;
    private transient Object value;

    private transient String label;
    private transient String name;

    private transient Boolean required;
    private transient Boolean blankAllowed;

    public FieldModel() {
        super();
    }

    public FieldModel(BeanContext context, Resource resource) {
        super(context, resource);
    }

    @Nonnull
    public ValueMap getProperties() {
        if (properties == null) {
            properties = getResource().getValueMap();
        }
        return properties;
    }

    @Nullable
    public Object getValue() {
        if (value == null) {
            value = getProperties().get(getName());
            if (value == null) {
                value = NULL_VALUE;
            }
        }
        return value == NULL_VALUE ? null : value;
    }

    @Nonnull
    public String getLabel() {
        if (label == null) {
            retrieveNameAndLabel();
        }
        return label;
    }

    @Nonnull
    public String getName() {
        if (name == null) {
            retrieveNameAndLabel();
        }
        return name;
    }

    @Nonnull
    public String getCssName() {
        return getName().replace('/', '-');
    }

    protected void retrieveNameAndLabel() {
        label = context.getAttribute(RA_LABEL, String.class);
        name = context.getAttribute(RA_NAME, String.class);
        if (StringUtils.isBlank(name) || StringUtils.isBlank(label)) {
            RequestPathInfo pathInfo = getRequest().getRequestPathInfo();
            String[] selectors = pathInfo.getSelectors();
            if (selectors.length > 0) {
                if (StringUtils.isBlank(label)) {
                    label = selectors[0].replace('_', ' ');
                }
                if (StringUtils.isBlank(name)) {
                    name = selectors[selectors.length > 1 ? 1 : 0];
                    if ("_".equals(name)) {
                        name = null; // try to use suffix (to support paths) if name selector part is 'blank'
                    }
                }
            }
            if (StringUtils.isBlank(name)) {
                String suffix = XSS.filter(pathInfo.getSuffix());
                if (StringUtils.isNotBlank(suffix) && !"/".equals(suffix.trim())) {
                    name = suffix.replaceFirst("^\\.?/+", "");
                }
                if (StringUtils.isBlank(name)) {
                    name = defaultName();
                }
            }
        }
        if (StringUtils.isBlank(label)) {
            label = name;
        }
    }

    @Nonnull
    protected String defaultName() {
        return DEFAULT_NAME;
    }

    public boolean isRequired() {
        if (required == null) {
            required = retrieveFlag(REQUIRED, false);
        }
        return required;
    }

    public boolean isBlankAllowed() {
        if (blankAllowed == null) {
            blankAllowed = retrieveFlag(BLANK, false);
        }
        return blankAllowed;
    }

    protected boolean retrieveFlag(@Nonnull final String key, boolean defaultValue) {
        return retrieveFlag(key, defaultValue, 2);
    }

    protected boolean retrieveFlag(@Nonnull final String key, boolean defaultValue, int offset) {
        Boolean flag = context.getAttribute(RA_BASE + key, Boolean.class);
        if (flag == null) {
            RequestPathInfo pathInfo = getRequest().getRequestPathInfo();
            String[] selectors = pathInfo.getSelectors();
            for (int i = offset; i < selectors.length; i++) {
                if (selectors[i].equals(key)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag != null ? flag : defaultValue;
    }

    protected String retrieveFilter(@Nullable final String defaultValue) {
        return retrieveFilter(defaultValue, 2);
    }

    protected String retrieveFilter(@Nullable final String defaultValue, int offset) {
        AssetsConfiguration config = context.getService(AssetsConfiguration.class);
        String filter = null;
        RequestPathInfo pathInfo = getRequest().getRequestPathInfo();
        String[] selectors = pathInfo.getSelectors();
        for (int i = offset; i < selectors.length; i++) {
            if (config.getFileFilter(context, selectors[i]) != null) {
                filter = selectors[i];
                break;
            }
        }
        return filter != null ? filter : defaultValue;
    }
}
