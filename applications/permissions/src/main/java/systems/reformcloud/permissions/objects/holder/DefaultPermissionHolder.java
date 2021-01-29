/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.permissions.objects.holder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.network.data.ProtocolBuffer;
import systems.reformcloud.permissions.checks.GeneralCheck;
import systems.reformcloud.permissions.checks.WildcardCheck;
import systems.reformcloud.permissions.nodes.PermissionNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class DefaultPermissionHolder implements PermissionHolder {

  private Collection<PermissionNode> permissionNodes;
  private Map<String, Collection<PermissionNode>> perGroupPermissions;

  public DefaultPermissionHolder() {
    this.permissionNodes = new CopyOnWriteArrayList<>();
    this.perGroupPermissions = new ConcurrentHashMap<>();
  }

  @Override
  public @Nullable Boolean hasPermission(@NotNull String permission) {
    permission = permission.toLowerCase();
    Boolean general = GeneralCheck.hasPermission(this, permission);
    if (general != null) {
      return general;
    }

    return WildcardCheck.hasWildcardPermission(this, permission);
  }

  @Override
  public @NotNull Collection<PermissionNode> getPermissions() {
    return this.permissionNodes;
  }

  @Override
  public void addPermission(@NotNull String permission) {
    this.addPermission(permission, true);
  }

  @Override
  public void addPermission(@NotNull String permission, boolean positive) {
    this.addPermission(permission, -1, positive);
  }

  @Override
  public void addPermission(@NotNull String permission, long timeout, boolean positive) {
    this.addPermission(new PermissionNode(System.currentTimeMillis(), timeout, positive, permission));
  }

  @Override
  public void addPermission(@NotNull PermissionNode permissionNode) {
    this.permissionNodes.add(permissionNode);
  }

  @Override
  public void removePermission(@NotNull String permission) {
    this.permissionNodes.removeIf(node -> node.getActualPermission().equalsIgnoreCase(permission));
  }

  @Override
  public void removePermission(@NotNull PermissionNode permissionNode) {
    this.permissionNodes.remove(permissionNode);
  }

  @Override
  public void clearPermissions() {
    this.permissionNodes.clear();
  }

  @Override
  public @NotNull Map<String, Collection<PermissionNode>> getPerGroupPermissions() {
    return this.perGroupPermissions;
  }

  @Override
  public void addPermission(@NotNull String group, @NotNull String permission) {
    this.addPermission(group, permission, true);
  }

  @Override
  public void addPermission(@NotNull String group, @NotNull String permission, boolean positive) {
    this.addPermission(group, permission, -1, positive);
  }

  @Override
  public void addPermission(@NotNull String group, @NotNull String permission, long timeout, boolean positive) {
    this.addPermission(group, new PermissionNode(System.currentTimeMillis(), timeout, positive, permission));
  }

  @Override
  public void addPermission(@NotNull String group, @NotNull PermissionNode permissionNode) {
    this.perGroupPermissions.computeIfAbsent(group, g -> new CopyOnWriteArrayList<>()).add(permissionNode);
  }

  @Override
  public void removePermission(@NotNull String group, @NotNull String permission) {
    final Collection<PermissionNode> permissionNodes = this.perGroupPermissions.get(group);
    if (permissionNodes != null) {
      permissionNodes.removeIf(node -> node.getActualPermission().equalsIgnoreCase(permission));
    }
  }

  @Override
  public void removePermission(@NotNull String group, @NotNull PermissionNode permissionNode) {
    final Collection<PermissionNode> permissionNodes = this.perGroupPermissions.get(group);
    if (permissionNodes != null) {
      permissionNodes.remove(permissionNode);
    }
  }

  @Override
  public void clearPerGroupPermissions(@NotNull String group) {
    final Collection<PermissionNode> permissionNodes = this.perGroupPermissions.get(group);
    if (permissionNodes != null) {
      permissionNodes.clear();
    }
  }

  @Override
  public void clearPerGroupPermissions() {
    this.perGroupPermissions.clear();
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeObjects(this.permissionNodes);

    buffer.writeVarInt(this.getPerGroupPermissions().size());
    for (Map.Entry<String, Collection<PermissionNode>> stringCollectionEntry : this.perGroupPermissions.entrySet()) {
      buffer.writeString(stringCollectionEntry.getKey());
      buffer.writeObjects(stringCollectionEntry.getValue());
    }
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.permissionNodes = buffer.readObjects(PermissionNode.class);

    int size = buffer.readVarInt();
    this.perGroupPermissions = new HashMap<>(size);
    for (int i = 0; i < size; i++) {
      this.perGroupPermissions.put(buffer.readString(), buffer.readObjects(PermissionNode.class));
    }
  }
}
