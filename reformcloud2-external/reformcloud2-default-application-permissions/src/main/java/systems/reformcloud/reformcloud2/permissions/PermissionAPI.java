package systems.reformcloud.reformcloud2.permissions;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.permissions.util.PermissionUtil;
import systems.reformcloud.reformcloud2.permissions.util.basic.DefaultPermissionUtil;

public final class PermissionAPI {

    private static PermissionAPI instance;

    private final PermissionUtil permissionUtil;

    // ====

    private PermissionAPI() {
        this.permissionUtil = DefaultPermissionUtil.hello();
    }

    @NotNull
    public PermissionUtil getPermissionUtil() {
        return permissionUtil;
    }

    @NotNull
    public static PermissionAPI getInstance() {
        return instance;
    }

    public static void handshake() {
        if (instance != null) {
            return;
        }

        instance = new PermissionAPI();
    }
}
