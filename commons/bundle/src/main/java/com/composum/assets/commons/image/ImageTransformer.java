package com.composum.assets.commons.image;

import java.awt.image.BufferedImage;
import java.util.Set;

/**
 * the interface for the elements of the OSGi image transformers configuration set
 */
public interface ImageTransformer {

    String OP_SCALE = "scale";
    String OP_CROP = "crop";
    String OP_WATERMARK = "watermark";
    String OP_BLUR = "blur";

    /**
     * returns the list of supported operations
     */
    Set<String> getOperations();

    /**
     * How useful is the transformer implementation to perform the operation (0...; 0: not useful).
     */
    int rating(String operation, Object options);

    /**
     * transforms the image
     */
    BufferedImage transform(RenditionTransformer service, BufferedImage image,
                            String operation, Object options);
}
