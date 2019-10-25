package com.composum.assets.commons.service;

import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.image.DefaultRenditionTransformer;
import com.composum.assets.commons.servlet.AdaptiveImageServlet;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.concurrent.LazyCreationServiceImpl;
import com.composum.sling.core.concurrent.SemaphoreSequencer;
import com.composum.sling.core.mapping.MappingRules;
import com.composum.sling.core.util.JsonUtil;
import com.composum.sling.platform.testing.testutil.ErrorCollectorAlwaysPrintingFailures;
import com.google.gson.stream.JsonWriter;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import java.io.InputStreamReader;
import java.io.StringWriter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the creation of renditions.
 */
public class AdaptiveImageServiceTest {

    @Rule
    public final SlingContext context = new SlingContext(ResourceResolverType.JCR_OAK);
    private AdaptiveImageServlet servlet;
    private AdaptiveImageService service;

    @Rule
    public final ErrorCollectorAlwaysPrintingFailures ec = new ErrorCollectorAlwaysPrintingFailures();

    /**
     * Uses content copied from the prototype site,
     * http://localhost:9090/content/prototype/composum/assets/site-1.infinity.json , files removed.
     */
    @Before
    public void setup() throws Exception {
        // printResourceRecursivelyAsJson(context.resourceResolver().getResource("/"));
        Session session = context.resourceResolver().adaptTo(Session.class);
        InputStreamReader cndReader = new InputStreamReader(getClass().getResourceAsStream("/adaptiveImageServiceTest" +
                "/nodetypes.cnd"));
        NodeType[] nodeTypes = CndImporter.registerNodeTypes(cndReader, session);
        assertEquals(10, nodeTypes.length);
        context.build().resource("/test/assets").commit();
        context.load().json("/adaptiveImageServiceTest/site-1.json", "/test/assets/site-1");
        context.load().binaryFile("/adaptiveImageServiceTest/images/image-1.png/wide/original/image-1.png",
                "/test/assets/site-1/images/image-1.png/wide/original/image-1.png");
        context.load().binaryFile("/adaptiveImageServiceTest/images/image-2.png/square/original/image-2.png",
                "/test/assets/site-1/images/image-2.png/square/original/image-2.png");
        context.registerInjectActivateService(new SemaphoreSequencer());
        context.registerInjectActivateService(new LazyCreationServiceImpl());
        context.registerInjectActivateService(new DefaultRenditionTransformer());
        service = context.registerInjectActivateService(new DefaultAdaptiveImageService());
    }

    @After
    public void teardown() throws Exception {
        context.resourceResolver().adaptTo(Session.class).refresh(false);
        printResourceRecursivelyAsJson(
                context.resourceResolver().getResource("/test/assets/site-1/images/image-1.png/thumbnail"));
    }

    @Test
    public void test() throws Exception {
        BeanContext beanContext = new BeanContext.Service(context.resourceResolver());
        Resource assetResource = context.resourceResolver().getResource("/test/assets/site-1/images/image-1.png");
        ImageAsset asset = new ImageAsset(beanContext, assetResource);
        assertTrue(asset.isValid());
        AssetRendition rendition = service.getOrCreateRendition(asset, "thumbnail", "medium");
        assertNotNull(rendition);
        assertTrue(rendition.isValid());
        assertNotNull(rendition.getStream());
        ec.checkThat(asset.getTransientsPath().replaceAll("workspace-[0-9]*", "workspace-time"),
                is("/var/composum/assets/test/assets/site-1/theuuidofthereplicatedversion/images/image-1.png/workspace-time"));
    }

    /**
     * Prints a resource and its subresources as JSON, depth effectively unlimited.
     * TODO: place this method somewhere sensible.
     */
    public static void printResourceRecursivelyAsJson(Resource resource) throws Exception {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.setHtmlSafe(true);
        jsonWriter.setIndent("    ");
        JsonUtil.exportJson(jsonWriter, resource, MappingRules.getDefaultMappingRules(), 99);
        System.out.println(writer);
    }

}
