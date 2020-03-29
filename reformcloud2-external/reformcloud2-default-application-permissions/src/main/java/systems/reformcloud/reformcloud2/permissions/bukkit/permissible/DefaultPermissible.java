package systems.reformcloud.reformcloud2.permissions.bukkit.permissible;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.util.group.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

import java.util.*;
import java.util.stream.Stream;

public class DefaultPermissible extends PermissibleBase {

    public DefaultPermissible(Player player) {
        super(player);
        this.uuid = player.getUniqueId();
    }

    private final UUID uuid;

    private Set<PermissionAttachmentInfo> perms;

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean value) {
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return has(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return has(perm.getName());
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return has(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return has(perm.getName());
    }

    @Override
    @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
    }

    @Override
    public void recalculatePermissions() {
    }

    @Override
    public synchronized void clearPermissions() {
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    @NotNull
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        this.perms = new HashSet<>();

        final PermissionUser permissionUser = PermissionAPI.getInstance().getPermissionUtil().loadUser(uuid);
        final ProcessInformation current = API.getInstance().getCurrentProcessInformation();

        permissionUser.getPermissionNodes().stream().filter(PermissionNode::isValid)
                .forEach(e -> perms.add(new PermissionAttachmentInfo(
                        this,
                        e.getActualPermission(),
                        null,
                        e.isSet()
                )));
        permissionUser
                .getGroups()
                .stream()
                .filter(NodeGroup::isValid)
                .map(e -> PermissionAPI.getInstance().getPermissionUtil().getGroup(e.getGroupName()))
                .filter(Objects::nonNull)
                .flatMap(e -> {
                    Stream.Builder<PermissionGroup> stream = Stream.<PermissionGroup>builder().add(e);
                    e.getSubGroups()
                            .stream()
                            .map(g -> PermissionAPI.getInstance().getPermissionUtil().getGroup(g))
                            .filter(Objects::nonNull)
                            .forEach(stream);
                    return stream.build();
                }).forEach(g -> {
                    g.getPermissionNodes().stream().filter(PermissionNode::isValid).forEach(e -> perms.add(new PermissionAttachmentInfo(
                            this,
                            e.getActualPermission(),
                            null,
                            e.isSet()
                    )));
                    Collection<PermissionNode> nodes = g.getPerGroupPermissions().get(current.getProcessGroup().getName());
                    if (nodes != null) {
                        nodes.stream().filter(PermissionNode::isValid).forEach(e -> perms.add(new PermissionAttachmentInfo(
                                this,
                                e.getActualPermission(),
                                null,
                                e.isSet()
                        )));
                    }
        });

        return perms;
    }

    private boolean has(String name) {
        final PermissionUser permissionUser = PermissionAPI.getInstance().getPermissionUtil().loadUser(uuid);
        return permissionUser.hasPermission(name);
    }
}
