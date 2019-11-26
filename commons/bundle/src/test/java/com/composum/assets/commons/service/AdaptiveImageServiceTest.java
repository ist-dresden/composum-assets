package com.composum.assets.commons.service;

import com.composum.assets.commons.AssetsConfigImpl;
import com.composum.assets.commons.AssetsConfiguration;
import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.handle.AssetRendition;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.image.DefaultRenditionTransformer;
import com.composum.assets.commons.image.transform.GaussianBlurTransformer;
import com.composum.assets.commons.image.transform.GraphicsCropTransformer;
import com.composum.assets.commons.image.transform.GraphicsScaleTransformer;
import com.composum.assets.commons.image.transform.GraphicsWatermarkTransformer;
import com.composum.assets.commons.image.transform.ImgScalrTransformer;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.concurrent.LazyCreationServiceImpl;
import com.composum.sling.core.concurrent.SemaphoreSequencer;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.ReleasedVersionable;
import com.composum.sling.platform.staging.StagingConstants;
import com.composum.sling.platform.staging.StagingReleaseManager;
import com.composum.sling.platform.staging.impl.DefaultStagingReleaseManager;
import com.composum.sling.platform.staging.impl.ReleaseChangeEventPublisherImpl;
import com.composum.sling.platform.staging.query.QueryBuilder;
import com.composum.sling.platform.staging.query.impl.QueryBuilderAdapterFactory;
import com.composum.sling.platform.testing.testutil.AnnotationWithDefaults;
import com.composum.sling.platform.testing.testutil.ErrorCollectorAlwaysPrintingFailures;
import com.composum.sling.platform.testing.testutil.JcrTestUtils;
import com.composum.sling.platform.testing.testutil.SlingMatchers;
import com.google.common.base.Function;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Calendar;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    private @NotNull ResourceResolver resolver;

    @Rule
    public final ErrorCollectorAlwaysPrintingFailures ec = new ErrorCollectorAlwaysPrintingFailures();

    /**
     * Uses content copied from the prototype site,
     * http://localhost:9090/content/prototype/composum/assets/site-1.infinity.json , files removed.
     */
    @Before
    public void setup() throws Exception {
        resolver = context.resourceResolver();
        Session session = resolver.adaptTo(Session.class);
        InputStreamReader cndReader = new InputStreamReader(getClass().getResourceAsStream("/adaptiveImageServiceTest" +
                "/nodetypes.cnd"));
        NodeType[] nodeTypes = CndImporter.registerNodeTypes(cndReader, session);
        assertEquals(10, nodeTypes.length);
        resolver.commit();
        context.build().resource("/test/assets").commit();
        context.load().json("/adaptiveImageServiceTest/site-1.json", "/test/assets/site-1");

        Resource file1 = context.load().binaryFile("/adaptiveImageServiceTest/images/image-1.png/wide/original/image-1.png",
                "/test/assets/site-1/images/image-1.png/wide/original/image-1.png");
        ec.checkThat(IOUtils.toByteArray(file1.getValueMap().get("jcr:content/jcr:data", InputStream.class)).length,
                is(120063));
        Resource file2 = context.load().binaryFile("/adaptiveImageServiceTest/images/image-2.png/square/original/image-2.png",
                "/test/assets/site-1/images/image-2.png/square/original/image-2.png");
        ec.checkThat(IOUtils.toByteArray(file2.getValueMap().get("jcr:content/jcr:data", InputStream.class)).length,
                is(155494));

        AssetsConfigImpl assetsConfig = new AssetsConfigImpl();
        assetsConfig.activate(AnnotationWithDefaults.of(AssetsConfigImpl.Configuration.class));
        context.registerService(AssetsConfiguration.class, assetsConfig);
        context.registerInjectActivateService(new AssetMetaPropertiesService());

        context.registerInjectActivateService(new GraphicsScaleTransformer());
        context.registerInjectActivateService(new GraphicsCropTransformer());
        context.registerInjectActivateService(new ImgScalrTransformer());
        context.registerInjectActivateService(new GaussianBlurTransformer());
        context.registerInjectActivateService(new GraphicsWatermarkTransformer());

        context.registerInjectActivateService(new SemaphoreSequencer());
        context.registerInjectActivateService(new LazyCreationServiceImpl());
        context.registerInjectActivateService(new DefaultRenditionTransformer());
        this.service = context.registerInjectActivateService(new DefaultAdaptiveImageService());
        assetService = context.registerInjectActivateService(new DefaultAssetsService());

        beanContext = new BeanContext.Service(resolver);
        asset1Resource = resolver.getResource("/test/assets/site-1/images/image-1.png");
    }

    @After
    public void tearDown() throws PersistenceException {
        // make sure everything is in a consistent state.
        resolver.commit();
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
        ec.checkThat(rendition.getImageUri(), SlingMatchers.stringMatchingPattern(
                "/test/assets/site-1/images/image-1.adaptive.wide.original.png/[0-9a-z]{10,26}/image-1.png"));
        // e.g. "/test/assets/site-1/images/image-1.adaptive.wide.original.png/i1piz0xupo1jh2re7fv8n281/image-1.png"
    }

    @Test
    public void retrieveImage() throws Exception {
        ImageAsset asset = new ImageAsset(beanContext, asset1Resource);
        assertTrue(asset.isValid());
        ec.checkThat(asset.getTransientsPath().replaceAll("workspace-[0-9]*", "workspace-time"),
                is("/var/composum/assets/test/assets/site-1/theuuidofthereplicatedversion/images/image-1.png/workspace-time"));
        ec.onFailure(() -> JcrTestUtils.printResourceRecursivelyAsJson(resolver,
                "/var/composum/assets/test/assets/site-1/"));

        AssetRendition rendition = service.getOrCreateRendition(asset, "thumbnail", "small");
        assertNotNull(rendition);
        assertTrue(rendition.isValid());
        InputStream stream = rendition.getStream();
        assertNotNull(stream);
        ec.checkThat(IOUtils.toByteArray(stream).length, allOf(lessThan(5000), greaterThan(1000)));
        ec.checkThat(rendition.getProperty(AssetsConstants.PROP_LAST_RENDERED, Calendar.class), notNullValue());
        ec.checkThat(rendition.getProperty(AssetsConstants.PROP_ASSETPATH, String.class), is(asset.getPath()));
        ec.checkThat(rendition.getProperty(AssetsConstants.PROP_VARIATIONNAME, String.class),
                is(rendition.getVariation().getName()));
        // checks that the AdjustMetaDataService generated the metadata
        ec.checkThat(rendition.getProperty("image-1.png/jcr:content/meta/Content-Type", String.class), is("image/png"));
        ec.checkThat(rendition.getProperty("image-1.png/jcr:content/meta/width", Integer.class), is(32));
        ec.checkThat(rendition.getImageUri(), SlingMatchers.stringMatchingPattern(
                "/test/assets/site-1/images/image-1.adaptive.thumbnail.small.png/[0-9a-z]{10,26}/image-1.png"));
        // e.g. "/test/assets/site-1/images/image-1.adaptive.thumbnail.small.png/4rctiiaic4j1wk06kyhit0ogo/image-1.png"
    }

    @Test
    public void changeImage() throws Exception {
        int origlength;
        {
            ImageAsset asset = new ImageAsset(beanContext, asset1Resource);
            AssetRendition rendition = service.getOrCreateRendition(asset, "thumbnail", "medium");
            origlength = IOUtils.toByteArray(rendition.getStream()).length;
        }
        Thread.sleep(1100); // make sure modification time in seconds changes
        // otherwise we won't get a new rendition.

        InputStream stream = getClass().getClassLoader().getResourceAsStream(
                "adaptiveImageServiceTest/images/image-2.png/square/original/image-2.png");
        assertNotNull(stream);
        assetService.changeImageAsset(beanContext, asset1Resource, "wide", stream);

        int changedlength;
        {
            ImageAsset asset = new ImageAsset(beanContext, asset1Resource);
            AssetRendition rendition = service.getOrCreateRendition(asset, "thumbnail", "medium");
            changedlength = IOUtils.toByteArray(rendition.getStream()).length;
        }
        ec.checkThat(changedlength, not(is(origlength)));
    }

    /** Creates a new original for the variation bar. */
    @Test
    public void createOtherOriginal() throws Exception {
        int origlength;
        {
            ImageAsset asset = new ImageAsset(beanContext, asset1Resource);
            AssetRendition rendition = service.getOrCreateRendition(asset, "bar", "medium");
            origlength = IOUtils.toByteArray(rendition.getStream()).length;
        }

        InputStream stream = getClass().getClassLoader().getResourceAsStream(
                "adaptiveImageServiceTest/images/image-2.png/square/original/image-2.png");
        assertNotNull(stream);
        assetService.uploadImageAsset(beanContext, "/test/assets/site-1/images", "image-1.png", "bar", stream);

        ImageAsset asset = new ImageAsset(beanContext, asset1Resource);
        AssetRendition rendition = service.getOrCreateRendition(asset, "bar", "medium");
        int newlength = IOUtils.toByteArray(rendition.getStream()).length;
        ec.checkThat(newlength, allOf(lessThan(50000), greaterThan(10000), not(is(origlength))));
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
        ec.onFailure(() -> JcrTestUtils.printResourceRecursivelyAsJson(resolver, assetResource.getPath()));
        ec.onFailure(() -> JcrTestUtils.printResourceRecursivelyAsJson(resolver, AssetsConstants.PATH_TRANSIENTS));
        resolver.commit();

        ImageAsset asset = new ImageAsset(beanContext, assetResource);
        AssetRendition rendition = service.getOrCreateRendition(asset, "bar", "medium");
        ec.checkThat(IOUtils.toByteArray(rendition.getStream()).length,
                allOf(lessThan(50000), greaterThan(10000)));
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
        Resource resource = resolver.getResource(path);
        // autocreated:
        // resourceResolver.create(resource, "meta", ImmutableMap.of(ResourceUtil.PROP_PRIMARY_TYPE,
        // AssetsConstants.NODE_TYPE_META_DATA));
        resource.adaptTo(ModifiableValueMap.class).put(ResourceUtil.PROP_MIXINTYPES,
                new String[]{StagingConstants.TYPE_MIX_PLATFORM_RESOURCE});
        resolver.commit();
        // JcrTestUtils.printResourceRecursivelyAsJson(resourceResolver, path);
        VersionManager versionManager = resolver.adaptTo(Session.class).getWorkspace().getVersionManager();
        Version version = versionManager.checkin(path);
        assertNotNull(version);

        // try behavior in staging.
        // Staging setup from DefaultStagingReleaseManagerTest:
        InputStreamReader cndReader = new InputStreamReader(getClass().getResourceAsStream("/adaptiveImageServiceTest" +
                "/stagingNodetypes.cnd"));
        NodeType[] nodeTypes = CndImporter.registerNodeTypes(cndReader, resolver.adaptTo(Session.class));
        assertEquals(2, nodeTypes.length);

        DefaultStagingReleaseManager releaseManager = new DefaultStagingReleaseManager() {{
            this.configuration = AnnotationWithDefaults.of(DefaultStagingReleaseManager.Configuration.class);
            this.publisher = new ReleaseChangeEventPublisherImpl();
            this.resolverFactory = mock(ResourceResolverFactory.class);
            when(this.resolverFactory.getServiceResourceResolver(null)).thenAnswer((x) -> context.resourceResolver().clone(null));
        }};
        context.registerAdapter(ResourceResolver.class, QueryBuilder.class,
                (Function<ResourceResolver, QueryBuilder>) (resolver) ->
                        new QueryBuilderAdapterFactory().getAdapter(resolver, QueryBuilder.class));

        // Now the test:
        Resource siteResource = resolver.getResource("/test/assets/site-1");
        siteResource.adaptTo(ModifiableValueMap.class).put(ResourceUtil.PROP_MIXINTYPES, new String[]{StagingConstants.TYPE_MIX_RELEASE_ROOT});
        resolver.commit();

        StagingReleaseManager.Release currentRelease = releaseManager.findRelease(siteResource, StagingConstants.CURRENT_RELEASE);
        releaseManager.updateRelease(currentRelease, Arrays.asList(ReleasedVersionable.forBaseVersion(resource)));

        ResourceResolver stagingResolver = releaseManager.getResolverForRelease(currentRelease, null, false);
        Resource stagedResource = stagingResolver.resolve(path);

        // JcrTestUtils.printResourceRecursivelyAsJson(stagedResource);
        ec.checkThat(stagedResource.getValueMap().get(StagingConstants.PROP_REPLICATED_VERSION, String.class), is(version.getIdentifier()));
        ec.checkThat(stagedResource.adaptTo(Node.class).getProperty(StagingConstants.PROP_REPLICATED_VERSION).getString(),
                is(version.getIdentifier()));

    }

}
