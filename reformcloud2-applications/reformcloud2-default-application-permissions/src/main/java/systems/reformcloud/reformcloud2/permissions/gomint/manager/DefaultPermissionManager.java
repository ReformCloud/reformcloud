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
package systems.reformcloud.reformcloud2.permissions.gomint.manager;

import io.gomint.permission.Group;
import io.gomint.permission.PermissionManager;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.nodes.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.nodes.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;

import java.util.UUID;

public class DefaultPermissionManager implements PermissionManager {

    private final UUID uniqueId;

    public DefaultPermissionManager(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public boolean hasPermission(String s) {
        return PermissionManagement.getInstance().loadUser(this.uniqueId).hasPermission(s);
    }

    @Override
    public boolean hasPermission(String s, boolean b) {
        return this.hasPermission(s); // TODO: should we change the api here?
    }

    @Override
    public void addGroup(Group group) {
        PermissionUser permissionUser = PermissionManagement.getInstance().loadUser(this.uniqueId);
        if (permissionUser.isInGroup(group.getName())) {
            return;
        }

        permissionUser.getGroups().add(new NodeGroup(System.currentTimeMillis(), -1, group.getName()));
        PermissionManagement.getInstance().updateUser(permissionUser);
    }

    @Override
    public void removeGroup(Group group) {
        PermissionUser permissionUser = PermissionManagement.getInstance().loadUser(this.uniqueId);
        if (!permissionUser.isInGroup(group.getName())) {
            return;
        }

        if (permissionUser.getGroups().removeIf(nodeGroup -> nodeGroup.getGroupName().equals(group.getName()))) {
            PermissionManagement.getInstance().updateUser(permissionUser);
        }
    }

    @Override
    public void setPermission(String s, boolean b) {
        PermissionManagement.getInstance().addUserPermission(this.uniqueId, PermissionNode.createNode(s, -1, b));
    }

    @Override
    public void removePermission(String s) {
        PermissionUser permissionUser = PermissionManagement.getInstance().loadUser(this.uniqueId);
        if (permissionUser.getPermissionNodes().removeIf(node -> node.getActualPermission().equals(s))) {
            PermissionManagement.getInstance().updateUser(permissionUser);
        }
    }
}
