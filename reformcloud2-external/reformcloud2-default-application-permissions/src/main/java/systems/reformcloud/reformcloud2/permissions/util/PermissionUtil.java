package systems.reformcloud.reformcloud2.permissions.util;

import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public interface PermissionUtil {

    @Nullable
    PermissionGroup getGroup(String name);

    void updateGroup(PermissionGroup permissionGroup);

    void addGroupPermission(PermissionGroup permissionGroup, PermissionNode permissionNode);

    void addProcessGroupPermission(String processGroup, PermissionGroup permissionGroup, PermissionNode permissionNode);

    @Nonnull
    PermissionGroup createGroup(String name);

    void addDefaultGroup(String group);

    void removeDefaultGroup(String group);

    @Nonnull
    Collection<PermissionGroup> getDefaultGroups();

    void deleteGroup(String name);

    boolean hasPermission(PermissionUser permissionUser, String permission);

    boolean hasPermission(PermissionGroup group, String permission);

    @Nonnull
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

    void handleInternalDefaultGroupsUpdate();
}
