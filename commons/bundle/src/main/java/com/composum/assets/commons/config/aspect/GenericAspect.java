package com.composum.assets.commons.config.aspect;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class GenericAspect extends HashMap<String, Object> {

    public GenericAspect() {
    }

    public GenericAspect(Map<String, Object> template) {
        super(template);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        T value = (T) get(key);
        return value != null ? value : defaultValue;
    }
}
