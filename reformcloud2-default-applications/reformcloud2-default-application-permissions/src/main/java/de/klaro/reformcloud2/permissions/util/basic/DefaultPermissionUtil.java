package de.klaro.reformcloud2.permissions.util.basic;

import de.klaro.reformcloud2.executor.api.ExecutorType;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.permissions.util.PermissionUtil;
import de.klaro.reformcloud2.permissions.util.group.PermissionGroup;
import de.klaro.reformcloud2.permissions.util.permission.PermissionNode;
import de.klaro.reformcloud2.permissions.util.user.PermissionUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPermissionUtil implements PermissionUtil {

    private static final String PERMISSION_GROUP_TABLE = "reformcloud_internal_db_perm_group";

    private static final String PERMISSION_PLAYER_TABLE = "reformcloud_internal_db_perm_player";

    private static final Map<String, PermissionGroup> CACHE = new ConcurrentHashMap<>();

    private static final Map<UUID, PermissionUser> USER_CACHE = new ConcurrentHashMap<>();

    private DefaultPermissionUtil() {}

    public static PermissionUtil doLoad() {
        ExecutorAPI.getInstance().createDatabase(PERMISSION_GROUP_TABLE);
        return new DefaultPermissionUtil();
    }

    @Override
    public PermissionGroup getGroup(String name) {
        if (CACHE.containsKey(name)) {
            return CACHE.get(name);
        }

        if (!ExecutorAPI.getInstance().contains(PERMISSION_GROUP_TABLE, name)) {
            return null;
        }

        return CACHE.put(name, ExecutorAPI.getInstance().find(
                PERMISSION_GROUP_TABLE,
                name,
                null,
                e -> e.get("group", PermissionGroup.TYPE)
        ));
    }

    @Override
    public void updateGroup(PermissionGroup permissionGroup) {
        CACHE.put(permissionGroup.getName(), permissionGroup);

        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)) {
            ExecutorAPI.getInstance().update(PERMISSION_GROUP_TABLE, permissionGroup.getName(),
                    new JsonConfiguration().add("group", permissionGroup));
            // Notify packet
        } else {
            // Send packet to controller
        }
    }

    @Override
    public void addGroupPermission(PermissionGroup permissionGroup, PermissionNode permissionNode) {
        permissionGroup.getPermissionNodes().add(permissionNode);
        updateGroup(permissionGroup);
    }

    @Override
    public void addProcessGroupPermission(String processGroup, PermissionGroup permissionGroup, PermissionNode permissionNode) {
        final Collection<PermissionNode> current = permissionGroup.getPerGroupPermissions()
                .putIfAbsent(processGroup, new ArrayList<>());
        if (current != null) {
            current.add(permissionNode);
            updateGroup(permissionGroup);
        }
    }

    @Override
    public PermissionGroup createGroup(String name) {
        final PermissionGroup permissionGroup = getGroup(name);
        if (permissionGroup != null) {
            return permissionGroup;
        }

        final PermissionGroup newGroup = new PermissionGroup(
                new ArrayList<>(),
                new ConcurrentHashMap<>(),
                new ArrayList<>(),
                name,
                0,
                -1
        );
        ExecutorAPI.getInstance().insert(PERMISSION_GROUP_TABLE, name, null, new JsonConfiguration().add(
                "group", newGroup
        ));
        return CACHE.put(name, newGroup);
    }

    @Override
    public void deleteGroup(String name) {
        if (getGroup(name) != null) {
            ExecutorAPI.getInstance().remove(PERMISSION_GROUP_TABLE, name);
            CACHE.remove(name);
        }
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
        if (USER_CACHE.containsKey(uuid)) {
            return USER_CACHE.get(uuid);
        }

        if (!ExecutorAPI.getInstance().contains(PERMISSION_PLAYER_TABLE, uuid.toString())) {
            final PermissionUser user = new PermissionUser(uuid, new ArrayList<>(), new ArrayList<>());
            ExecutorAPI.getInstance().insert(PERMISSION_PLAYER_TABLE, uuid.toString(), null, new JsonConfiguration()
                    .add("user", user)
            );
            return USER_CACHE.put(uuid, user);
        }


        final PermissionUser result = ExecutorAPI.getInstance().find(
                PERMISSION_PLAYER_TABLE,
                uuid.toString(),
                null,
                e -> e.get("user", PermissionUser.TYPE)
        );
        return USER_CACHE.put(uuid, result);
    }

    @Override
    public void addUserPermission(UUID uuid, PermissionNode permissionNode) {
        final PermissionUser user = loadUser(uuid);
        if (user != null) {
            user.getPermissionNodes().add(permissionNode);
            updateUser(user);
        }
    }

    @Override
    public void updateUser(PermissionUser permissionUser) {
        USER_CACHE.put(permissionUser.getUuid(), permissionUser);

        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)) {
            ExecutorAPI.getInstance().update(PERMISSION_PLAYER_TABLE, permissionUser.getUuid().toString(),
                    new JsonConfiguration().add("user", permissionUser));
            // Notify packet
        } else {
            // Send packet to controller
        }
    }

    @Override
    public void deleteUser(UUID uuid) {
        if (loadUser(uuid) != null) {
            ExecutorAPI.getInstance().remove(PERMISSION_PLAYER_TABLE, uuid.toString());
            USER_CACHE.remove(uuid);
        }
    }

    @Override
    public void handleDisconnect(UUID uuid) {
        USER_CACHE.remove(uuid);
    }

    private boolean hasPermission(PermissionGroup group, String perm) {
        return group.hasPermission(perm);
    }
}