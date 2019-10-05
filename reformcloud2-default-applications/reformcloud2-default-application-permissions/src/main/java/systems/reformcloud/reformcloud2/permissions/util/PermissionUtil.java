package systems.reformcloud.reformcloud2.permissions.util;

import systems.reformcloud.reformcloud2.executor.api.common.utility.annotiations.Nullable;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

import java.util.UUID;

public interface PermissionUtil {

    @Nullable
    PermissionGroup getGroup(String name);

    void updateGroup(PermissionGroup permissionGroup);

    void addGroupPermission(PermissionGroup permissionGroup, PermissionNode permissionNode);

    void addProcessGroupPermission(String processGroup, PermissionGroup permissionGroup, PermissionNode permissionNode);

    @Nullable
    PermissionGroup createGroup(String name);

    void deleteGroup(String name);

    boolean hasPermission(PermissionUser permissionUser, String permission);

    PermissionUser loadUser(UUID uuid);

    void addUserPermission(UUID uuid, PermissionNode permissionNode);

    void updateUser(PermissionUser permissionUser);

    void deleteUser(UUID uuid);

    void handleDisconnect(UUID uuid);

    void handleInternalPermissionGroupUpdate(PermissionGroup permissionGroup);

    void handleInternalPermissionGroupCreate(PermissionGroup permissionGroup);

    void handleInternalPermissionGroupDelete(PermissionGroup permissionGroup);

    void handleInternalUserUpdate(PermissionUser permissionUser);

    void handleInternalUserCreate(PermissionUser permissionUser);

    void handleInternalUserDelete(PermissionUser permissionUser);
}
