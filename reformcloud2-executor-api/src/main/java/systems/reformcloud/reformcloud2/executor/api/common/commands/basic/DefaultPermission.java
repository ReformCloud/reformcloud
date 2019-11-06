package systems.reformcloud.reformcloud2.executor.api.common.commands.basic;

import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.Permission;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;

import javax.annotation.Nonnull;

public final class DefaultPermission implements Permission {

    public DefaultPermission(String permission, PermissionResult defaultResult) {
        this.permission = permission;
        this.defaultResult = defaultResult;
    }

    private final String permission;

    private final PermissionResult defaultResult;

    @Nonnull
    @Override
    public String permission() {
        return permission;
    }

    @Nonnull
    @Override
    public PermissionResult defaultResult() {
        return defaultResult;
    }
}
