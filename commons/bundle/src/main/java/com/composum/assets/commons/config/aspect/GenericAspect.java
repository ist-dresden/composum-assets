package com.composum.assets.commons.config.aspect;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ModifiableValueMapDecorator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

/**
 *
 */
public class GenericAspect {

    protected final ValueMap values;

    public GenericAspect() {
        this.values = new ModifiableValueMapDecorator(new HashMap<>());
    }

    public GenericAspect(ValueMap values) {
        this.values = values;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(@Nonnull final String key, @Nullable final T defaultValue) {
        return defaultValue != null ? values.get(key, defaultValue) : (T) values.get(key);
    }
}
