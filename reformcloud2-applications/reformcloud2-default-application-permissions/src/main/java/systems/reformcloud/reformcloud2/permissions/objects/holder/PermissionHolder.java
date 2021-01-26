/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.reformcloud.reformcloud2.permissions.objects.holder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.network.data.SerializableObject;
import systems.reformcloud.reformcloud2.permissions.nodes.PermissionNode;

import java.util.Collection;
import java.util.Map;

public interface PermissionHolder extends SerializableObject {

  @Nullable Boolean hasPermission(@NotNull String permission);

  @NotNull Collection<PermissionNode> getPermissions();

  void addPermission(@NotNull String permission);

  void addPermission(@NotNull String permission, boolean positive);

  void addPermission(@NotNull String permission, long timeout, boolean positive);

  void addPermission(@NotNull PermissionNode permissionNode);

  void removePermission(@NotNull String permission);

  void removePermission(@NotNull PermissionNode permissionNode);

  void clearPermissions();

  @NotNull Map<String, Collection<PermissionNode>> getPerGroupPermissions();

  void addPermission(@NotNull String group, @NotNull String permission);

  void addPermission(@NotNull String group, @NotNull String permission, boolean positive);

  void addPermission(@NotNull String group, @NotNull String permission, long timeout, boolean positive);

  void addPermission(@NotNull String group, @NotNull PermissionNode permissionNode);

  void removePermission(@NotNull String group, @NotNull String permission);

  void removePermission(@NotNull String group, @NotNull PermissionNode permissionNode);

  void clearPerGroupPermissions(@NotNull String group);

  void clearPerGroupPermissions();
}
