/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.permissions.util.basic;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.permissions.packets.PacketGroupAction;
import systems.reformcloud.reformcloud2.permissions.packets.PacketUserAction;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;
import systems.reformcloud.reformcloud2.permissions.util.PermissionUtil;
import systems.reformcloud.reformcloud2.permissions.util.events.group.PermissionDefaultGroupsChangedEvent;
import systems.reformcloud.reformcloud2.permissions.util.events.group.PermissionGroupCreateEvent;
import systems.reformcloud.reformcloud2.permissions.util.events.group.PermissionGroupDeleteEvent;
import systems.reformcloud.reformcloud2.permissions.util.events.group.PermissionGroupUpdateEvent;
import systems.reformcloud.reformcloud2.permissions.util.events.user.PermissionUserCreateEvent;
import systems.reformcloud.reformcloud2.permissions.util.events.user.PermissionUserDeleteEvent;
import systems.reformcloud.reformcloud2.permissions.util.events.user.PermissionUserUpdateEvent;
import systems.reformcloud.reformcloud2.permissions.util.group.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultPermissionUtil implements PermissionUtil {

    public static final String PERMISSION_GROUP_TABLE = "reformcloud_internal_db_perm_group";

    public static final String PERMISSION_PLAYER_TABLE = "reformcloud_internal_db_perm_player";

    private static final String PERMISSION_CONFIG_TABLE = "reformcloud_internal_db_perm_config";

    public static final String PERMISSION_NAME_TO_UNIQUE_ID_TABLE = "reformcloud_internal_db_perm_name_uuid";

    private static final Map<String, PermissionGroup> CACHE = new ConcurrentHashMap<>();

    private static final Map<UUID, PermissionUser> USER_CACHE = new ConcurrentHashMap<>();

    private static final Collection<PermissionGroup> CACHED_DEFAULT_GROUPS = new LinkedList<>();

    private DefaultPermissionUtil() {
        loadDefaultGroups();
    }

    public static PermissionUtil hello() {
        ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().createDatabase(PERMISSION_GROUP_TABLE);
        ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().createDatabase(PERMISSION_PLAYER_TABLE);
        ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().createDatabase(PERMISSION_CONFIG_TABLE);
        ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().createDatabase(PERMISSION_NAME_TO_UNIQUE_ID_TABLE);

        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)
                || ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)) {
            if (!ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().contains(PERMISSION_CONFIG_TABLE, "config")) {
                ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().insert(
                        PERMISSION_CONFIG_TABLE,
                        "config",
                        null,
                        new JsonConfiguration().add("defaultGroups", Collections.emptyList())
                );
            }
        }

        return new DefaultPermissionUtil();
    }

    @Override
    public PermissionGroup getGroup(@NotNull String name) {
        if (CACHE.containsKey(name)) {
            return CACHE.get(name);
        }

        if (!ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().contains(PERMISSION_GROUP_TABLE, name)) {
            return null;
        }

        PermissionGroup group = ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().find(
                PERMISSION_GROUP_TABLE,
                name,
                null,
                e -> e.get("group", PermissionGroup.TYPE)
        );
        if (group == null) {
            return null;
        }

        eraseGroupCache(group);
        CACHE.put(name, group);
        return group;
    }

    @Override
    public void updateGroup(@NotNull PermissionGroup permissionGroup) {
        CACHE.put(permissionGroup.getName(), permissionGroup);

        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)
                || ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)) {
            ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().update(PERMISSION_GROUP_TABLE, permissionGroup.getName(),
                    new JsonConfiguration().add("group", permissionGroup));
        }

        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(new PacketGroupAction(permissionGroup, PermissionAction.UPDATE)));
    }

    @Override
    public void addGroupPermission(@NotNull PermissionGroup permissionGroup, @NotNull PermissionNode permissionNode) {
        permissionGroup.getPermissionNodes().add(permissionNode);
        updateGroup(permissionGroup);
    }

    @Override
    public void addProcessGroupPermission(@NotNull String processGroup, @NotNull PermissionGroup permissionGroup, @NotNull PermissionNode permissionNode) {
        final Collection<PermissionNode> current = permissionGroup.getPerGroupPermissions().get(processGroup);
        if (current == null) {
            permissionGroup.getPerGroupPermissions().put(processGroup, new ArrayList<>(Collections.singletonList(permissionNode)));
            updateGroup(permissionGroup);
            return;
        }

        current.add(permissionNode);
        updateGroup(permissionGroup);
    }

    @Override
    public void addDefaultGroup(@NotNull String group) {
        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)
                || ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)) {
            JsonConfiguration config = ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().find(PERMISSION_CONFIG_TABLE, "config", null);
            if (config == null) {
                return;
            }

            Collection<String> defaultGroups = config.get("defaultGroups", new TypeToken<Collection<String>>() {
            });
            if (defaultGroups == null) {
                return;
            }

            if (defaultGroups.contains(group)) {
                return;
            }

            defaultGroups.add(group);
            config.add("defaultGroups", defaultGroups);
            ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().update(PERMISSION_CONFIG_TABLE, "config", config);
            DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketGroupAction(null, PermissionAction.DEFAULT_GROUPS_CHANGED)));
        } else {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(new PacketGroupAction(new PermissionGroup(
                    new ArrayList<>(),
                    new HashMap<>(),
                    new ArrayList<>(),
                    group,
                    -1),
                    PermissionAction.DEFAULT_GROUPS_CHANGED)
            ));
        }
    }

    @Override
    public void removeDefaultGroup(@NotNull String group) {
        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)
                || ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)) {
            JsonConfiguration config = ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().find(PERMISSION_CONFIG_TABLE, "config", null);
            if (config == null) {
                return;
            }

            Collection<String> defaultGroups = config.get("defaultGroups", new TypeToken<Collection<String>>() {
            });
            if (defaultGroups == null) {
                return;
            }

            if (!defaultGroups.contains(group)) {
                return;
            }

            defaultGroups.remove(group);
            config.add("defaultGroups", defaultGroups);
            ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().update(PERMISSION_CONFIG_TABLE, "config", config);
            DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketGroupAction(null, PermissionAction.DEFAULT_GROUPS_CHANGED)));
        } else {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(new PacketGroupAction(new PermissionGroup(
                    new ArrayList<>(),
                    new HashMap<>(),
                    new ArrayList<>(),
                    group,
                    -1),
                    PermissionAction.DEFAULT_GROUPS_CHANGED
            )));
        }
    }

    @NotNull
    @Override
    public PermissionGroup createGroup(@NotNull String name) {
        final PermissionGroup permissionGroup = getGroup(name);
        if (permissionGroup != null) {
            return permissionGroup;
        }

        final PermissionGroup newGroup = new PermissionGroup(
                new ArrayList<>(),
                new ConcurrentHashMap<>(),
                new ArrayList<>(),
                name,
                0
        );
        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)
                || ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)) {
            ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().insert(PERMISSION_GROUP_TABLE, name, null, new JsonConfiguration().add(
                    "group", newGroup
            ));
        }

        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(new PacketGroupAction(newGroup, PermissionAction.CREATE)));
        CACHE.put(name, newGroup);

        return newGroup;
    }

    @NotNull
    @Override
    public Collection<PermissionGroup> getDefaultGroups() {
        return Collections.unmodifiableCollection(CACHED_DEFAULT_GROUPS);
    }

    @Override
    public void deleteGroup(@NotNull String name) {
        final PermissionGroup toDelete = getGroup(name);
        if (toDelete != null) {
            if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)
                    || ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)) {
                ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().remove(PERMISSION_GROUP_TABLE, name);
            }
        }

        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(new PacketGroupAction(toDelete, PermissionAction.DELETE)));
        CACHE.remove(name);
    }

    @Override
    public boolean hasPermission(@NotNull PermissionUser permissionUser, @NotNull String permission) {
        permission = permission.toLowerCase();
        for (NodeGroup group : permissionUser.getGroups()) {
            if (!group.isValid()) {
                continue;
            }

            final PermissionGroup permissionGroup = getGroup(group.getGroupName());
            if (permissionGroup == null) {
                continue;
            }

            if (hasPermission(permissionGroup, permission)) {
                return true;
            }
        }

        return false;
    }

    @NotNull
    @Override
    public PermissionUser loadUser(@NotNull UUID uuid) {
        if (USER_CACHE.containsKey(uuid)) {
            return USER_CACHE.get(uuid);
        }

        if (!ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().contains(PERMISSION_PLAYER_TABLE, uuid.toString())) {
            final PermissionUser user = new PermissionUser(uuid, new ArrayList<>(), new ArrayList<>());
            if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)
                    || ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)) {
                ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().insert(PERMISSION_PLAYER_TABLE, uuid.toString(), null, new JsonConfiguration()
                        .add("user", user)
                );
            }

            DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketUserAction(user, PermissionAction.CREATE)));
            USER_CACHE.put(uuid, user);

            return user;
        }

        final PermissionUser result = ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().find(
                PERMISSION_PLAYER_TABLE,
                uuid.toString(),
                null,
                e -> e.get("user", PermissionUser.TYPE)
        );
        if (result == null) {
            return new PermissionUser(uuid, new ArrayList<>(), new ArrayList<>());
        }

        eraseUserCache(result);
        USER_CACHE.put(uuid, result);
        return result;
    }

    @NotNull
    @Override
    public PermissionUser loadUser(@NotNull UUID uuid, @Nullable String name) {
        if (name != null) {
            pushToDB(uuid, name);
        }

        return loadUser(uuid);
    }

    @Override
    public void addUserPermission(@NotNull UUID uuid, @NotNull PermissionNode permissionNode) {
        final PermissionUser user = loadUser(uuid);
        user.getPermissionNodes().add(permissionNode);
        updateUser(user);
    }

    @Override
    public void removeUserGroup(@NotNull UUID uuid, @NotNull String group) {
        final PermissionUser user = loadUser(uuid);
        Streams.filterToReference(user.getGroups(), e -> e.getGroupName().equals(group)).ifPresent(e -> {
            user.getGroups().remove(e);
            updateUser(user);
        });
    }

    @Override
    public void addUserGroup(@NotNull UUID uuid, @NotNull NodeGroup group) {
        final PermissionUser user = loadUser(uuid);
        user.getGroups().add(group);
        updateUser(user);
    }

    @Override
    public void updateUser(@NotNull PermissionUser permissionUser) {
        USER_CACHE.put(permissionUser.getUniqueID(), permissionUser);

        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)
                || ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)) {
            ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().update(PERMISSION_PLAYER_TABLE, permissionUser.getUniqueID().toString(),
                    new JsonConfiguration().add("user", permissionUser));
        }

        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> e.sendPacket(new PacketUserAction(permissionUser, PermissionAction.UPDATE)));
    }

    @Override
    public void deleteUser(@NotNull UUID uuid) {
        final PermissionUser user = loadUser(uuid);
        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)
                || ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE)) {
            ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().remove(PERMISSION_PLAYER_TABLE, uuid.toString());
        }

        DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketUserAction(user, PermissionAction.DELETE)));
        USER_CACHE.remove(uuid);
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

        ExecutorAPI.getInstance().getEventManager().callEvent(new PermissionGroupUpdateEvent(permissionGroup));
    }

    @Override
    public void handleInternalPermissionGroupCreate(PermissionGroup permissionGroup) {
        ExecutorAPI.getInstance().getEventManager().callEvent(new PermissionGroupCreateEvent(permissionGroup));
    }

    @Override
    public void handleInternalPermissionGroupDelete(PermissionGroup permissionGroup) {
        CACHE.remove(permissionGroup.getName());
        Streams.filterToReference(CACHED_DEFAULT_GROUPS, e -> e.getName().equals(permissionGroup.getName()))
                .ifPresent(CACHED_DEFAULT_GROUPS::remove);
        ExecutorAPI.getInstance().getEventManager().callEvent(new PermissionGroupDeleteEvent(permissionGroup.getName()));
    }

    @Override
    public void handleInternalUserUpdate(PermissionUser permissionUser) {
        if (USER_CACHE.containsKey(permissionUser.getUniqueID())) {
            USER_CACHE.put(permissionUser.getUniqueID(), permissionUser);
        }

        ExecutorAPI.getInstance().getEventManager().callEvent(new PermissionUserUpdateEvent(permissionUser));
    }

    @Override
    public void handleInternalUserCreate(PermissionUser permissionUser) {
        ExecutorAPI.getInstance().getEventManager().callEvent(new PermissionUserCreateEvent(permissionUser));
    }

    @Override
    public void handleInternalUserDelete(PermissionUser permissionUser) {
        USER_CACHE.remove(permissionUser.getUniqueID());
        ExecutorAPI.getInstance().getEventManager().callEvent(new PermissionUserDeleteEvent(permissionUser.getUniqueID()));
    }

    @Override
    public void handleInternalDefaultGroupsUpdate() {
        loadDefaultGroups();
        ExecutorAPI.getInstance().getEventManager().callEvent(new PermissionDefaultGroupsChangedEvent());
    }

    @Override
    public boolean hasPermission(@NotNull PermissionGroup group, @NotNull String perm) {
        if (group.hasPermission(perm)) {
            return true;
        }

        for (String subGroup : group.getSubGroups()) {
            PermissionGroup sub = getGroup(subGroup);
            if (sub == null) {
                continue;
            }

            if (sub.hasPermission(perm)) {
                return true;
            }
        }

        return false;
    }

    private void eraseUserCache(PermissionUser permissionUser) {
        Streams.allOf(permissionUser.getGroups(), e -> !e.isValid()).forEach(permissionUser.getGroups()::remove);
        Streams.allOf(permissionUser.getPermissionNodes(), e -> !e.isValid()).forEach(permissionUser.getPermissionNodes()::remove);
        updateUser(permissionUser);
    }

    private void eraseGroupCache(PermissionGroup permissionGroup) {
        Streams.allOf(permissionGroup.getPermissionNodes(), e -> !e.isValid()).forEach(permissionGroup.getPermissionNodes()::remove);
        permissionGroup.getPerGroupPermissions().forEach((k, v) -> Streams.allOf(v, e -> !e.isValid()).forEach(v::remove));
        updateGroup(permissionGroup);
    }

    private synchronized void loadDefaultGroups() {
        JsonConfiguration config = ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().find(PERMISSION_CONFIG_TABLE, "config", null);
        if (config == null) {
            return;
        }

        if (!config.has("defaultGroups")) {
            return;
        }

        Collection<String> defaultGroups = config.get("defaultGroups", new TypeToken<Collection<String>>() {
        });
        if (defaultGroups == null) {
            return;
        }

        defaultGroups.forEach(e -> {
            PermissionGroup group = getGroup(e);
            if (group == null) {
                return;
            }

            CACHED_DEFAULT_GROUPS.add(group);
        });
    }

    private void pushToDB(UUID uuid, String name) {
        if (ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().contains(PERMISSION_NAME_TO_UNIQUE_ID_TABLE, name)) {
            ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().update(PERMISSION_NAME_TO_UNIQUE_ID_TABLE, name, new JsonConfiguration().add("id", uuid));
        } else {
            ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().insert(
                    PERMISSION_NAME_TO_UNIQUE_ID_TABLE,
                    name,
                    uuid.toString(),
                    new JsonConfiguration().add("id", uuid)
            );
        }
    }
}
