package systems.reformcloud.reformcloud2.permissions.velocity.permission;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.proxy.Player;

public class DefaultPermissionProvider implements PermissionProvider {

    public static final DefaultPermissionProvider INSTANCE = new DefaultPermissionProvider();

    private DefaultPermissionProvider() {}

    @Override
    public PermissionFunction createFunction(PermissionSubject permissionSubject) {
        if (permissionSubject instanceof Player) {
            final Player player = (Player) permissionSubject;
            return new DefaultPermissionFunction(player);
        }

        return null;
    }
}
