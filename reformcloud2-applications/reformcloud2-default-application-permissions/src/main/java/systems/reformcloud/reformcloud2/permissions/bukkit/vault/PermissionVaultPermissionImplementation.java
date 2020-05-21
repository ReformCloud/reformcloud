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
package systems.reformcloud.reformcloud2.permissions.bukkit.vault;

import net.milkbowl.vault.permission.Permission;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.nodes.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.nodes.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;

public class PermissionVaultPermissionImplementation extends Permission {

    private final PermissionManagement permissionManagement = PermissionManagement.getInstance();

    PermissionVaultPermissionImplementation() {
    }

    @Override
    public String getName() {
        return "ReformCloud2BukkitPermissions";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        return VaultUtil.getUserFromName(player).map(user -> user.hasPermission(permission)).orElse(false);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        return VaultUtil.getUserFromName(player).map(user -> {
            this.permissionManagement.addUserPermission(user.getUniqueID(), PermissionNode.createNode(permission, -1, true));
            return true;
        }).orElse(false);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return VaultUtil.getUserFromName(player).map(user -> {
            PermissionNode node = Streams.filter(user.getPermissionNodes(), e -> e.getActualPermission().equals(permission));
            if (node == null) {
                return false;
            }

            user.getPermissionNodes().remove(node);
            this.permissionManagement.updateUser(user);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return this.permissionManagement.getPermissionGroup(group).map(g -> g.hasPermission(permission)).orElse(false);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        return this.permissionManagement.getPermissionGroup(group).map(g -> {
            this.permissionManagement.addGroupPermission(g, PermissionNode.createNode(permission, -1, true));
            return true;
        }).orElse(false);
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        return this.permissionManagement.getPermissionGroup(group).map(g -> {
            PermissionNode node = Streams.filter(g.getPermissionNodes(), e -> e.getActualPermission().equals(permission));
            if (node == null) {
                return false;
            }

            g.getPermissionNodes().remove(node);
            this.permissionManagement.updateGroup(g);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return VaultUtil.getUserFromName(player).map(permissionUser -> permissionUser.isInGroup(group)).orElse(false);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return VaultUtil.getUserFromName(player).map(p -> {
            boolean success = p.getGroups().add(new NodeGroup(System.currentTimeMillis(), -1, group));
            if (success) {
                this.permissionManagement.updateUser(p);
            }

            return success;
        }).orElse(false);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return VaultUtil.getUserFromName(player).map(permissionUser -> {
            NodeGroup nodeGroup = Streams.filter(permissionUser.getGroups(), e -> e.getGroupName().equals(group));
            if (nodeGroup == null) {
                return false;
            }

            permissionUser.getGroups().remove(nodeGroup);
            this.permissionManagement.updateUser(permissionUser);
            return true;
        }).orElse(false);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return VaultUtil.getUserFromName(player)
                .map(permissionUser -> permissionUser.getGroups().stream().map(NodeGroup::getGroupName).toArray(String[]::new))
                .orElse(new String[0]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return VaultUtil.getUserFromName(player)
                .flatMap(PermissionUser::getHighestPermissionGroup)
                .map(PermissionGroup::getName)
                .orElse(null);
    }

    @Override
    public String[] getGroups() {
        return this.permissionManagement.getPermissionGroups().stream().map(PermissionGroup::getName).toArray(String[]::new);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}
