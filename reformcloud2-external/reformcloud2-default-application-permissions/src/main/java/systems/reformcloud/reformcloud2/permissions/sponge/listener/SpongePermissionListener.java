package systems.reformcloud.reformcloud2.permissions.sponge.listener;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;

public class SpongePermissionListener {

    @Listener
    public void handle(final ClientConnectionEvent.Disconnect event) {
        PermissionAPI.getInstance().getPermissionUtil().handleDisconnect(event.getTargetEntity().getUniqueId());
    }
}
