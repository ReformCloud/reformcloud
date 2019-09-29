package de.klaro.reformcloud2.permissions.util.user;

import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.permissions.PermissionAPI;
import de.klaro.reformcloud2.permissions.util.permission.PermissionNode;

import java.util.Collection;
import java.util.UUID;

public class PermissionUser {

    public PermissionUser(UUID uuid, Collection<PermissionNode> permissionNodes, Collection<String> groups) {
        this.uuid = uuid;
        this.permissionNodes = permissionNodes;
        this.groups = groups;
    }

    private final UUID uuid;

    private final Collection<PermissionNode> permissionNodes;

    private final Collection<String> groups;

    public UUID getUuid() {
        return uuid;
    }

    public Collection<PermissionNode> getPermissionNodes() {
        return permissionNodes;
    }

    public Collection<String> getGroups() {
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
                e -> e.getActualPermission().equalsIgnoreCase(permission) && !e.canBeRemoved());
        if (node != null) {
            return node.isSet();
        }

        return PermissionAPI.INSTANCE.getPermissionUtil().hasPermission(this, permission);
    }
}
