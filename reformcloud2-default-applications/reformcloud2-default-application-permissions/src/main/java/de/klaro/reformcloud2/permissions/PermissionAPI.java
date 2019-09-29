package de.klaro.reformcloud2.permissions;

import de.klaro.reformcloud2.permissions.util.PermissionUtil;
import de.klaro.reformcloud2.permissions.util.basic.DefaultPermissionUtil;

public class PermissionAPI {

    public static final PermissionAPI INSTANCE = new PermissionAPI();

    private PermissionAPI() {
        this.permissionUtil = new DefaultPermissionUtil();
    }

    private final PermissionUtil permissionUtil;

    public PermissionUtil getPermissionUtil() {
        return permissionUtil;
    }
}
