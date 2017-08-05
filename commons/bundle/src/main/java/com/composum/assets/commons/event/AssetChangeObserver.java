/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.assets.commons.event;

import com.composum.sling.core.event.AbstractChangeObserver;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static com.composum.assets.commons.AssetsConstants.ASSETS_SERVICE_USER;

@Component(immediate = true)
@Service
public class AssetChangeObserver extends AbstractChangeObserver {

    private static final Logger LOG = LoggerFactory.getLogger(AssetChangeObserver.class);

    public static final String PATH = "/content";

    @Reference
    protected SlingRepository repository;

    @Reference
    protected ResourceResolverFactory resolverFactory;

    @Override
    protected String getServiceUserId() {
        return ASSETS_SERVICE_USER;
    }

    @Override
    protected String getObservedPath() {
        return PATH;
    }

    @Override
    protected void doOnChange(ResourceResolver resolver, ChangedResource change)
            throws RepositoryException, PersistenceException {

    }

    @Override
    protected String getTargetPath(Node node)
            throws RepositoryException {
        return isTargetNode(node) ? node.getPath() : null;
    }

    @Override
    protected boolean isTargetNode(Node node) throws RepositoryException {
        return false;
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