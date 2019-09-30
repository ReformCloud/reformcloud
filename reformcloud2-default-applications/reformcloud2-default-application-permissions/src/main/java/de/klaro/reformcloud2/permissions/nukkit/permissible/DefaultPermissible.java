package de.klaro.reformcloud2.permissions.nukkit.permissible;

import cn.nukkit.Player;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.permissions.PermissionAPI;
import de.klaro.reformcloud2.permissions.util.group.PermissionGroup;
import de.klaro.reformcloud2.permissions.util.permission.PermissionNode;
import de.klaro.reformcloud2.permissions.util.user.PermissionUser;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class DefaultPermissible extends PermissibleBase {

    public DefaultPermissible(Player player) {
        super(player);
        this.uuid = player.getUniqueId();
    }

    private final UUID uuid;

    private Map<String, PermissionAttachmentInfo> perms;

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean value) {
    }

    @Override
    public boolean isPermissionSet(String name) {
        return has(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return has(perm.getName());
    }

    @Override
    public boolean hasPermission(String name) {
        return has(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return has(perm.getName());
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, Boolean value) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
    }

    @Override
    public void recalculatePermissions() {
    }

    @Override
    public synchronized void clearPermissions() {
    }

    @Override
    public Map<String, PermissionAttachmentInfo> getEffectivePermissions() {
        this.perms = new ConcurrentHashMap<>();

        final PermissionUser permissionUser = PermissionAPI.INSTANCE.getPermissionUtil().loadUser(uuid);
        final ProcessInformation current = ExecutorAPI.getInstance().getThisProcessInformation();

        permissionUser.getPermissionNodes().stream().filter(PermissionNode::isValid)
                .forEach(e -> perms.put(e.getActualPermission(), new PermissionAttachmentInfo(
                        this,
                        e.getActualPermission(),
                        null,
                        e.isSet()
                )));
        permissionUser
                .getGroups()
                .stream()
                .map(e -> PermissionAPI.INSTANCE.getPermissionUtil().getGroup(e))
                .filter(Objects::nonNull)
                .filter(PermissionGroup::isValid)
                .flatMap(e -> {
                    Stream.Builder<PermissionGroup> stream = Stream.<PermissionGroup>builder().add(e);
                    e.getSubGroups()
                            .stream()
                            .map(g -> PermissionAPI.INSTANCE.getPermissionUtil().getGroup(g))
                            .filter(Objects::nonNull)
                            .filter(PermissionGroup::isValid)
                            .forEach(stream);
                    return stream.build();
                }).forEach(g -> {
            g.getPermissionNodes().stream().filter(PermissionNode::isValid).forEach(e -> perms.put(e.getActualPermission(), new PermissionAttachmentInfo(
                    this,
                    e.getActualPermission(),
                    null,
                    e.isSet()
            )));
            if (current != null) {
                Collection<PermissionNode> nodes = g.getPerGroupPermissions().get(current.getProcessGroup().getName());
                if (nodes != null) {
                    nodes.stream().filter(PermissionNode::isValid).forEach(e -> perms.put(e.getActualPermission(), new PermissionAttachmentInfo(
                            this,
                            e.getActualPermission(),
                            null,
                            e.isSet()
                    )));
                }
            }
        });

        return perms;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name) {
        return new PermissionAttachment(plugin, this);
    }

    private boolean has(String name) {
        final PermissionUser permissionUser = PermissionAPI.INSTANCE.getPermissionUtil().loadUser(uuid);
        return PermissionAPI.INSTANCE.getPermissionUtil().hasPermission(permissionUser, name);
    }
}
