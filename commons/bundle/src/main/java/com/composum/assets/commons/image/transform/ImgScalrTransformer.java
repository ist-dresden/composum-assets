package com.composum.assets.commons.image.transform;

import com.composum.assets.commons.config.aspect.Size;
import com.composum.assets.commons.image.ImageTransformer;
import com.composum.assets.commons.image.RenditionTransformer;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * the SCALE transformer service based on the 'imgscalr' algorithms
 */
@SuppressWarnings("deprecation")
@Component(configurationFactory = true, immediate = true)
@Service()
public class ImgScalrTransformer implements ImageTransformer {

    public static final Set<String> OP_SET = Collections.singleton(OP_SCALE);

    @Override
    public Set<String> getOperations() {
        return OP_SET;
    }

    @Override
    public int rating(String operation, Object options) {
        return OP_SCALE.equalsIgnoreCase(operation) ? 10 : 0;
    }

    @Override
    public BufferedImage transform(RenditionTransformer service, BufferedImage image,
                                   String operation, Object options) {
        @SuppressWarnings("unchecked")
        Size size = options instanceof Size ? (Size) options : new Size((Map<String, Object>) options);
        if (size.width < image.getWidth() || size.height < image.getHeight()) {
            image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, size.width, size.height);
        }
        return image;
    }
}