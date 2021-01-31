package com.composum.assets.commons.handle;

import com.composum.assets.commons.config.RenditionConfig;
import com.composum.sling.clientlibs.handle.FileHandle;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

public class SimpleImageRendition extends AssetRendition {

    public SimpleImageRendition(@Nonnull final BeanContext context, @Nonnull final Resource resource,
                                @Nonnull final AssetVariation variation, @Nonnull final RenditionConfig config) {
        super(context, resource, variation, config);
    }

    @Override
    public FileHandle getFile() {
        return new FileHandle(getResource());
    }

}
