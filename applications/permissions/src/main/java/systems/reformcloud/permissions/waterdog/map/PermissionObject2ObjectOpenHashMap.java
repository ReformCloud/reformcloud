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
package systems.reformcloud.permissions.waterdog.map;

import dev.waterdog.utils.types.Permission;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import systems.reformcloud.permissions.PermissionManagement;

import java.util.Locale;
import java.util.UUID;

public class PermissionObject2ObjectOpenHashMap extends Object2ObjectOpenHashMap<String, Permission> {

  private final UUID uniqueId;

  public PermissionObject2ObjectOpenHashMap(UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  @Override
  public Permission get(Object k) {
    final Boolean hasPermission = PermissionManagement.getInstance().loadUser(this.uniqueId).hasPermission(((String) k).toUpperCase(Locale.ROOT));
    return hasPermission == null ? null : new Permission(((String) k), hasPermission);
  }

  @Override
  public Permission put(String s, Permission permission) {
    return null;
  }

  @Override
  public Permission remove(Object k) {
    return null;
  }
}
