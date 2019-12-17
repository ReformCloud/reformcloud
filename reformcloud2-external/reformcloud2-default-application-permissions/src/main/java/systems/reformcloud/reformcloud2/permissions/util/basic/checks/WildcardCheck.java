package systems.reformcloud.reformcloud2.permissions.util.basic.checks;

import java.util.Collection;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

public class WildcardCheck {

  public static boolean hasWildcardPermission(PermissionGroup permissionGroup,
                                              String perm) {
    if (permissionGroup.getPermissionNodes().stream().anyMatch(e -> {
          final String actual = e.getActualPermission();
          if (actual.length() > 1 && actual.endsWith("*") &&
              perm.startsWith(actual.substring(0, perm.length() - 1)) &&
              e.isValid()) {
            return e.isSet();
          }

          return false;
        })) {
      return true;
    }

    final ProcessInformation current = ExecutorAPI.getInstance()
                                           .getSyncAPI()
                                           .getProcessSyncAPI()
                                           .getThisProcessInformation();
    if (current == null ||
        !permissionGroup.getPerGroupPermissions().containsKey(
            current.getProcessGroup().getName())) {
      return false;
    }

    final Collection<PermissionNode> currentGroupPerms =
        permissionGroup.getPerGroupPermissions().get(
            current.getProcessGroup().getName());
    if (currentGroupPerms.isEmpty()) {
      return false;
    }

    for (PermissionNode currentGroupPerm : currentGroupPerms) {
      final String actual = currentGroupPerm.getActualPermission();
      if (actual.length() > 1 && actual.endsWith("*") &&
          perm.startsWith(actual.substring(0, actual.length() - 1)) &&
          currentGroupPerm.isValid()) {
        return currentGroupPerm.isSet();
      }
    }

    return false;
  }

  public static Boolean hasWildcardPermission(PermissionUser permissionUser,
                                              String perm) {
    for (PermissionNode permissionNode : permissionUser.getPermissionNodes()) {
      final String actual = permissionNode.getActualPermission();
      if (actual.length() > 1 && actual.endsWith("*") &&
          perm.startsWith(actual.substring(0, actual.length() - 1)) &&
          permissionNode.isValid()) {
        return permissionNode.isSet();
      }
    }

    return null;
  }
}
