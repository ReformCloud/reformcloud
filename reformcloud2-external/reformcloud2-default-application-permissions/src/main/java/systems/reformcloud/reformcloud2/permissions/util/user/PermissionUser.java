package systems.reformcloud.reformcloud2.permissions.util.user;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.util.basic.checks.WildcardCheck;
import systems.reformcloud.reformcloud2.permissions.util.group.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;

import java.util.Collection;
import java.util.UUID;

public class PermissionUser implements SerializableObject {

    public static final TypeToken<PermissionUser> TYPE = new TypeToken<PermissionUser>() {
    };

    @ApiStatus.Internal
    public PermissionUser() {
    }

    public PermissionUser(
            @NotNull UUID uuid,
            @NotNull Collection<PermissionNode> permissionNodes,
            @NotNull Collection<NodeGroup> groups
    ) {
        this.uuid = uuid;
        this.permissionNodes = permissionNodes;
        this.groups = groups;
    }

    private UUID uuid;

    private Collection<PermissionNode> permissionNodes;

    private Collection<NodeGroup> groups;

    @NotNull
    public UUID getUniqueID() {
        return uuid;
    }

    @NotNull
    public Collection<PermissionNode> getPermissionNodes() {
        return permissionNodes;
    }

    @NotNull
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

        final PermissionNode node = Streams.filter(permissionNodes,
                e -> e.getActualPermission().equalsIgnoreCase(permission) && e.isValid());
        if (node != null) {
            return node.isSet();
        }

        final PermissionNode star = Streams.filter(permissionNodes,
                e -> e.getActualPermission().equals("*") && e.isValid());
        if (star != null && star.isSet()) {
            return true;
        }

        Boolean wildCard = WildcardCheck.hasWildcardPermission(this, permission);
        if (wildCard != null) {
            return wildCard;
        }

        return PermissionAPI.getInstance().getPermissionUtil().hasPermission(this, permission);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.uuid);
        buffer.writeObjects(this.groups);
        buffer.writeObjects(this.permissionNodes);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.uuid = buffer.readUniqueId();
        this.groups = buffer.readObjects(NodeGroup.class);
        this.permissionNodes = buffer.readObjects(PermissionNode.class);
    }
}
