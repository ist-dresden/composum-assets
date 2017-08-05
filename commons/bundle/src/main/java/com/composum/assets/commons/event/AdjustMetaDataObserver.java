package com.composum.assets.commons.event;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.service.MetaPropertiesService;
import com.composum.sling.core.event.AbstractChangeObserver;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.NodeUtil;
import com.composum.sling.core.util.PropertyUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.text.SimpleDateFormat;

import static com.composum.assets.commons.AssetsConstants.ASSETS_SERVICE_USER;

/**
 * the observer to adjust the meta data of an image if the image content is changed
 */
@Component(immediate = true)
@Service
public class AdjustMetaDataObserver extends AbstractChangeObserver {

    private static final Logger LOG = LoggerFactory.getLogger(AdjustMetaDataObserver.class);

    public static final String PATH = "/content";

    public static final StringFilter DATA_PROPERTY_FILTER =
            new StringFilter.WhiteList("/jcr:content/jcr:data$");

    @Reference
    protected SlingRepository repository;

    @Reference
    protected ResourceResolverFactory resolverFactory;

    @Reference
    protected MetaPropertiesService metaPropertiesService;

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
        metaPropertiesService.adjustMetaProperties(resolver, resource);
    }

    @Override
    protected StringFilter getPropertyPathFilter() {
        return DATA_PROPERTY_FILTER;
    }

    @Override
    protected String getTargetPath(Node node)
            throws RepositoryException {
        return NodeUtil.isNodeType(node,
                ResourceUtil.TYPE_FOLDER,
                ResourceUtil.TYPE_SLING_FOLDER,
                ResourceUtil.TYPE_SLING_ORDERED_FOLDER)
                ? null : node.getPath();
    }

    @Override
    protected boolean isTargetNode(Node node)
            throws RepositoryException {
        return isAssetContent(node) || isImageResource(node);
    }

    protected boolean isAssetContent(Node node)
            throws RepositoryException {
        return node.isNodeType(AssetsConstants.NODE_TYPE_ASSET_CONTENT);
    }

    protected boolean isImageResource(Node node)
            throws RepositoryException {
        return node.isNodeType(JcrConstants.NT_RESOURCE)
                && PropertyUtil.getProperty(node, JcrConstants.JCR_MIMETYPE, "").startsWith("image/");
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