package systems.reformcloud.reformcloud2.permissions.velocity.permission;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import java.util.UUID;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

public class DefaultPermissionFunction implements PermissionFunction {

  DefaultPermissionFunction(Player player) { this.uuid = player.getUniqueId(); }

  private final UUID uuid;

  @Override
  public Tristate getPermissionValue(String s) {
    if (s == null) {
      return Tristate.FALSE;
    }

    final PermissionUser permissionUser =
        PermissionAPI.getInstance().getPermissionUtil().loadUser(uuid);
    return permissionUser.hasPermission(s) ? Tristate.TRUE : Tristate.FALSE;
  }
}
