/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.permissions.defaults;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.wrappers.DatabaseTableWrapper;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.events.group.PermissionGroupCreateEvent;
import systems.reformcloud.reformcloud2.permissions.events.group.PermissionGroupDeleteEvent;
import systems.reformcloud.reformcloud2.permissions.events.group.PermissionGroupUpdateEvent;
import systems.reformcloud.reformcloud2.permissions.events.user.PermissionUserCreateEvent;
import systems.reformcloud.reformcloud2.permissions.events.user.PermissionUserDeleteEvent;
import systems.reformcloud.reformcloud2.permissions.events.user.PermissionUserUpdateEvent;
import systems.reformcloud.reformcloud2.permissions.internal.UUIDFetcher;
import systems.reformcloud.reformcloud2.permissions.nodes.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.nodes.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;
import systems.reformcloud.reformcloud2.permissions.packets.PacketGroupAction;
import systems.reformcloud.reformcloud2.permissions.packets.PacketUserAction;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPermissionManagement extends PermissionManagement {

    public static final String PERMISSION_GROUP_TABLE = "reformcloud_internal_db_perm_group";
    public static final String PERMISSION_PLAYER_TABLE = "reformcloud_internal_db_perm_player";
    public static final String PERMISSION_NAME_TO_UNIQUE_ID_TABLE = "reformcloud_internal_db_perm_name_uuid";

    private static final boolean NODE = ExecutorAPI.getInstance().getType().equals(ExecutorType.NODE);

    private final Map<String, PermissionGroup> nameToGroupCache = new ConcurrentHashMap<>();
    private final Map<UUID, PermissionUser> uniqueIdToUserCache = new ConcurrentHashMap<>();

    private final DatabaseTableWrapper permissionGroupTable;
    private final DatabaseTableWrapper permissionUserTable;
    private final DatabaseTableWrapper nameToUniqueIdDatabase;

    public DefaultPermissionManagement() {
        this.permissionGroupTable = ExecutorAPI.getInstance().getDatabaseProvider().createTable(PERMISSION_GROUP_TABLE);
        this.permissionUserTable = ExecutorAPI.getInstance().getDatabaseProvider().createTable(PERMISSION_PLAYER_TABLE);
        this.nameToUniqueIdDatabase = ExecutorAPI.getInstance().getDatabaseProvider().createTable(PERMISSION_NAME_TO_UNIQUE_ID_TABLE);

        for (JsonConfiguration configuration : this.permissionGroupTable.getAll()) {
            PermissionGroup group = configuration.get("group", PermissionGroup.TYPE);
            if (group != null) {
                this.eraseGroupCache(group);
                this.nameToGroupCache.put(group.getName(), group);
            }
        }
    }

    @Override
    public @NotNull Optional<PermissionGroup> getPermissionGroup(@NotNull String name) {
        return Optional.ofNullable(this.nameToGroupCache.get(name));
    }

    @Override
    public void updateGroup(@NotNull PermissionGroup permissionGroup) {
        this.nameToGroupCache.put(permissionGroup.getName(), permissionGroup);

        if (NODE) {
            this.permissionGroupTable.update(permissionGroup.getName(), "", new JsonConfiguration().add("group", permissionGroup));
        }

        this.publish(new PacketGroupAction(permissionGroup, PermissionAction.UPDATE));
    }

    @Override
    public void addGroupPermission(@NotNull PermissionGroup permissionGroup, @NotNull PermissionNode permissionNode) {
        permissionGroup.getPermissionNodes().add(permissionNode);
        this.updateGroup(permissionGroup);
    }

    @Override
    public void addProcessGroupPermission(@NotNull String processGroup, @NotNull PermissionGroup permissionGroup, @NotNull PermissionNode permissionNode) {
        Collection<PermissionNode> current = permissionGroup.getPerGroupPermissions().get(processGroup);
        if (current == null) {
            permissionGroup.getPerGroupPermissions().put(processGroup, new ArrayList<>(Collections.singletonList(permissionNode)));
            this.updateGroup(permissionGroup);
            return;
        }

        current.add(permissionNode);
        this.updateGroup(permissionGroup);
    }

    @NotNull
    @Override
    @Deprecated
    public PermissionGroup createGroup(@NotNull String name) {
        PermissionGroup newGroup = new PermissionGroup(
            new ArrayList<>(),
            new ConcurrentHashMap<>(),
            new ArrayList<>(),
            name,
            0
        );
        return this.createPermissionGroup(newGroup);
    }

    @Override
    public @NotNull PermissionGroup createPermissionGroup(@NotNull PermissionGroup permissionGroup) {
        PermissionGroup group = this.getPermissionGroup(permissionGroup.getName()).orElse(null);
        if (group != null) {
            return group;
        }

        if (NODE) {
            this.permissionGroupTable.insert(permissionGroup.getName(), "", new JsonConfiguration().add("group", permissionGroup));
        }

        this.publish(new PacketGroupAction(permissionGroup, PermissionAction.CREATE));
        this.nameToGroupCache.put(permissionGroup.getName(), permissionGroup);

        return permissionGroup;
    }

    @NotNull
    @Override
    public Collection<PermissionGroup> getDefaultGroups() {
        return Streams.allOf(this.nameToGroupCache.values(), PermissionGroup::isDefaultGroup);
    }

    @Override
    public @NotNull @UnmodifiableView Collection<PermissionGroup> getPermissionGroups() {
        return Collections.unmodifiableCollection(this.nameToGroupCache.values());
    }

    @Override
    public void deleteGroup(@NotNull String name) {
        this.getPermissionGroup(name).ifPresent(permissionGroup -> {
            if (NODE) {
                this.permissionGroupTable.remove(permissionGroup.getName(), "");
            }

            this.publish(new PacketGroupAction(permissionGroup, PermissionAction.DELETE));
            this.nameToGroupCache.remove(name);
        });
    }

    @Override
    public boolean hasPermission(@NotNull PermissionUser permissionUser, @NotNull String permission) {
        permission = permission.toLowerCase();
        for (NodeGroup group : permissionUser.getGroups()) {
            if (!group.isValid()) {
                continue;
            }

            final PermissionGroup permissionGroup = this.getPermissionGroup(group.getGroupName()).orElse(null);
            if (permissionGroup == null) {
                continue;
            }

            Boolean hasPermission = this.hasPermission0(permissionGroup, permission);
            if (hasPermission != null) {
                return hasPermission;
            }
        }

        return false;
    }

    @NotNull
    @Override
    public PermissionUser loadUser(@NotNull UUID uuid) {
        PermissionUser permissionUser = this.uniqueIdToUserCache.get(uuid);
        return permissionUser == null ? this.loadUser0(uuid) : permissionUser;
    }

    @Override
    public @NotNull Optional<PermissionUser> getFirstExistingUser(@NotNull String name) {
        UUID uniqueID = UUIDFetcher.getUUIDFromName(name);
        if (uniqueID == null) {
            return Optional.empty();
        }

        return this.getExistingUser(uniqueID);
    }

    @Override
    public @NotNull Optional<PermissionUser> loadUser(@NotNull String name) {
        UUID uniqueID = UUIDFetcher.getUUIDFromName(name);
        if (uniqueID == null) {
            return Optional.empty();
        }

        return Optional.of(this.loadUser(uniqueID));
    }

    @Override
    public @NotNull Optional<PermissionUser> getExistingUser(@NotNull UUID uniqueId) {
        PermissionUser permissionUser = this.uniqueIdToUserCache.get(uniqueId);
        if (permissionUser != null) {
            return Optional.of(permissionUser);
        }

        Optional<JsonConfiguration> configuration = this.permissionUserTable.get(uniqueId.toString(), "");
        if (!configuration.isPresent()) {
            return Optional.empty();
        }

        permissionUser = configuration.get().get("user", PermissionUser.TYPE);
        if (permissionUser == null) {
            return Optional.empty();
        }

        this.uniqueIdToUserCache.put(permissionUser.getUniqueID(), permissionUser);
        this.eraseUserCache(permissionUser);
        return Optional.of(permissionUser);
    }

    private @NotNull PermissionUser loadUser0(@NotNull UUID uuid) {
        return this.getExistingUser(uuid).orElseGet(() -> this.createPermissionUser(uuid));
    }

    @NotNull
    private PermissionUser createPermissionUser(@NotNull UUID uniqueID) {
        PermissionUser permissionUser = new PermissionUser(uniqueID, new ArrayList<>(), new ArrayList<>());

        this.permissionUserTable.insert(uniqueID.toString(), "", new JsonConfiguration().add("user", permissionUser));
        this.publish(new PacketUserAction(permissionUser, PermissionAction.CREATE));
        this.uniqueIdToUserCache.put(uniqueID, permissionUser);

        return permissionUser;
    }

    @NotNull
    @Override
    public PermissionUser loadUser(@NotNull UUID uuid, @Nullable String name) {
        if (name != null) {
            this.pushToDB(uuid, name);
        }

        return this.loadUser(uuid);
    }

    @Override
    public void assignDefaultGroups(@NotNull UUID uniqueId) {
        this.getExistingUser(uniqueId).ifPresent(this::assignDefaultGroups);
    }

    @Override
    public void assignDefaultGroups(@NotNull PermissionUser permissionUser) {
        boolean hasChanged = false;
        for (PermissionGroup defaultGroup : this.getDefaultGroups()) {
            if (permissionUser.getGroups().stream().noneMatch(group -> group.getGroupName().equals(defaultGroup.getName()))) {
                permissionUser.getGroups().add(new NodeGroup(System.currentTimeMillis(), -1, defaultGroup.getName()));
                hasChanged = true;
            }
        }

        if (hasChanged) {
            this.updateUser(permissionUser);
        }
    }

    @Override
    public void addUserPermission(@NotNull UUID uuid, @NotNull PermissionNode permissionNode) {
        PermissionUser user = this.loadUser(uuid);
        user.getPermissionNodes().add(permissionNode);
        this.updateUser(user);
    }

    @Override
    public void removeUserGroup(@NotNull UUID uuid, @NotNull String group) {
        PermissionUser user = this.loadUser(uuid);
        Streams.filterToReference(user.getGroups(), e -> e.getGroupName().equals(group)).ifPresent(e -> {
            user.getGroups().remove(e);
            this.updateUser(user);
        });
    }

    @Override
    public void addUserGroup(@NotNull UUID uuid, @NotNull NodeGroup group) {
        PermissionUser user = this.loadUser(uuid);
        user.getGroups().add(group);
        this.updateUser(user);
    }

    @Override
    public void updateUser(@NotNull PermissionUser permissionUser) {
        this.uniqueIdToUserCache.put(permissionUser.getUniqueID(), permissionUser);

        this.permissionUserTable.update(permissionUser.getUniqueID().toString(), "", new JsonConfiguration().add("user", permissionUser));
        this.publish(new PacketUserAction(permissionUser, PermissionAction.UPDATE));
    }

    @Override
    public void deleteUser(@NotNull UUID uuid) {
        PermissionUser user = this.loadUser(uuid);
        this.permissionUserTable.remove(uuid.toString(), "");
        this.publish(new PacketUserAction(user, PermissionAction.DELETE));
        this.uniqueIdToUserCache.remove(uuid);
    }

    @Override
    public void handleDisconnect(UUID uuid) {
        this.uniqueIdToUserCache.remove(uuid);
    }

    @Override
    public void handleInternalPermissionGroupUpdate(PermissionGroup permissionGroup) {
        this.nameToGroupCache.put(permissionGroup.getName(), permissionGroup);
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PermissionGroupUpdateEvent(permissionGroup));
    }

    @Override
    public void handleInternalPermissionGroupCreate(PermissionGroup permissionGroup) {
        this.nameToGroupCache.put(permissionGroup.getName(), permissionGroup);
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PermissionGroupCreateEvent(permissionGroup));
    }

    @Override
    public void handleInternalPermissionGroupDelete(PermissionGroup permissionGroup) {
        this.nameToGroupCache.remove(permissionGroup.getName());
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PermissionGroupDeleteEvent(permissionGroup.getName()));
    }

    @Override
    public void handleInternalUserUpdate(PermissionUser permissionUser) {
        if (this.uniqueIdToUserCache.containsKey(permissionUser.getUniqueID())) {
            this.uniqueIdToUserCache.put(permissionUser.getUniqueID(), permissionUser);
        }

        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PermissionUserUpdateEvent(permissionUser));
    }

    @Override
    public void handleInternalUserCreate(PermissionUser permissionUser) {
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PermissionUserCreateEvent(permissionUser));
    }

    @Override
    public void handleInternalUserDelete(PermissionUser permissionUser) {
        this.uniqueIdToUserCache.remove(permissionUser.getUniqueID());
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PermissionUserDeleteEvent(permissionUser.getUniqueID()));
    }

    @Override
    public boolean hasPermission(@NotNull PermissionGroup group, @NotNull String perm) {
        Boolean hasPermission = this.hasPermission0(group, perm.toLowerCase());
        return hasPermission != null && hasPermission;
    }

    @Nullable
    private Boolean hasPermission0(@NotNull PermissionGroup group, @NotNull String perm) {
        Boolean hasSubPermission = group.hasPermission(perm);
        if (hasSubPermission != null) {
            return hasSubPermission;
        }

        for (String subGroup : group.getSubGroups()) {
            PermissionGroup sub = this.getPermissionGroup(subGroup).orElse(null);
            if (sub == null) {
                continue;
            }

            hasSubPermission = this.hasPermission0(sub, perm);
            if (hasSubPermission != null) {
                return hasSubPermission;
            }
        }

        return null;
    }

    private void eraseUserCache(@NotNull PermissionUser permissionUser) {
        boolean hasChanged = permissionUser.getGroups().removeIf(group -> !group.isValid())
            || permissionUser.getPermissionNodes().removeIf(permissionNode -> !permissionNode.isValid());
        for (Map.Entry<String, Collection<PermissionNode>> stringCollectionEntry : permissionUser.getPerGroupPermissions().entrySet()) {
            hasChanged = stringCollectionEntry.getValue().removeIf(permissionNode -> !permissionNode.isValid());
        }

        if (hasChanged) {
            this.updateUser(permissionUser);
        }
    }

    private void eraseGroupCache(@NotNull PermissionGroup permissionGroup) {
        boolean hasChanged = permissionGroup.getPermissionNodes().removeIf(permissionNode -> !permissionNode.isValid());
        for (Map.Entry<String, Collection<PermissionNode>> stringCollectionEntry : permissionGroup.getPerGroupPermissions().entrySet()) {
            hasChanged = stringCollectionEntry.getValue().removeIf(permissionNode -> !permissionNode.isValid());
        }

        if (hasChanged) {
            this.updateGroup(permissionGroup);
        }
    }

    private void pushToDB(@NotNull UUID uuid, @NotNull String name) {
        CommonHelper.EXECUTOR.execute(() -> this.nameToUniqueIdDatabase.insert(name, uuid.toString(), new JsonConfiguration().add("id", uuid)));
    }

    private void publish(@NotNull Packet packet) {
        for (NetworkChannel registeredChannel : ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).getRegisteredChannels()) {
            if (registeredChannel.isAuthenticated()) {
                if (NODE && ExecutorAPI.getInstance().getNodeInformationProvider().getNodeInformation(registeredChannel.getName()).isPresent()) {
                    continue;
                }

                registeredChannel.sendPacket(packet);
            }
        }
    }
}
