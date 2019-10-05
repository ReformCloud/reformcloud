package systems.reformcloud.reformcloud2.permissions.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.velocity.permission.DefaultPermissionProvider;

public class VelocityPermissionListener {

    @Subscribe
    public void handle(final PermissionsSetupEvent event) {
        event.setProvider(DefaultPermissionProvider.INSTANCE);
    }

    @Subscribe
    public void handle(final DisconnectEvent event) {
        PermissionAPI.INSTANCE.getPermissionUtil().handleDisconnect(event.getPlayer().getUniqueId());
    }
}
