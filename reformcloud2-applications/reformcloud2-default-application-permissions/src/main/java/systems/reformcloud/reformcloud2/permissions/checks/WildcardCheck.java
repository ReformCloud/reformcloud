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
package systems.reformcloud.reformcloud2.permissions.checks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.permissions.nodes.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;

import java.util.Collection;

public final class WildcardCheck {

    private WildcardCheck() {
        throw new UnsupportedOperationException();
    }

    public static Boolean hasWildcardPermission(@NotNull PermissionGroup permissionGroup, @NotNull String perm) {
        Boolean hasPermission = hasPermission(permissionGroup.getPermissionNodes(), perm);
        if (hasPermission != null) {
            return hasPermission;
        }

        final ProcessInformation current = Embedded.getInstance().getCurrentProcessInformation();
        final Collection<PermissionNode> currentGroupPerms = permissionGroup.getPerGroupPermissions().get(current.getProcessGroup().getName());
        if (currentGroupPerms == null || currentGroupPerms.isEmpty()) {
            return null;
        }

        return hasPermission(currentGroupPerms, perm);
    }

    @Nullable
    public static Boolean hasWildcardPermission(@NotNull PermissionUser permissionUser, @NotNull String perm) {
        Boolean hasPermission = hasPermission(permissionUser.getPermissionNodes(), perm);
        if (hasPermission != null) {
            return hasPermission;
        }

        final ProcessInformation current = Embedded.getInstance().getCurrentProcessInformation();
        final Collection<PermissionNode> currentGroupPerms = permissionUser.getPerGroupPermissions().get(current.getProcessGroup().getName());
        if (currentGroupPerms == null || currentGroupPerms.isEmpty()) {
            return null;
        }

        return hasPermission(currentGroupPerms, perm);
    }

    @Nullable
    private static Boolean hasPermission(@NotNull Collection<PermissionNode> permissionNodes, @NotNull String permission) {
        for (PermissionNode permissionNode : permissionNodes) {
            final String actual = permissionNode.getActualPermission();
            if (actual.length() > 1
                && actual.endsWith("*")
                && permission.startsWith(actual.substring(0, actual.length() - 1))
                && permissionNode.isValid()) {
                return permissionNode.isSet();
            }
        }

        return null;
    }
}
