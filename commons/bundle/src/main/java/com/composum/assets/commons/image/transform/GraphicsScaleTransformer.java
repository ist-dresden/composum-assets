package com.composum.assets.commons.image.transform;

import com.composum.assets.commons.config.aspect.Size;
import com.composum.assets.commons.image.ImageTransformer;
import com.composum.assets.commons.image.RenditionTransformer;
import org.osgi.service.component.annotations.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * the SCALE transformer service based on the JDK Graphics algorithms
 */
@SuppressWarnings("deprecation")
@Component(
        service = ImageTransformer.class,
        immediate = true
)
public class GraphicsScaleTransformer implements ImageTransformer {

    public static final Set<String> OP_SET = Collections.singleton(OP_SCALE);

    @Override
    public Set<String> getOperations() {
        return OP_SET;
    }

    @Override
    public int rating(String operation, Object options) {
        return OP_SCALE.equalsIgnoreCase(operation) ? 8 : 0;
    }

    @Override
    public BufferedImage transform(RenditionTransformer service, BufferedImage image,
                                   String operation, Object options) {
        @SuppressWarnings("unchecked")
        Size size = options instanceof Size ? (Size) options : new Size((Map<String, Object>) options);
        BufferedImage scaled = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, size.width, size.height, null);
        g2.dispose();
        return scaled;
    }
}
