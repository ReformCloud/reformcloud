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
package systems.reformcloud.reformcloud2.permissions.util.basic.checks;

import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

import java.util.Collection;

public class WildcardCheck {

    public static boolean hasWildcardPermission(PermissionGroup permissionGroup, String perm) {
        if (permissionGroup.getPermissionNodes().stream().anyMatch(e -> {
            final String actual = e.getActualPermission();
            if (actual.length() > 1
                    && actual.endsWith("*")
                    && perm.startsWith(actual.substring(0, actual.length() - 1))
                    && e.isValid()) {
                return e.isSet();
            }

            return false;
        })) {
            return true;
        }

        final ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        if (!permissionGroup.getPerGroupPermissions().containsKey(current.getProcessGroup().getName())) {
            return false;
        }

        final Collection<PermissionNode> currentGroupPerms = permissionGroup.getPerGroupPermissions()
                .get(current.getProcessGroup().getName());
        if (currentGroupPerms.isEmpty()) {
            return false;
        }

        for (PermissionNode currentGroupPerm : currentGroupPerms) {
            final String actual = currentGroupPerm.getActualPermission();
            if (actual.length() > 1
                    && actual.endsWith("*")
                    && perm.startsWith(actual.substring(0, actual.length() - 1))
                    && currentGroupPerm.isValid()) {
                return currentGroupPerm.isSet();
            }
        }

        return false;
    }

    public static Boolean hasWildcardPermission(PermissionUser permissionUser, String perm) {
        for (PermissionNode permissionNode : permissionUser.getPermissionNodes()) {
            final String actual = permissionNode.getActualPermission();
            if (actual.length() > 1
                    && actual.endsWith("*")
                    && perm.startsWith(actual.substring(0, actual.length() - 1))
                    && permissionNode.isValid()) {
                return permissionNode.isSet();
            }
        }

        return null;
    }
}
