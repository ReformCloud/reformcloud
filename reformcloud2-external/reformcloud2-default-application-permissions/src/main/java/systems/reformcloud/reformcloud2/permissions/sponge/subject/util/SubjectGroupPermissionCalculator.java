package systems.reformcloud.reformcloud2.permissions.sponge.subject.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;

public class SubjectGroupPermissionCalculator {

  private SubjectGroupPermissionCalculator() {
    throw new UnsupportedOperationException();
  }

  public static Map<String, Boolean> getPermissionsOf(PermissionGroup group) {
    Map<String, Boolean> out = new HashMap<>();
    final ProcessInformation current = ExecutorAPI.getInstance()
                                           .getSyncAPI()
                                           .getProcessSyncAPI()
                                           .getThisProcessInformation();
    if (current != null) {
      Collection<PermissionNode> permissionNodes =
          group.getPerGroupPermissions().get(
              current.getProcessGroup().getName());
      if (permissionNodes != null) {
        permissionNodes.forEach(e -> {
          if (!e.isValid()) {
            return;
          }

          out.put(e.getActualPermission(), e.isSet());
        });
      }
    }

    group.getPermissionNodes().forEach(e -> {
      if (!e.isValid()) {
        return;
      }

      out.put(e.getActualPermission(), e.isSet());
    });
    return out;
  }
}
