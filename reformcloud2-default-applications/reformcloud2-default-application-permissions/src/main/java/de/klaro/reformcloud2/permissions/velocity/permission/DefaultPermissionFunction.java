package de.klaro.reformcloud2.permissions.velocity.permission;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import de.klaro.reformcloud2.permissions.PermissionAPI;
import de.klaro.reformcloud2.permissions.util.user.PermissionUser;

import java.util.UUID;

public class DefaultPermissionFunction implements PermissionFunction {

    DefaultPermissionFunction(Player player) {
        this.uuid = player.getUniqueId();
    }

    private final UUID uuid;

    @Override
    public Tristate getPermissionValue(String s) {
        if (s == null) {
            return Tristate.FALSE;
        }

        final PermissionUser permissionUser = PermissionAPI.INSTANCE.getPermissionUtil().loadUser(uuid);
        return PermissionAPI.INSTANCE.getPermissionUtil().hasPermission(permissionUser, s) ? Tristate.TRUE : Tristate.FALSE;
    }
}
