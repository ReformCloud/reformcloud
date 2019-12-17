package systems.reformcloud.reformcloud2.permissions.sponge.subject.base.user;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.AbstractSpongeSubjectData;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.util.SubjectGroupPermissionCalculator;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

public class SpongeSubjectData extends AbstractSpongeSubjectData {

  public SpongeSubjectData(@Nonnull UUID user) { this.uniqueID = user; }

  private final UUID uniqueID;

  @Override
  @Nonnull
  public Map<Set<Context>, Map<String, Boolean>> getAllPermissions() {
    return Collections.singletonMap(SubjectData.GLOBAL_CONTEXT,
                                    getPermissions());
  }

  @Override
  @Nonnull
  public Map<String, Boolean> getPermissions(@Nullable Set<Context> contexts) {
    return getPermissions();
  }

  private Map<String, Boolean> getPermissions() {
    Map<String, Boolean> out = new HashMap<>();
    PermissionUser user =
        PermissionAPI.getInstance().getPermissionUtil().loadUser(uniqueID);

    user.getPermissionNodes().forEach(e -> {
      if (!e.isValid()) {
        return;
      }

      out.put(e.getActualPermission(), e.isSet());
    });

    user.getGroups().forEach(e -> {
      if (!e.isValid()) {
        return;
      }

      PermissionGroup group =
          PermissionAPI.getInstance().getPermissionUtil().getGroup(
              e.getGroupName());
      if (group == null) {
        return;
      }

      out.putAll(getPermissionsOf(group));
      group.getSubGroups().forEach(g -> {
        PermissionGroup sub =
            PermissionAPI.getInstance().getPermissionUtil().getGroup(g);
        if (sub == null) {
          return;
        }

        out.putAll(getPermissionsOf(sub));
      });
    });

    return out;
  }

  private Map<String, Boolean> getPermissionsOf(PermissionGroup group) {
    return SubjectGroupPermissionCalculator.getPermissionsOf(group);
  }
}
