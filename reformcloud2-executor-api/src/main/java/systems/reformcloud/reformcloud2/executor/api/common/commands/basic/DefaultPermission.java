package systems.reformcloud.reformcloud2.executor.api.common.commands.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.Permission;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;

public final class DefaultPermission implements Permission {

    public DefaultPermission(String permission, PermissionResult defaultResult) {
        this.permission = permission;
        this.defaultResult = defaultResult;
    }

    private final String permission;

    private final PermissionResult defaultResult;

    @NotNull
    @Override
    public String permission() {
        return permission;
    }

    @NotNull
    @Override
    public PermissionResult defaultResult() {
        return defaultResult;
    }
}
