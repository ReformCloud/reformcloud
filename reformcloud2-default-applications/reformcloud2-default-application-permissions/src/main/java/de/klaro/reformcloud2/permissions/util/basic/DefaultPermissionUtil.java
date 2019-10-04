package de.klaro.reformcloud2.permissions.util.basic;

import de.klaro.reformcloud2.executor.api.ExecutorType;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalEventBusHandler;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.permissions.packets.api.out.APIPacketOutGroupAction;
import de.klaro.reformcloud2.permissions.packets.controller.out.ControllerPacketOutGroupAction;
import de.klaro.reformcloud2.permissions.packets.controller.out.ControllerPacketOutUserAction;
import de.klaro.reformcloud2.permissions.packets.util.PermissionAction;
import de.klaro.reformcloud2.permissions.util.PermissionUtil;
import de.klaro.reformcloud2.permissions.util.events.group.PermissionGroupCreateEvent;
import de.klaro.reformcloud2.permissions.util.events.group.PermissionGroupDeleteEvent;
import de.klaro.reformcloud2.permissions.util.events.group.PermissionGroupUpdateEvent;
import de.klaro.reformcloud2.permissions.util.events.user.PermissionUserCreateEvent;
import de.klaro.reformcloud2.permissions.util.events.user.PermissionUserDeleteEvent;
import de.klaro.reformcloud2.permissions.util.events.user.PermissionUserUpdateEvent;
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
            DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new ControllerPacketOutGroupAction(permissionGroup, PermissionAction.UPDATE)));
        } else {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(new APIPacketOutGroupAction(permissionGroup, PermissionAction.UPDATE)));
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
        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)) {
            ExecutorAPI.getInstance().insert(PERMISSION_GROUP_TABLE, name, null, new JsonConfiguration().add(
                    "group", newGroup
            ));
            DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new ControllerPacketOutGroupAction(newGroup, PermissionAction.CREATE)));
        } else {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(new APIPacketOutGroupAction(newGroup, PermissionAction.CREATE)));
        }

        return CACHE.put(name, newGroup);
    }

    @Override
    public void deleteGroup(String name) {
        final PermissionGroup toDelete = getGroup(name);
        if (toDelete != null) {
            if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)) {
                ExecutorAPI.getInstance().remove(PERMISSION_GROUP_TABLE, name);
                DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new ControllerPacketOutGroupAction(toDelete, PermissionAction.DELETE)));
            } else {
                DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(new APIPacketOutGroupAction(toDelete, PermissionAction.DELETE)));
            }
        }

        CACHE.remove(name);
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
            if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)) {
                ExecutorAPI.getInstance().insert(PERMISSION_PLAYER_TABLE, uuid.toString(), null, new JsonConfiguration()
                        .add("user", user)
                );
                DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new ControllerPacketOutUserAction(user, PermissionAction.CREATE)));
            } else {
                DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(new ControllerPacketOutUserAction(user, PermissionAction.CREATE)));
            }

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
            DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new ControllerPacketOutUserAction(permissionUser, PermissionAction.UPDATE)));
        } else {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(new ControllerPacketOutUserAction(permissionUser, PermissionAction.UPDATE)));
        }
    }

    @Override
    public void deleteUser(UUID uuid) {
        final PermissionUser user = loadUser(uuid);
        if (user != null) {
            if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)) {
                ExecutorAPI.getInstance().remove(PERMISSION_PLAYER_TABLE, uuid.toString());
                DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new ControllerPacketOutUserAction(user, PermissionAction.DELETE)));
            } else {
                DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(new ControllerPacketOutUserAction(user, PermissionAction.DELETE)));
            }

            USER_CACHE.remove(uuid);
        }
    }

    @Override
    public void handleDisconnect(UUID uuid) {
        USER_CACHE.remove(uuid);
    }

    @Override
    public void handleInternalPermissionGroupUpdate(PermissionGroup permissionGroup) {
        if (CACHE.containsKey(permissionGroup.getName())) {
            CACHE.put(permissionGroup.getName(), permissionGroup);
        }

        ExternalEventBusHandler.getInstance().callEvent(new PermissionGroupUpdateEvent(permissionGroup));
    }

    @Override
    public void handleInternalPermissionGroupCreate(PermissionGroup permissionGroup) {
        ExternalEventBusHandler.getInstance().callEvent(new PermissionGroupCreateEvent(permissionGroup));
    }

    @Override
    public void handleInternalPermissionGroupDelete(PermissionGroup permissionGroup) {
        CACHE.remove(permissionGroup.getName());
        ExternalEventBusHandler.getInstance().callEvent(new PermissionGroupDeleteEvent(permissionGroup.getName()));
    }

    @Override
    public void handleInternalUserUpdate(PermissionUser permissionUser) {
        if (USER_CACHE.containsKey(permissionUser.getUuid())) {
            USER_CACHE.put(permissionUser.getUuid(), permissionUser);
        }

        ExternalEventBusHandler.getInstance().callEvent(new PermissionUserUpdateEvent(permissionUser));
    }

    @Override
    public void handleInternalUserCreate(PermissionUser permissionUser) {
        ExternalEventBusHandler.getInstance().callEvent(new PermissionUserCreateEvent(permissionUser));
    }

    @Override
    public void handleInternalUserDelete(PermissionUser permissionUser) {
        USER_CACHE.remove(permissionUser.getUuid());
        ExternalEventBusHandler.getInstance().callEvent(new PermissionUserDeleteEvent(permissionUser.getUuid()));
    }

    private boolean hasPermission(PermissionGroup group, String perm) {
        return group.hasPermission(perm);
    }
}