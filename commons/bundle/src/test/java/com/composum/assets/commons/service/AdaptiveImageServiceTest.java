package com.composum.assets.commons.service;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.image.DefaultRenditionTransformer;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.concurrent.LazyCreationServiceImpl;
import com.composum.sling.core.concurrent.SemaphoreSequencer;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.testing.testutil.ErrorCollectorAlwaysPrintingFailures;
import com.composum.sling.platform.testing.testutil.JcrTestUtils;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.jetbrains.annotations.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the creation of renditions.
 */
public class AdaptiveImageServiceTest {

    @Rule
    public final SlingContext context = new SlingContext(ResourceResolverType.JCR_OAK);
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
    public void retrieveOriginal() throws Exception {
        ImageAsset asset = new ImageAsset(beanContext, asset1Resource);
        AssetRendition rendition = service.getRendition(asset, "wide", "original");
        assertNotNull(rendition);
        assertTrue(rendition.isValid());
        InputStream stream = rendition.getStream();
        assertNotNull(stream);
        ec.checkThat(IOUtils.toByteArray(stream).length, is(120063));
    }

    @Test
    public void retrieveImage() throws Exception {
        ImageAsset asset = new ImageAsset(beanContext, asset1Resource);
        assertTrue(asset.isValid());
        ec.checkThat(asset.getTransientsPath().replaceAll("workspace-[0-9]*", "workspace-time"),
                is("/var/composum/assets/test/assets/site-1/theuuidofthereplicatedversion/images/image-1.png/workspace-time"));
        ec.onFailure(() -> JcrTestUtils.printResourceRecursivelyAsJson(context.resourceResolver(),
                "/test/assets/site-1/images/image-1.png/thumbnail"));

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
        JcrTestUtils.printResourceRecursivelyAsJson(rendition.getResource());

        ec.checkThat(rendition.getTransientsPath().replaceAll("workspace-[0-9]*", "workspace-time"),
                is("/var/composum/assets/test/assets/site-1/theuuidofthereplicatedversion/images" +
                        "/image-1.png/workspace-time/bar/workspace-time/medium"));
    }

    @Test
    public void createNewAsset() throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(
                "adaptiveImageServiceTest/images/image-2.png/square/original/image-2.png");
        assertNotNull(stream);
        Resource assetResource = assetService.createImageAsset(beanContext, "/test/assets/site-1/images",
                "newasset.png", "bar", stream);
        ec.onFailure(() -> JcrTestUtils.printResourceRecursivelyAsJson(context.resourceResolver(), assetResource.getPath()));
        ec.onFailure(() -> JcrTestUtils.printResourceRecursivelyAsJson(context.resourceResolver(), AssetsConstants.PATH_TRANSIENTS));
        // the meta node is specified as autocreated, but Jackrabbit doesn't create it for unknown reasons in this
        // test. :-( So we have to do it manually to be able to commit.
        // FIXME(hps,25.10.19) what about autocreated meta? Autocreating it doesn't work right now as it is
        // configured in the nodetypes.cnd, but when I insert the base type, I get ItemExistsException on the JSON
        // import.
        context.build().resource("test/assets/site-1/images/newasset.png/jcr:content/meta",
                ResourceUtil.PROP_PRIMARY_TYPE, AssetsConstants.NODE_TYPE_META_DATA);
        context.resourceResolver().commit();

        ImageAsset asset = new ImageAsset(beanContext, assetResource);
        AssetRendition rendition = service.getOrCreateRendition(asset, "bar", "medium");
        ec.checkThat(IOUtils.toByteArray(rendition.getStream()).length, is(161679));
    }

    @Test
    public void accessInvalidAsset() throws Exception {
        ImageAsset asset = new ImageAsset(beanContext, asset1Resource);
        ec.checkThat(service.getRendition(asset, "bar", "invalid"), nullValue());
        ec.checkThat(service.getRendition(asset, "invalid", "medium"), nullValue());
        ec.checkThat(service.getOrCreateRendition(asset, "bar", "invalid"), nullValue());
        ec.checkThat(service.getOrCreateRendition(asset, "invalid", "medium"), nullValue());
    }

    /**
     * Checks whether an assetcontent is actually versionable - in earlier versions the metadata outocreation did
     * not work, and then a checkin failed for some reason.
     */
    @Test
    public void checkinAsset() throws Exception {
        String path = "/test/assets/site-1/images/image-1.png/wide/original/image-1.png/jcr:content";
        Resource resource = context.resourceResolver().getResource(path);
        context.resourceResolver().create(resource, "meta", ImmutableMap.of(ResourceUtil.PROP_PRIMARY_TYPE,
                AssetsConstants.NODE_TYPE_META_DATA));
        resource.adaptTo(ModifiableValueMap.class).put(ResourceUtil.PROP_MIXINTYPES,
                new String[]{AssetsConstants.MIXIN_TYPE_ASSET_RESOURCE});
        context.resourceResolver().commit();
        JcrTestUtils.printResourceRecursivelyAsJson(context.resourceResolver(), path);
        VersionManager versionManager = context.resourceResolver().adaptTo(Session.class).getWorkspace().getVersionManager();
        Version version = versionManager.checkin(path);
        assertNotNull(version);
    }

}
