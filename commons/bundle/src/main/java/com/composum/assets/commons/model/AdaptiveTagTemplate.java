package com.composum.assets.commons.model;

import com.composum.assets.commons.util.ImageUtil;
import com.composum.assets.commons.util.TemplateUtil;
import com.composum.sling.core.util.LinkUtil;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.assets.commons.util.TemplateUtil.TEMPLATE_ENCODING;

/**
 * a code snippet based template to create an HTML image/picture tag
 * <br/>
 * the snippet is a file which contains keys for the image properties and URLs, e.g.:
 * <code><pre>
 * <picture>
 *     <source media="(min-width: 1200px)" srcset="${wide.medium} 1x, ${wide.large} 2x"/>
 *     <source media="(min-width: 769px)" srcset="${square.medium} 1x, ${square.large} 2x"/>
 *     <source srcset="${normal.small} 1x, ${normal.medium} 2x"/>
 *     <img src="${}" title="${title}" alt="${alt}"/>
 * </picture>
 * the '${variation.rendition}' keys are replaces by the corresponding image URLs;
 * the '${}' key is replaced by the default image URL
 * </pre></code>
 */
public class AdaptiveTagTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(AdaptiveTagTemplate.class);

    public static final String KEY_ALT = "alt";
    public static final String KEY_TITLE = "title";

    public static final Pattern RENDITION_PATTERN = Pattern.compile("\\$\\{([^.}]*)\\.?([^.}]*)\\}");

    protected String code = "";

    public AdaptiveTagTemplate(Resource resource) {
        try {
            try (InputStream inputStream = TemplateUtil.getTemplate(resource)) {
                code = IOUtils.toString(inputStream, TEMPLATE_ENCODING);
            } catch (IOException ioex) {
                LOG.error(ioex.getMessage(), ioex);
            }
        } catch (RepositoryException rex) {
            LOG.error(rex.getMessage(), rex);
        }
    }

    public String buildTag(AdaptiveImageComponent component) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = RENDITION_PATTERN.matcher(code);
        int len = code.length();
        int pos = 0;
        while (matcher.find(pos)) {
            result.append(code, pos, matcher.start());
            String variation = matcher.group(1);
            String rendition = matcher.group(2);
            switch (variation + rendition) {
                case KEY_TITLE:
                    result.append(component.getTitle());
                    break;
                case KEY_ALT:
                    result.append(component.getAltText());
                    break;
                default:
                    result.append(LinkUtil.getUrl(component.getRequest(),
                            ImageUtil.getImageUri(component.getAsset(), variation, rendition)));
                    break;
            }
            pos = matcher.end();
        }
        if (pos >= 0 && pos < len) {
            result.append(code, pos, len);
        }
        return result.toString();
    }
}
