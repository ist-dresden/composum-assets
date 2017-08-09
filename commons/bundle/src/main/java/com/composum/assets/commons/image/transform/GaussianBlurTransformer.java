package com.composum.assets.commons.image.transform;

import com.composum.assets.commons.config.transform.Blur;
import com.composum.assets.commons.image.ImageTransformer;
import com.composum.assets.commons.image.RenditionTransformer;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * the BLUR transformer service based on a gaussian blur algorithm
 */
@SuppressWarnings("deprecation")
@Component(configurationFactory = true, immediate = true)
@Service()
public class GaussianBlurTransformer implements ImageTransformer {

    public static final Set<String> OP_SET = Collections.singleton(OP_BLUR);

    public static final String KEY_FACTOR = "factor";

    @Override
    public Set<String> getOperations() {
        return OP_SET;
    }

    @Override
    public int rating(String operation, Object options) {
        return OP_BLUR.equalsIgnoreCase(operation) ? 5 : 0;
    }

    @Override
    public BufferedImage transform(RenditionTransformer service, BufferedImage image,
                                   String operation, Object options) {
        @SuppressWarnings("unchecked")
        Blur blur = options instanceof Blur ? (Blur) options : new Blur((Map<String, Object>) options);
        int factor = (Integer) blur.get(KEY_FACTOR);
        ConvolveOp filter = getGaussianBlurFilter(factor);
        for (int i = 0; i < factor; i++) {
            image = filter.filter(image, null);
        }
        return image;
    }

    public static ConvolveOp getGaussianBlurFilter(int blurFactor) {

        int square = blurFactor * blurFactor;
        float value = 1.0f / (float) square;
        float kernelData[] = new float[square];
        Arrays.fill(kernelData, value);

        Kernel kernel = new Kernel(blurFactor, blurFactor, kernelData);

        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    }
}