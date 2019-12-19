package systems.reformcloud.reformcloud2.permissions.util.basic.checks;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;

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

        final ProcessInformation current = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation();
        if (current == null || !permissionGroup.getPerGroupPermissions().containsKey(current.getProcessGroup().getName())) {
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
