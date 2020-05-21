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

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;

public class PermissionVaultChatImplementation extends Chat {

    private final PermissionManagement permissionManagement = PermissionManagement.getInstance();

    PermissionVaultChatImplementation(Permission perms) {
        super(perms);
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
    public String getPlayerPrefix(String world, String player) {
        return VaultUtil.getUserFromName(player).flatMap(PermissionUser::getPrefix).orElse(null);
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        VaultUtil.getUserFromName(player).ifPresent(user -> {
            user.setPrefix(prefix);
            this.permissionManagement.updateUser(user);
        });
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        return VaultUtil.getUserFromName(player).flatMap(PermissionUser::getSuffix).orElse(null);
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        VaultUtil.getUserFromName(player).ifPresent(user -> {
            user.setSuffix(suffix);
            this.permissionManagement.updateUser(user);
        });
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return this.permissionManagement.getPermissionGroup(group).flatMap(PermissionGroup::getPrefix).orElse(null);
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        this.permissionManagement.getPermissionGroup(group).ifPresent(permissionGroup -> {
            permissionGroup.setPrefix(prefix);
            this.permissionManagement.updateGroup(permissionGroup);
        });
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return this.permissionManagement.getPermissionGroup(group).flatMap(PermissionGroup::getSuffix).orElse(null);
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        this.permissionManagement.getPermissionGroup(group).ifPresent(permissionGroup -> {
            permissionGroup.setSuffix(suffix);
            this.permissionManagement.updateGroup(permissionGroup);
        });
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        return VaultUtil.getUserFromName(player).map(user -> user.getExtra().getOrDefault(node, defaultValue)).orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {
        VaultUtil.getUserFromName(player).ifPresent(permissionUser -> {
            permissionUser.getExtra().add(node, value);
            this.permissionManagement.updateUser(permissionUser);
        });
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        return this.permissionManagement.getPermissionGroup(group).map(g -> g.getExtra().getOrDefault(node, defaultValue)).orElse(defaultValue);
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {
        this.permissionManagement.getPermissionGroup(group).ifPresent(permissionGroup -> {
            permissionGroup.getExtra().add(node, value);
            this.permissionManagement.updateGroup(permissionGroup);
        });
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        return VaultUtil.getUserFromName(player).map(user -> user.getExtra().getOrDefault(node, defaultValue)).orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {
        VaultUtil.getUserFromName(player).ifPresent(permissionUser -> {
            permissionUser.getExtra().add(node, value);
            this.permissionManagement.updateUser(permissionUser);
        });
    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        return this.permissionManagement.getPermissionGroup(group).map(g -> g.getExtra().getOrDefault(node, defaultValue)).orElse(defaultValue);
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {
        this.permissionManagement.getPermissionGroup(group).ifPresent(permissionGroup -> {
            permissionGroup.getExtra().add(node, value);
            this.permissionManagement.updateGroup(permissionGroup);
        });
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        return VaultUtil.getUserFromName(player).map(user -> user.getExtra().getOrDefault(node, defaultValue)).orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
        VaultUtil.getUserFromName(player).ifPresent(permissionUser -> {
            permissionUser.getExtra().add(node, value);
            this.permissionManagement.updateUser(permissionUser);
        });
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        return this.permissionManagement.getPermissionGroup(group).map(g -> g.getExtra().getOrDefault(node, defaultValue)).orElse(defaultValue);
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
        this.permissionManagement.getPermissionGroup(group).ifPresent(permissionGroup -> {
            permissionGroup.getExtra().add(node, value);
            this.permissionManagement.updateGroup(permissionGroup);
        });
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        return VaultUtil.getUserFromName(player).map(user -> user.getExtra().getOrDefault(node, defaultValue)).orElse(defaultValue);
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {
        VaultUtil.getUserFromName(player).ifPresent(permissionUser -> {
            permissionUser.getExtra().add(node, value);
            this.permissionManagement.updateUser(permissionUser);
        });
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        return this.permissionManagement.getPermissionGroup(group).map(g -> g.getExtra().getOrDefault(node, defaultValue)).orElse(defaultValue);
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {
        this.permissionManagement.getPermissionGroup(group).ifPresent(permissionGroup -> {
            permissionGroup.getExtra().add(node, value);
            this.permissionManagement.updateGroup(permissionGroup);
        });
    }
}
