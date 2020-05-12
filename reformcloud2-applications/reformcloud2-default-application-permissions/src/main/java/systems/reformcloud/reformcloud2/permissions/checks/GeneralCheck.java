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
package systems.reformcloud.reformcloud2.permissions.checks;

import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.permissions.nodes.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;

import java.util.Collection;

public class GeneralCheck {

    public static boolean hasPermission(PermissionGroup permissionGroup, String perm) {
        if (has(permissionGroup, "*")) {
            return true;
        }

        return has(permissionGroup, perm);
    }

    private static boolean has(PermissionGroup permissionGroup, String perm) {
        if (permissionGroup.getPermissionNodes().stream().anyMatch(e -> e.getActualPermission().equals(perm) && e.isSet())) {
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
            if (currentGroupPerm.getActualPermission().equals(perm)) {
                return currentGroupPerm.isSet();
            }
        }

        return false;
    }
}
