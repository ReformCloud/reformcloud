package de.klaro.reformcloud2.permissions.util;

import de.klaro.reformcloud2.executor.api.common.utility.annotiations.Nullable;
import de.klaro.reformcloud2.permissions.util.group.PermissionGroup;
import de.klaro.reformcloud2.permissions.util.permission.PermissionNode;
import de.klaro.reformcloud2.permissions.util.user.PermissionUser;

import java.util.UUID;

public interface PermissionUtil {

    @Nullable
    PermissionGroup getGroup(String name);

    @Nullable
    PermissionGroup createGroup(String name);

    void deleteGroup(String name);

    boolean hasPermission(PermissionUser permissionUser, String permission);

    PermissionUser loadUser(UUID uuid);

    void addUserPermission(UUID uuid, PermissionNode permissionNode);

    void deleteUser(UUID uuid);
}
