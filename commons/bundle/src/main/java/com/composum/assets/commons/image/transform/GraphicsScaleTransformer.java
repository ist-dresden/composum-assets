package com.composum.assets.commons.image.transform;

import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.config.aspect.Size;
import com.composum.assets.commons.image.ImageTransformer;
import com.composum.assets.commons.image.RenditionTransformer;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Set;

/**
 * the SCALE transformer service based on the JDK Graphics algorithms
 */
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
        Size size = options instanceof Size ? (Size) options : options instanceof ConfigHandle
                ? new Size((ConfigHandle) options) : new Size((ValueMap) options, true);
        BufferedImage scaled = null;
        if (size.getWidth() != null && size.getHeight() != null) {
            scaled = new BufferedImage(size.getWidth(), size.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = scaled.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(image, 0, 0, size.getWidth(), size.getHeight(), null);
            g2.dispose();
        }
        return scaled != null ? scaled : image;
    }
}
