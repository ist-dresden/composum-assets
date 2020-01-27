package com.composum.assets.commons.image.transform;

import com.composum.assets.commons.config.aspect.Size;
import com.composum.assets.commons.image.ImageTransformer;
import com.composum.assets.commons.image.RenditionTransformer;
import org.apache.sling.api.resource.ValueMap;
import org.imgscalr.Scalr;
import org.osgi.service.component.annotations.Component;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Set;

/**
 * the SCALE transformer service based on the 'imgscalr' algorithms
 */
@Component(
        service = {ImageTransformer.class},
        immediate = true
)
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
        Size size = options instanceof Size ? (Size) options : new Size((ValueMap) options, true);
        if (size.getWidth() != null && size.getHeight() != null &&
                (size.getWidth() < image.getWidth() || size.getHeight() < image.getHeight())) {
            image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT,
                    size.getWidth(), size.getHeight());
        }
        return image;
    }
}
