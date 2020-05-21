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
package systems.reformcloud.reformcloud2.permissions.bukkit.permissible;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.nodes.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.nodes.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;

import java.util.*;
import java.util.stream.Stream;

public class DefaultPermissible extends PermissibleBase {

    private final UUID uuid;
    private Set<PermissionAttachmentInfo> perms;

    public DefaultPermissible(Player player) {
        super(player);
        this.uuid = player.getUniqueId();
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean value) {
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return this.has(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return this.has(perm.getName());
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return this.has(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return this.has(perm.getName());
    }

    @Override
    @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
    }

    @Override
    public void recalculatePermissions() {
    }

    @Override
    public synchronized void clearPermissions() {
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    @NotNull
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        this.perms = new HashSet<>();

        final PermissionUser permissionUser = PermissionManagement.getInstance().loadUser(this.uuid);
        final ProcessInformation current = API.getInstance().getCurrentProcessInformation();

        permissionUser.getPermissionNodes().stream().filter(PermissionNode::isValid)
                .forEach(e -> this.perms.add(new PermissionAttachmentInfo(
                        this,
                        e.getActualPermission(),
                        null,
                        e.isSet()
                )));
        permissionUser
                .getGroups()
                .stream()
                .filter(NodeGroup::isValid)
                .map(e -> PermissionManagement.getInstance().getGroup(e.getGroupName()))
                .filter(Objects::nonNull)
                .flatMap(e -> {
                    Stream.Builder<PermissionGroup> stream = Stream.<PermissionGroup>builder().add(e);
                    e.getSubGroups()
                            .stream()
                            .map(g -> PermissionManagement.getInstance().getGroup(g))
                            .filter(Objects::nonNull)
                            .forEach(stream);
                    return stream.build();
                }).forEach(g -> {
            g.getPermissionNodes().stream().filter(PermissionNode::isValid).forEach(e -> this.perms.add(new PermissionAttachmentInfo(
                    this,
                    e.getActualPermission(),
                    null,
                    e.isSet()
            )));
            Collection<PermissionNode> nodes = g.getPerGroupPermissions().get(current.getProcessGroup().getName());
            if (nodes != null) {
                nodes.stream().filter(PermissionNode::isValid).forEach(e -> this.perms.add(new PermissionAttachmentInfo(
                        this,
                        e.getActualPermission(),
                        null,
                        e.isSet()
                )));
            }
        });

        return this.perms;
    }

    private boolean has(String name) {
        final PermissionUser permissionUser = PermissionManagement.getInstance().loadUser(this.uuid);
        return permissionUser.hasPermission(name);
    }
}
