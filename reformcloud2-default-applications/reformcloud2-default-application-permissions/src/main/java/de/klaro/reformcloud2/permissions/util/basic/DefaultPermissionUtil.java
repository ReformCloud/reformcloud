package de.klaro.reformcloud2.permissions.util.basic;

import de.klaro.reformcloud2.permissions.util.PermissionUtil;
import de.klaro.reformcloud2.permissions.util.group.PermissionGroup;
import de.klaro.reformcloud2.permissions.util.permission.PermissionNode;
import de.klaro.reformcloud2.permissions.util.user.PermissionUser;

import java.util.UUID;

public class DefaultPermissionUtil implements PermissionUtil {

    @Override
    public PermissionGroup getGroup(String name) {
        return null;
    }

    @Override
    public PermissionGroup createGroup(String name) {
        return null;
    }

    @Override
    public void deleteGroup(String name) {

    }

    @Override
    public boolean hasPermission(PermissionUser permissionUser, String permission) {
        permission = permission.toLowerCase();
        for (String group : permissionUser.getGroups()) {
            final PermissionGroup permissionGroup = getGroup(group);
            if (permissionGroup == null) {
                continue;
            }

            if (hasPermission(permissionGroup, permission)) {
                return true;
            }

            for (String subGroup : permissionGroup.getSubGroups()) {
                final PermissionGroup sub = getGroup(subGroup);
                if (sub == null) {
                    continue;
                }

                if (hasPermission(sub, permission)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public PermissionUser loadUser(UUID uuid) {
        return null;
    }

    @Override
    public void addUserPermission(UUID uuid, PermissionNode permissionNode) {

    }

    @Override
    public void deleteUser(UUID uuid) {

    }

    private boolean hasPermission(PermissionGroup group, String perm) {
        return group.hasPermission(perm);
    }
}
