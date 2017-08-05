package com.composum.assets.commons.setup;

import com.composum.sling.core.usermanagement.core.UserManagementService;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.vault.packaging.InstallContext;
import org.apache.jackrabbit.vault.packaging.InstallHook;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SetupHook implements InstallHook {

    private static final Logger LOG = LoggerFactory.getLogger(SetupHook.class);

    public static final String ASSETS_IMMEDIATE_PATH = "composum/assets/";

    public static final String ASSETS_MANAGERS_GROUP = "asset-managers";

    public static final String ASSETS_SYSTEM_USERS_PATH = "system/composum/assets/";

    public static final String ASSETS_SERVICE_USER = "composum-assets-service";

    public static final String ADMINISTRATORS_GROUP = "administrators";

    public static final Map<String, List<String>> ASSETS_SYSTEM_USERS;

    static {
        ASSETS_SYSTEM_USERS = new LinkedHashMap<>();
        ASSETS_SYSTEM_USERS.put(ASSETS_SYSTEM_USERS_PATH + ASSETS_SERVICE_USER, Collections.singletonList(
                ADMINISTRATORS_GROUP
        ));
    }

    public static final Map<String, List<String>> ASSETS_GROUPS;

    static {
        ASSETS_GROUPS = new LinkedHashMap<>();
        ASSETS_GROUPS.put(ASSETS_IMMEDIATE_PATH + ASSETS_MANAGERS_GROUP, Collections.<String>emptyList());
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void execute(InstallContext ctx) throws PackageException {
        switch (ctx.getPhase()) {
            case INSTALLED:
                LOG.info("installed: execute...");

                setupGroupsAndUsers(ctx);

                LOG.info("installed: execute ends.");
        }
    }

    protected void setupGroupsAndUsers(InstallContext ctx) {
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
        } catch (RepositoryException rex) {
            LOG.error(rex.getMessage(), rex);
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
