package systems.reformcloud.reformcloud2.permissions.nukkit.permissible;

import cn.nukkit.Player;
import cn.nukkit.permission.PermissibleBase;
import cn.nukkit.permission.Permission;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.permission.PermissionAttachmentInfo;
import cn.nukkit.plugin.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.util.group.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

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

        final PermissionUser permissionUser = PermissionAPI.getInstance().getPermissionUtil().loadUser(uuid);
        final ProcessInformation current = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation();

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
        final PermissionUser permissionUser = PermissionAPI.getInstance().getPermissionUtil().loadUser(uuid);
        return permissionUser.hasPermission(name);
    }
}
