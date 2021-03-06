/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.image;

import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.aspect.Crop;
import com.composum.assets.commons.config.aspect.Size;
import com.composum.assets.commons.config.aspect.Watermark;
import com.composum.assets.commons.config.transform.Blur;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.platform.commons.util.LockAsAutoCloseable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The RenditionTransformer is performing the build of each rendition requested by the AdaptiveImageServlet.
 * This implementation is collecting the set of available ImageTransformer services which are used to perform
 * the image transformation operations according to the asset configuration set if the image asset.
 */
@Component(
        service = {RenditionTransformer.class},
        immediate = true
)
public class DefaultRenditionTransformer implements RenditionTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRenditionTransformer.class);

    public static final float RATIO_SIMILARITY = 0.05f;

    public static final Crop ASPECT_RATIO_CROP = new Crop();

    /**
     * Maps operations to transformer list. Always access locked by {@link #readWriteLock}.
     */
    protected Map<String, List<ImageTransformer>> imageTransformers = new HashMap<>();

    /**
     * For imageTransformers, since it's a complicated datastructure to update.
     */
    protected ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * Registers a ImageTransformer service implementation.
     * Injection of the ImageTransformer services provided by the OSGi configuration.
     */
    @Reference(service = ImageTransformer.class,
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC)
    protected void bindImageTransformer(final ImageTransformer transformer) {
        for (String operation : transformer.getOperations()) {
            bindImageTransformer(operation, transformer);
        }
    }

    /**
     * Registers a ImageTransformer service implementation for one operation.
     */
    protected void bindImageTransformer(String operation, final ImageTransformer transformer) {
        try (LockAsAutoCloseable ignored = LockAsAutoCloseable.lock(readWriteLock.readLock())) {
            List<ImageTransformer> transformers = imageTransformers.computeIfAbsent(operation,
                    k -> Collections.synchronizedList(new ArrayList<>()));
            LOG.info("transformer.bind: [{}] {}", operation, transformer.getClass().getName());
            transformers.add(transformer);
        }
    }

    /**
     * Removes a ImageTransformer service implementation from the transformer configuration.
     */
    protected void unbindImageTransformer(final ImageTransformer transformer) {
        for (String operation : transformer.getOperations()) {
            unbindImageTransformer(operation, transformer);
        }
    }

    protected void unbindImageTransformer(String operation, final ImageTransformer transformer) {
        try (LockAsAutoCloseable ignored = LockAsAutoCloseable.lock(readWriteLock.readLock())) {
            List<ImageTransformer> transformers = imageTransformers.get(operation);
            if (transformers != null && transformers.contains(transformer)) {
                LOG.info("transformer.unbind: [{}] {}", operation, transformer.getClass().getName());
                transformers.remove(transformer);
            }
        }
    }

    /**
     * Performs a transformation operation using the best matching transformer in the configuration set.
     */
    @Override
    public BufferedImage transform(BufferedImage image, String operation, Object options) {
        ImageTransformer transformer = null;
        try (LockAsAutoCloseable ignored = LockAsAutoCloseable.lock(readWriteLock.readLock())) {
            List<ImageTransformer> transformers = imageTransformers.get(operation);
            if (transformers != null) {
                int rating = 0;
                for (ImageTransformer t : transformers) {
                    int r = t.rating(operation, options);
                    if (r > rating) {
                        rating = r;
                        transformer = t;
                    }
                }
            }
        }
        if (transformer != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("do transform [{}] using {}", operation, transformer.getClass().getName());
            }
            return transformer.transform(this, image, operation, options);
        } else {
            LOG.warn("no transfomer configured for operation: {}", operation);
            return image;
        }
    }

    @Override
    public BufferedImage scale(BufferedImage image, int width, int height) {
        return transform(image, ImageTransformer.OP_SCALE, new Size(width, height, null));
    }

    @Override
    public BufferedImage crop(BufferedImage image, int width, int height, Crop options) {
        return transform(image, ImageTransformer.OP_CROP, new Crop(width, height, options));
    }

    @Override
    @Nonnull
    public BufferedImage transform(@Nonnull final AssetRendition rendition, @Nonnull BufferedImage image,
                                   @Nonnull final BuilderContext context) {
        RenditionConfig config = rendition.getConfig();
        if (LOG.isDebugEnabled()) {
            LOG.debug("rendition transformation: {}", config);
        }
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        Size size = config.getSize();
        Integer renditionWidth = size.getWidth();
        Integer renditionHeight = size.getHeight();
        Float aspectRatio = size.getAspectRatio();
        if (aspectRatio == null) {
            aspectRatio = (float) imageWidth / (float) imageHeight;
        }
        if (renditionHeight == null && renditionWidth != null) {
            renditionHeight = Math.round((float) renditionWidth / aspectRatio);
        }
        if (renditionWidth == null && renditionHeight != null) {
            renditionWidth = Math.round((float) renditionHeight * aspectRatio);
        }
        //noinspection ConstantConditions
        if (renditionWidth != null && renditionHeight != null &&
                (renditionWidth < imageWidth || renditionHeight < imageHeight)) {
            Crop crop = config.getCrop();
            float imageRatio = (float) imageWidth / (float) imageHeight;
            if (crop.isDefault()) {
                float ratioAbberation = imageRatio - aspectRatio;
                if (ratioAbberation < -RATIO_SIMILARITY) {
                    image = crop(image, renditionWidth, renditionHeight, ASPECT_RATIO_CROP);
                } else if (ratioAbberation > RATIO_SIMILARITY) {
                    image = crop(image, renditionWidth, renditionHeight, ASPECT_RATIO_CROP);
                } else {
                    image = scale(image, renditionWidth, renditionHeight);
                }
            } else {
                image = crop(image, renditionWidth, renditionHeight, crop);
            }
        }
        Blur blur = config.getBlur();
        if (blur.isValid()) {
            image = transform(image, ImageTransformer.OP_BLUR, blur);
        }
        Watermark watermark = config.getWatermark();
        if (watermark.isValid()) {
            image = transform(image, ImageTransformer.OP_WATERMARK, watermark);
        }
        return image;
    }
}
