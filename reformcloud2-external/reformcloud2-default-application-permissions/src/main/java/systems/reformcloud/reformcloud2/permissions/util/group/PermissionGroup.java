package systems.reformcloud.reformcloud2.permissions.util.group;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.permissions.util.basic.checks.GeneralCheck;
import systems.reformcloud.reformcloud2.permissions.util.basic.checks.WildcardCheck;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PermissionGroup implements SerializableObject {

    public static final TypeToken<PermissionGroup> TYPE = new TypeToken<PermissionGroup>() {
    };

    public PermissionGroup(
            @NotNull Collection<PermissionNode> permissionNodes,
            @NotNull Map<String, Collection<PermissionNode>> perGroupPermissions,
            @NotNull Collection<String> subGroups,
            @NotNull String name,
            int priority
    ) {
        this.permissionNodes = permissionNodes;
        this.perGroupPermissions = perGroupPermissions;
        this.subGroups = subGroups;
        this.name = name;
        this.priority = priority;
    }

    private Collection<PermissionNode> permissionNodes;

    private Map<String, Collection<PermissionNode>> perGroupPermissions;

    private Collection<String> subGroups;

    private String name;

    private int priority;

    @NotNull
    public Collection<PermissionNode> getPermissionNodes() {
        return permissionNodes;
    }

    @NotNull
    public Map<String, Collection<PermissionNode>> getPerGroupPermissions() {
        return perGroupPermissions;
    }

    @NotNull
    public Collection<String> getSubGroups() {
        return subGroups;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean hasPermission(@NotNull String perm) {
        if (WildcardCheck.hasWildcardPermission(this, perm)) {
            return true;
        }

        return GeneralCheck.hasPermission(this, perm);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObjects(this.permissionNodes);

        buffer.writeVarInt(this.perGroupPermissions.size());
        for (Map.Entry<String, Collection<PermissionNode>> stringCollectionEntry : this.perGroupPermissions.entrySet()) {
            buffer.writeString(stringCollectionEntry.getKey());
            buffer.writeObjects(stringCollectionEntry.getValue());
        }

        buffer.writeStringArray(this.subGroups);
        buffer.writeString(this.name);
        buffer.writeInt(this.priority);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.permissionNodes = buffer.readObjects(PermissionNode.class);

        int size = buffer.readVarInt();
        this.perGroupPermissions = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            this.perGroupPermissions.put(buffer.readString(), buffer.readObjects(PermissionNode.class));
        }

        this.subGroups = buffer.readStringArray();
        this.name = buffer.readString();
        this.priority = buffer.readInt();
    }
}
