package com.composum.assets.commons.image.transform;

import com.composum.assets.commons.config.aspect.Crop;
import com.composum.assets.commons.image.ImageTransformer;
import com.composum.assets.commons.image.RenditionTransformer;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * the CROP transformer service
 */
@SuppressWarnings("deprecation")
@Component(configurationFactory = true, immediate = true)
@Service()
public class GraphicsCropTransformer implements ImageTransformer {

    public static final Set<String> OP_SET = Collections.singleton(OP_CROP);

    @Override
    public Set<String> getOperations() {
        return OP_SET;
    }

    @Override
    public int rating(String operation, Object options) {
        return OP_CROP.equalsIgnoreCase(operation) ? 10 : 0;
    }

    @Override
    public BufferedImage transform(RenditionTransformer service, BufferedImage image,
                                   String operation, Object options) {
        @SuppressWarnings("unchecked")
        Crop crop = options instanceof Crop ? (Crop) options : new Crop((Map<String, Object>) options);
        if (crop.scale != null && crop.scale > 0f) {
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            float aspectRatio = (float) imageWidth / (float) imageHeight;
            float requestedRatio = (float) crop.getWidth() / (float) crop.getHeight();
            if (crop.scale < 1.0f) {
                // scale to factor
                if (aspectRatio < requestedRatio) {
                    imageWidth = Math.round((float) imageWidth * crop.scale);
                    imageHeight = Math.round((float) imageWidth / aspectRatio);
                } else {
                    imageHeight = Math.round((float) imageHeight * crop.scale);
                    imageWidth = Math.round((float) imageHeight * aspectRatio);
                }
                image = service.scale(image, imageWidth, imageHeight);
            } else {
                // scale to target size if scale = '1'
                if (aspectRatio < requestedRatio) {
                    imageWidth = crop.getWidth();
                    imageHeight = Math.round((float) imageWidth / aspectRatio);
                } else {
                    imageHeight = crop.getHeight();
                    imageWidth = Math.round((float) imageHeight * aspectRatio);
                }
                image = service.scale(image, imageWidth, imageHeight);
            }
        }
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int width = Math.min(crop.getWidth(), imageWidth);
        int height = Math.min(crop.getHeight(), imageHeight);
        int x = Math.round((imageWidth - width) * crop.horizontal);
        int y = Math.round((imageHeight - height) * crop.vertical);
        return image.getSubimage(x, y, width, height);
    }
}