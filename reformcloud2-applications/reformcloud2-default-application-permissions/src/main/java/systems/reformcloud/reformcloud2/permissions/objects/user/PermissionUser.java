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
package systems.reformcloud.reformcloud2.permissions.objects.user;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.checks.WildcardCheck;
import systems.reformcloud.reformcloud2.permissions.nodes.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.nodes.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;

import java.util.Collection;
import java.util.Optional;
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

    @NotNull
    public Optional<PermissionGroup> getHighestPermissionGroup() {
        PermissionGroup permissionGroup = null;

        for (NodeGroup nodeGroup : this.groups) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(nodeGroup.getGroupName());
            if (group == null) {
                continue;
            }

            if (permissionGroup == null) {
                permissionGroup = group;
            } else if (permissionGroup.getPriority() > group.getPriority()) {
                permissionGroup = group;
            }
        }

        return Optional.ofNullable(permissionGroup);
    }

    public boolean isInGroup(@NotNull String group) {
        for (NodeGroup nodeGroup : this.groups) {
            if (nodeGroup.getGroupName().equals(group) && nodeGroup.isValid()) {
                return true;
            }
        }

        return false;
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

        return PermissionManagement.getInstance().hasPermission(this, permission);
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
