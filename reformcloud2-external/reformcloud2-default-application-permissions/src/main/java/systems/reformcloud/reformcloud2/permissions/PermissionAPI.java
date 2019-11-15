package systems.reformcloud.reformcloud2.permissions;

import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.permissions.util.PermissionUtil;
import systems.reformcloud.reformcloud2.permissions.util.basic.DefaultPermissionUtil;

import javax.annotation.Nonnull;

public final class PermissionAPI {

    private static PermissionAPI instance;

    private final PermissionUtil permissionUtil;

    // ====

    private PermissionAPI() {
        Conditions.isTrue(instance != null);
        this.permissionUtil = DefaultPermissionUtil.hello();
    }

    @Nonnull
    public PermissionUtil getPermissionUtil() {
        return permissionUtil;
    }

    @Nonnull
    public static PermissionAPI getInstance() {
        return instance;
    }

    /**
     * This methods prevents an exception when creating a new instance of this class
     */
    public static void handshake() {
        if (instance != null) {
            return;
        }

        instance = new PermissionAPI();
    }
}
