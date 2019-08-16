package de.klaro.reformcloud2.executor.api.common.commands.basic;

import de.klaro.reformcloud2.executor.api.common.commands.permission.Permission;
import de.klaro.reformcloud2.executor.api.common.commands.permission.PermissionResult;

public final class DefaultPermission implements Permission {

    public DefaultPermission(String permission, PermissionResult defaultResult) {
        this.permission = permission;
        this.defaultResult = defaultResult;
    }

    private final String permission;

    private final PermissionResult defaultResult;

    @Override
    public String permission() {
        return permission;
    }

    @Override
    public PermissionResult defaultResult() {
        return defaultResult;
    }
}
