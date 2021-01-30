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
package systems.reformcloud.permissions.checks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.refomcloud.embedded.Embedded;
import systems.reformcloud.permissions.nodes.PermissionNode;
import systems.reformcloud.permissions.objects.holder.PermissionHolder;
import systems.reformcloud.process.ProcessInformation;

import java.util.Collection;

public final class GeneralCheck {

  private GeneralCheck() {
    throw new UnsupportedOperationException();
  }

  @Nullable
  public static Boolean hasPermission(@NotNull PermissionHolder holder, @NotNull String perm) {
    Boolean star = has(holder, "*");
    if (star != null && star) {
      return true;
    }

    return has(holder, perm);
  }

  @Nullable
  private static Boolean has(@NotNull PermissionHolder holder, @NotNull String perm) {
    if (holder.getPermissions().stream().anyMatch(e -> e.getActualPermission().equalsIgnoreCase(perm) && e.isSet())) {
      return true;
    }

    final ProcessInformation current = Embedded.getInstance().getCurrentProcessInformation();
    if (!holder.getPerGroupPermissions().containsKey(current.getProcessGroup().getName())) {
      return null;
    }

    final Collection<PermissionNode> currentGroupPerms = holder.getPerGroupPermissions().get(current.getProcessGroup().getName());
    if (currentGroupPerms.isEmpty()) {
      return null;
    }

    for (PermissionNode currentGroupPerm : currentGroupPerms) {
      if (currentGroupPerm.getActualPermission().equalsIgnoreCase(perm)) {
        return currentGroupPerm.isSet();
      }
    }

    return null;
  }
}
