package com.composum.assets.commons.setup;

import com.composum.sling.core.service.RepositorySetupService;
import com.composum.sling.core.setup.util.SetupUtil;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.apache.jackrabbit.JcrConstants.MIX_VERSIONABLE;

/**
 * Setup for Assets module.
 */
public class SetupHook implements InstallHook {

    private static final Logger LOG = LoggerFactory.getLogger(SetupHook.class);

    public static final String ASSETS_SYSTEM_USERS_PATH = "system/composum/assets/";

    public static final String ASSETS_SERVICE_USER = "composum-assets-service";

    public static final String MIXIN_ASSET_FOLDER = "cpa:assetFolderContent";

    private static final String SERVICE_ACLS = "/conf/composum/assets/commons/acl/service.json";

    public static final Map<String, List<String>> ASSETS_USERS;
    public static final Map<String, List<String>> ASSETS_SYSTEM_USERS;
    public static final Map<String, List<String>> ASSETS_GROUPS;

    static {
        ASSETS_USERS = new LinkedHashMap<>();
        ASSETS_SYSTEM_USERS = new LinkedHashMap<>();
        ASSETS_SYSTEM_USERS.put(ASSETS_SYSTEM_USERS_PATH + ASSETS_SERVICE_USER, emptyList());
        ASSETS_GROUPS = new LinkedHashMap<>();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void execute(InstallContext ctx) throws PackageException {
        switch (ctx.getPhase()) {
            case PREPARE:
                LOG.info("prepare: execute...");
                SetupUtil.setupGroupsAndUsers(ctx, ASSETS_GROUPS, ASSETS_SYSTEM_USERS, ASSETS_USERS);
                LOG.info("prepare: execute ends.");
                break;
            case INSTALLED:
                LOG.info("installed: execute...");
                setupAcls(ctx);
                checkContentNodeMixins(ctx, "cpa:AssetConfiguration", new String[]{MIXIN_ASSET_FOLDER});
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

    protected void setupAcls(InstallContext ctx) throws PackageException {
        RepositorySetupService setupService = SetupUtil.getService(RepositorySetupService.class);
        try {
            Session session = ctx.getSession();
            setupService.addJsonAcl(session, SERVICE_ACLS, null);
            session.save();
        } catch (Exception rex) {
            LOG.error(rex.getMessage(), rex);
            throw new PackageException(rex);
        }
    }

    /**
     * We want all jcr:content nodes containing an cpa:AssetConfiguration to be versionable so that they can be
     * published. Thus, we need to add a mix:versionable if need be.
     */
    protected void checkContentNodeMixins(InstallContext ctx, String primaryType, String[] mixins)
            throws PackageException {
        try {
            Session session = ctx.getSession();
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery("select * from [" + primaryType + "]", Query.JCR_SQL2);
            QueryResult result = query.execute();
            for (NodeIterator it = result.getNodes(); it.hasNext(); ) {
                Node node = it.nextNode();
                if (node.isNodeType(primaryType)) {
                    while (node != null && !node.getName().equals(JcrConstants.JCR_CONTENT)) {
                        node = node.getParent();
                    }
                    for (String mixin : mixins) {
                        if (node != null && !node.isNodeType(mixin)) {
                            LOG.warn("Consider adding {} to {} to have the asset configuration versioned",
                                    mixin, node.getPath());
                            // Strangely Node.addMixin fails with missing mandatory properties. :-(
                            node.addMixin(mixin);
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
            throw new PackageException(e);
        }
    }

    protected void updateNodeTypes(InstallContext ctx) throws PackageException {
        try {
            Session session = ctx.getSession();
            NodeTypeManager nodeTypeManager = session.getWorkspace().getNodeTypeManager();
            boolean updateIsNecessary = isUpdateIsNecessary(nodeTypeManager);
            if (updateIsNecessary) {
                LOG.info("Updating asset nodetypes necessary.");
                try (VaultPackage vaultPackage = ctx.getPackage()) {
                    Archive archive = vaultPackage.getArchive();
                    try (InputStream stream = archive.openInputStream(archive.getEntry("/META-INF/vault/nodetypes.cnd"))) {
                        if (stream != null) {
                            InputStreamReader cndReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                            CndImporter.registerNodeTypes(cndReader, session, true);
                        }
                    }
                }
                if (isUpdateIsNecessary(nodeTypeManager)) {
                    LOG.error("Bug: after updating nodetypes we have still updateIsNecessary=true");
                }
            } else {
                LOG.info("No asset nodetype update needed.");
            }
        } catch (Exception rex) {
            LOG.error(rex.getMessage(), rex);
            throw new PackageException(rex);
        }
    }

    private boolean isUpdateIsNecessary(NodeTypeManager nodeTypeManager) throws RepositoryException {
        NodeType assetContentType = nodeTypeManager.getNodeType("cpa:AssetContent");
        return !assetContentType.isNodeType(MIX_VERSIONABLE)
                || !assetContentType.isNodeType(NodeType.MIX_LOCKABLE)
                || nodeTypeManager.getNodeType(MIXIN_ASSET_FOLDER) == null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> type) {
        Bundle serviceBundle = FrameworkUtil.getBundle(type);
        BundleContext serviceBundleContext = serviceBundle.getBundleContext();
        ServiceReference<?> serviceReference = serviceBundleContext.getServiceReference(type.getName());
        return (T) serviceBundleContext.getService(serviceReference);
    }
}
