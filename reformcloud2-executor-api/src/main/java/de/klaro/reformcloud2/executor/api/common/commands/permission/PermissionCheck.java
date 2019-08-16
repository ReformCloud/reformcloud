package de.klaro.reformcloud2.executor.api.common.commands.permission;

public interface PermissionCheck {

    PermissionResult checkPermission(PermissionHolder permissionHolder, Permission permission);

    PermissionResult checkPermission(PermissionHolder permissionHolder, String permission);
}
