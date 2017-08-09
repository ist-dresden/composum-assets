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
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The RenditionTransformer is performing the build of each rendition requested by the AdaptiveImageServlet.
 * This implementation is collecting the set of available ImageTransformer services which are used to perform
 * the image transformation operations according to the asset configuration set if the image asset.
 */
@SuppressWarnings("deprecation")
@Component(immediate = true)
@Service()
public class DefaultRenditionTransformer implements RenditionTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRenditionTransformer.class);

    public static final float RATIO_SIMILARITY = 0.05f;

    public static final Crop ASPECT_RATIO_CROP = new Crop();

    /**
     * injection of the ImageTransformer services provided by the OSGi configuration
     */
    @Reference(referenceInterface = ImageTransformer.class,
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            policy = ReferencePolicy.DYNAMIC)
    protected Map<String, List<ImageTransformer>> imageTransformers;

    /**
     * Gets or initializes the transformers configuration.
     */
    @Nonnull
    protected Map<String, List<ImageTransformer>> getImageTransformers() {
        if (imageTransformers == null) {
            imageTransformers = new HashMap<>();
        }
        return imageTransformers;
    }

    /**
     * Registers a ImageTransformer service implementation.
     */
    protected void bindImageTransformer(final ImageTransformer transformer) {
        for (String operation : transformer.getOperations()) {
            bindImageTransformer(operation, transformer);
        }
    }

    /**
     * Registers a ImageTransformer service implementation for one operation.
     */
    protected synchronized void bindImageTransformer(String operation, final ImageTransformer transformer) {
        List<ImageTransformer> transformers = getImageTransformers().get(operation);
        if (transformers == null) {
            imageTransformers.put(operation, transformers = new ArrayList<>());
        }
        LOG.info("transformer.bind: [" + operation + "] " + transformer.getClass().getName());
        transformers.add(transformer);
    }

    /**
     * Removes a ImageTransformer service implementation from the transformer configuration.
     */
    protected synchronized void unbindImageTransformer(final ImageTransformer transformer) {
        for (String operation : transformer.getOperations()) {
            unbindImageTransformer(operation, transformer);
        }
    }

    protected synchronized void unbindImageTransformer(String operation, final ImageTransformer transformer) {
        List<ImageTransformer> transformers = getImageTransformers().get(operation);
        if (transformers != null && transformers.contains(transformer)) {
            LOG.info("transformer.unbind: [" + operation + "] " + transformer.getClass().getName());
            transformers.remove(transformer);
        }
    }

    /**
     * Performs a transformation operation using the best matching transformer in the configuration set.
     */
    @Override
    public BufferedImage transform(BufferedImage image, String operation, Object options) {
        ImageTransformer transformer = null;
        List<ImageTransformer> transformers = getImageTransformers().get(operation);
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
        if (transformer != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("do transform [" + operation + "] using " + transformer.getClass().getName());
            }
            return transformer.transform(this, image, operation, options);
        } else {
            LOG.warn("no transfomer configured for operation: " + operation);
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
    public BufferedImage transform(AssetRendition rendition, BufferedImage image, BuilderContext context) {
        RenditionConfig config = rendition.getConfig();
        if (LOG.isDebugEnabled()) {
            LOG.debug("rendition transformation: " + config);
        }
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        Size size = config.getSize();
        Integer renditionWidth = size.width;
        Integer renditionHeight = size.height;
        Float aspectRatio = size.aspectRatio;
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
