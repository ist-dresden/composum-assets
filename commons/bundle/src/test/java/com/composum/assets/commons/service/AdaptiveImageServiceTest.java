package com.composum.assets.commons.service;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.image.DefaultRenditionTransformer;
import com.composum.assets.commons.servlet.AdaptiveImageServlet;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.concurrent.LazyCreationServiceImpl;
import com.composum.sling.core.concurrent.SemaphoreSequencer;
import com.composum.sling.core.mapping.MappingRules;
import com.composum.sling.core.util.JsonUtil;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.testing.testutil.ErrorCollectorAlwaysPrintingFailures;
import com.composum.sling.platform.testing.testutil.JcrTestUtils;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import java.io.InputStream;
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
    private AssetsService assetService;
    private BeanContext beanContext;
    private Resource asset1Resource;

    @Rule
    public final ErrorCollectorAlwaysPrintingFailures ec = new ErrorCollectorAlwaysPrintingFailures();

    /**
     * Uses content copied from the prototype site,
     * http://localhost:9090/content/prototype/composum/assets/site-1.infinity.json , files removed.
     */
    @Before
    public void setup() throws Exception {
        Session session = context.resourceResolver().adaptTo(Session.class);
        InputStreamReader cndReader = new InputStreamReader(getClass().getResourceAsStream("/adaptiveImageServiceTest" +
                "/nodetypes.cnd"));
        NodeType[] nodeTypes = CndImporter.registerNodeTypes(cndReader, session);
        assertEquals(10, nodeTypes.length);
        context.resourceResolver().commit();
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
        assetService = context.registerInjectActivateService(new DefaultAssetsService());

        beanContext = new BeanContext.Service(context.resourceResolver());
        asset1Resource = context.resourceResolver().getResource("/test/assets/site-1/images/image-1.png");
    }

    @After
    public void tearDown() throws PersistenceException {
        // make sure everything is in a consistent state.
        context.resourceResolver().commit();
    }

    @Test
    public void retrieveImage() throws Exception {
        ImageAsset asset = new ImageAsset(beanContext, asset1Resource);
        assertTrue(asset.isValid());
        ec.checkThat(asset.getTransientsPath().replaceAll("workspace-[0-9]*", "workspace-time"),
                is("/var/composum/assets/test/assets/site-1/theuuidofthereplicatedversion/images/image-1.png/workspace-time"));
        ec.onFailure(() -> JcrTestUtils.printResourceRecursivelyAsJson(context.resourceResolver().getResource(
                "/test/assets/site-1/images/image-1.png/thumbnail")));

        AssetRendition rendition = service.getOrCreateRendition(asset, "thumbnail", "medium");
        assertNotNull(rendition);
        assertTrue(rendition.isValid());
        InputStream stream = rendition.getStream();
        assertNotNull(stream);
        ec.checkThat(IOUtils.toByteArray(stream).length, is(189558));
    }

    @Test
    public void changeImage() throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(
                "adaptiveImageServiceTest/images/image-2.png/square/original/image-2.png");
        assertNotNull(stream);
        assetService.changeImageAsset(beanContext, asset1Resource, "wide", stream);

        ImageAsset asset = new ImageAsset(beanContext, asset1Resource);
        AssetRendition rendition = service.getOrCreateRendition(asset, "thumbnail", "medium");
        ec.checkThat(IOUtils.toByteArray(rendition.getStream()).length, is(161679));
    }

    /** Differential-test for {@link #createOtherOriginal()} : situation without change (of size). */
    @Test
    public void noCreateOtherOriginal() throws Exception {
        ImageAsset asset = new ImageAsset(beanContext, asset1Resource);
        AssetRendition rendition = service.getOrCreateRendition(asset, "bar", "medium");
        ec.checkThat(IOUtils.toByteArray(rendition.getStream()).length, is(189558));
    }

    /** Creates a new original for the variation bar. */
    @Test
    public void createOtherOriginal() throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(
                "adaptiveImageServiceTest/images/image-2.png/square/original/image-2.png");
        assertNotNull(stream);
        assetService.uploadImageAsset(beanContext, "/test/assets/site-1/images", "image-1.png", "bar", stream);

        ImageAsset asset = new ImageAsset(beanContext, asset1Resource);
        AssetRendition rendition = service.getOrCreateRendition(asset, "bar", "medium");
        ec.checkThat(IOUtils.toByteArray(rendition.getStream()).length, is(161679));
    }

    /** Creates a new asset. */
    @Test
    public void createNewAsset() throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(
                "adaptiveImageServiceTest/images/image-2.png/square/original/image-2.png");
        assertNotNull(stream);
        Resource assetResource = assetService.createImageAsset(beanContext, "/test/assets/site-1/images",
                "newasset.png", "bar", stream);
        ec.onFailure(() -> JcrTestUtils.printResourceRecursivelyAsJson(assetResource));
        // the meta node is specified as autocreated, but Jackrabbit doesn't create it for unknown reasons in this
        // test. :-( So we have to do it manually to be able to commit.
        context.build().resource("test/assets/site-1/images/newasset.png/jcr:content/meta",
                ResourceUtil.PROP_PRIMARY_TYPE, AssetsConstants.NODE_TYPE_META_DATA);
        context.resourceResolver().commit();

        ImageAsset asset = new ImageAsset(beanContext, assetResource);
        AssetRendition rendition = service.getOrCreateRendition(asset, "bar", "medium");
        ec.checkThat(IOUtils.toByteArray(rendition.getStream()).length, is(161679));
    }

}
