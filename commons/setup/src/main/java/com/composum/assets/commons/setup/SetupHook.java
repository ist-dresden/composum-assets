package com.composum.assets.commons.setup;

import com.composum.sling.core.usermanagement.core.UserManagementService;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.vault.fs.io.Archive;
import org.apache.jackrabbit.vault.packaging.InstallContext;
import org.apache.jackrabbit.vault.packaging.InstallHook;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.apache.jackrabbit.vault.packaging.VaultPackage;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SetupHook implements InstallHook {

    private static final Logger LOG = LoggerFactory.getLogger(SetupHook.class);

    public static final String ASSETS_SYSTEM_USERS_PATH = "system/composum/assets/";

    public static final String ASSETS_SERVICE_USER = "composum-assets-service";

    public static final String ADMINISTRATORS_GROUP = "administrators";

    public static final Map<String, List<String>> ASSETS_SYSTEM_USERS;

    static {
        ASSETS_SYSTEM_USERS = new LinkedHashMap<>();
        ASSETS_SYSTEM_USERS.put(ASSETS_SYSTEM_USERS_PATH + ASSETS_SERVICE_USER, Collections.emptyList());
    }

    public static final Map<String, List<String>> ASSETS_GROUPS;

    static {
        ASSETS_GROUPS = new LinkedHashMap<>();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void execute(InstallContext ctx) throws PackageException {
        switch (ctx.getPhase()) {
            case INSTALLED:
                LOG.info("installed: execute...");
                setupGroupsAndUsers(ctx);
                addVersionableMixinToVariousNodes(ctx);
                // updateNodeTypes should be the last actions since we need a session.save() there.
                updateNodeTypes(ctx);
                LOG.info("installed: execute ends.");
        }
    }

    protected void setupGroupsAndUsers(InstallContext ctx) throws PackageException {
        UserManagementService userManagementService = getService(UserManagementService.class);
        try {
            JackrabbitSession session = (JackrabbitSession) ctx.getSession();
            UserManager userManager = session.getUserManager();
            for (Map.Entry<String, List<String>> entry : ASSETS_SYSTEM_USERS.entrySet()) {
                Authorizable user = userManagementService.getOrCreateUser(session, userManager, entry.getKey(), true);
                if (user != null) {
                    for (String groupName : entry.getValue()) {
                        userManagementService.assignToGroup(session, userManager, user, groupName);
                    }
                }
            }
            for (Map.Entry<String, List<String>> entry : ASSETS_GROUPS.entrySet()) {
                Authorizable group = userManagementService.getOrCreateGroup(session, userManager, entry.getKey());
                if (group != null) {
                    for (String groupName : entry.getValue()) {
                        userManagementService.assignToGroup(session, userManager, group, groupName);
                    }
                }
            }
            session.save();
        } catch (RepositoryException | RuntimeException rex) {
            LOG.error(rex.getMessage(), rex);
            throw new PackageException(rex);
        }
    }

    /**
     * We want all jcr:content nodes containing an cpa:AssetConfiguration to be versionable so that they can be
     * published. Thus, we need to add a mix:versionable if need be.
     * Also updating the nodetypes to versionable fails with missing mandatory properties if they aren't versionable.
     * So we have to add a versionable mixin before updating. :-(
     */
    protected void addVersionableMixinToVariousNodes(InstallContext ctx) throws PackageException {
        AtomicReference<ResourceResolver> serviceResolver = new AtomicReference<>();
        try {
            Session session = ctx.getSession();
            QueryManager queryManager = session.getWorkspace().getQueryManager();

            for (String type : Arrays.asList("cpa:AssetConfiguration", "cpa:AssetResource", "cpa:AssetContent")) {
                Query query = queryManager.createQuery("select * from [" + type + "]", Query.JCR_SQL2);
                QueryResult result = query.execute();
                for (NodeIterator it = result.getNodes(); it.hasNext(); ) {
                    Node node = it.nextNode();
                    if (node.isNodeType("cpa:AssetConfiguration")) {
                        while (node != null && !node.getName().equals(JcrConstants.JCR_CONTENT)) {
                            node = node.getParent();
                        }
                    }
                    makeVersionable(node, serviceResolver);
                }
                if (serviceResolver.get() != null) {
                    serviceResolver.get().commit();
                    session.save();
                }
            }
        } catch (RepositoryException | LoginException | PersistenceException e) {
            throw new PackageException(e);
        } finally {
            if (serviceResolver.get() != null) {
                serviceResolver.get().close();
            }
        }
    }

    protected void makeVersionable(Node node, AtomicReference<ResourceResolver> serviceResolverHolder) throws RepositoryException, LoginException {
        if (node != null && !node.isNodeType(JcrConstants.MIX_VERSIONABLE)) {
            LOG.info("Adding {} to {}", JcrConstants.MIX_VERSIONABLE, node.getPath());
            // this surprisingly throws up with missing mandatory properties, so we do it with resources:
            // node.addMixin(JcrConstants.MIX_VERSIONABLE);
            if (serviceResolverHolder.get() == null) {
                serviceResolverHolder.set(getService(ResourceResolverFactory.class).getAdministrativeResourceResolver(null));
            }
            Resource resource = serviceResolverHolder.get().getResource(node.getPath());
            ModifiableValueMap mvm = resource.adaptTo(ModifiableValueMap.class);
            String[] mixins = mvm.get(JcrConstants.JCR_MIXINTYPES, new String[0]);
            List<String> mixinList = new ArrayList<String>(Arrays.asList(mixins));
            mixinList.add(JcrConstants.MIX_VERSIONABLE);
            mvm.put(JcrConstants.JCR_MIXINTYPES, mixinList.toArray(new String[0]));
        }
    }

    protected void updateNodeTypes(InstallContext ctx) throws PackageException {
        try {
            Session session = ctx.getSession();
            NodeTypeManager nodeTypeManager = session.getWorkspace().getNodeTypeManager();
            NodeType assetContentType = nodeTypeManager.getNodeType("cpa:AssetContent");
            NodeType assetResourceType = nodeTypeManager.getNodeType("cpa:AssetResource");
            if (!assetContentType.isNodeType(org.apache.jackrabbit.JcrConstants.MIX_VERSIONABLE)
                    || !assetResourceType.isNodeType(org.apache.jackrabbit.JcrConstants.MIX_VERSIONABLE)
                    || !assetResourceType.isNodeType(JcrConstants.MIX_CREATED)
            ) {
                LOG.info("Updating asset nodetypes neccesary.");
                try (VaultPackage vaultPackage = ctx.getPackage()) {
                    Archive archive = vaultPackage.getArchive();
                    try (InputStream stream = archive.openInputStream(archive.getEntry("/META-INF/vault/nodetypes.cnd"))) {
                        if (stream != null) {
                            InputStreamReader cndReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                            CndImporter.registerNodeTypes(cndReader, session, true);
                        }
                    }
                }
            } else {
                LOG.info("No asset nodetype update needed.");
            }
        } catch (Exception rex) {
            LOG.error(rex.getMessage(), rex);
            throw new PackageException(rex);
        }
    }


    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> type) {
        Bundle serviceBundle = FrameworkUtil.getBundle(type);
        BundleContext serviceBundleContext = serviceBundle.getBundleContext();
        ServiceReference serviceReference = serviceBundleContext.getServiceReference(type.getName());
        return (T) serviceBundleContext.getService(serviceReference);
    }
}
