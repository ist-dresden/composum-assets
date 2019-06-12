package com.composum.assets.commons.event;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.ConfigHandle;
import com.composum.assets.commons.service.AssetsService;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.event.AbstractChangeObserver;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.NodeUtil;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static com.composum.assets.commons.AssetsConstants.ASSETS_SERVICE_USER;

/**
 * the observer to ensure that only one sibling in a config layer is the default config
 * <code><pre>
 * if (the properties name is 'categories' and
 *     the nodes type is one of the 'assetconfig' types and
 *     the 'categories' property contains a value 'default') {
 *     ensure that this node is the only one with category 'default' in its level
 * }
 * </pre></code>
 */
@Component(immediate = true)
@Service
public class AdjustDefaultConfigObserver extends AbstractChangeObserver {

    private static final Logger LOG = LoggerFactory.getLogger(AdjustDefaultConfigObserver.class);

    public static final String PATH = "/content";

    public static final StringFilter CATEGORY_PROPERTY_FILTER =
            new StringFilter.WhiteList("/jcr:content/(.*/)?categories$");

    public static class DefaultCategoryFilter extends ResourceFilter.AbstractResourceFilter {

        @Override
        public boolean accept(Resource resource) {
            ValueMap values = resource.getValueMap();
            List<String> categories = Arrays.asList(values.get(ConfigHandle.CATEGORIES, new String[0]));
            return categories.contains(ConfigHandle.DEFAULT);
        }

        @Override
        public boolean isRestriction() {
            return false;
        }

        @Override
        public void toString(StringBuilder builder) {
            builder.append(getClass().getSimpleName());
        }
    }

    public static final DefaultCategoryFilter DEFAULT_CATEGORY_FILTER = new DefaultCategoryFilter();

    @Reference
    protected SlingRepository repository;

    @Reference
    protected ResourceResolverFactory resolverFactory;

    @Reference
    protected AssetsService assetsService;

    @Override
    protected String getServiceUserId() {
        return ASSETS_SERVICE_USER;
    }

    @Override
    protected String getObservedPath() {
        return PATH;
    }

    /**
     * changes the meta information for one change item
     */
    @Override
    protected void doOnChange(ResourceResolver resolver, ChangedResource change)
            throws RepositoryException, PersistenceException {
        if (LOG.isInfoEnabled()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(LOG_DATE_FORMAT);
            LOG.info("changed: " + change.getResource().getPath() + ", " +
                    dateFormat.format(change.getTime().getTime()) + ", " +
                    change.getUser());
        }
        Resource resource = change.getResource();
        BeanContext context = new BeanContext.Service(resolver);
        assetsService.setDefaultConfiguration(context, resource, false);
    }

    @Override
    protected StringFilter getPropertyPathFilter() {
        return CATEGORY_PROPERTY_FILTER;
    }

    @Override
    protected ResourceFilter getResourceFilter() {
        return DEFAULT_CATEGORY_FILTER;
    }

    @Override
    protected String getTargetPath(Node node)
            throws RepositoryException {
        return isTargetNode(node) ? node.getPath() : null;
    }

    @Override
    protected boolean isTargetNode(Node node)
            throws RepositoryException {
        return isAssetConfig(node);
    }

    protected boolean isAssetConfig(Node node)
            throws RepositoryException {
        return NodeUtil.isNodeType(node,
                AssetsConstants.NODE_TYPE_ASSET_CONFIG,
                AssetsConstants.NODE_TYPE_VARIATION_CONFIG,
                AssetsConstants.NODE_TYPE_RENDITION_CONFIG);
    }

    @Override
    protected ResourceResolver getResolver()
            throws LoginException {
        return resolverFactory != null ? resolverFactory.getServiceResourceResolver(null) : null;
    }

    @Override
    protected Session getSession()
            throws RepositoryException {
        return repository != null ? repository.loginService(null, null) : null;
    }
}
