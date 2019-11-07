/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.image;

import com.composum.assets.commons.service.AdaptiveImageService;
import com.composum.assets.commons.service.MetaPropertiesService;
import com.composum.sling.core.concurrent.LazyCreationService;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Context to build variations / renditions and render them.
 */
public class BuilderContext {

    protected final AdaptiveImageService service;
    protected final LazyCreationService lazyCreationService;
    protected final MetaPropertiesService metaPropertiesService;
    protected final ExecutorService executor;
    protected final Map<String, Object> hints;

    public BuilderContext(final AdaptiveImageService service,
                          final LazyCreationService lazyCreationService,
                          MetaPropertiesService metaPropertiesService, final ExecutorService executor,
                          final Map<String, Object> hints) {
        this.service = service;
        this.lazyCreationService = lazyCreationService;
        this.metaPropertiesService = metaPropertiesService;
        this.executor = executor;
        this.hints = hints;
    }

    public AdaptiveImageService getService() {
        return service;
    }

    public LazyCreationService getLazyCreationService() {
        return lazyCreationService;
    }

    public MetaPropertiesService getMetaPropertiesService() {
        return metaPropertiesService;
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    public void hint(String key, Object value) {
        hints.put(key, value);
    }

}
