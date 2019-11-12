package systems.reformcloud.reformcloud2.permissions.util.user;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.util.group.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;

import java.util.Collection;
import java.util.UUID;

public class PermissionUser {

    public static final TypeToken<PermissionUser> TYPE = new TypeToken<PermissionUser>() {};

    public PermissionUser(UUID uuid, Collection<PermissionNode> permissionNodes, Collection<NodeGroup> groups) {
        this.uuid = uuid;
        this.permissionNodes = permissionNodes;
        this.groups = groups;
    }

    private final UUID uuid;

    private final Collection<PermissionNode> permissionNodes;

    private final Collection<NodeGroup> groups;

    public UUID getUuid() {
        return uuid;
    }

    public Collection<PermissionNode> getPermissionNodes() {
        return permissionNodes;
    }

    public Collection<NodeGroup> getGroups() {
        return groups;
    }

    public boolean hasPermission(String permission) {
        if (permission == null) {
            new NullPointerException("permission").printStackTrace();
            return false;
        }

        if (permission.equalsIgnoreCase("bukkit.brodcast")
                || permission.equalsIgnoreCase("bukkit.brodcast.admin")) {
            return true;
        }

        final PermissionNode node = Links.filter(permissionNodes,
                e -> e.getActualPermission().equalsIgnoreCase(permission) && e.isValid());
        if (node != null) {
            return node.isSet();
        }

        final PermissionNode star = Links.filter(permissionNodes,
                e -> e.getActualPermission().equals("*") && e.isValid());
        if (star != null && star.isSet()) {
            return true;
        }

        return PermissionAPI.INSTANCE.getPermissionUtil().hasPermission(this, permission);
    }
}
