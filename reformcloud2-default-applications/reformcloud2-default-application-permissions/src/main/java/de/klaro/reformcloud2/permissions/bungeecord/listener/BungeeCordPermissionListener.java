package de.klaro.reformcloud2.permissions.bungeecord.listener;

import de.klaro.reformcloud2.permissions.PermissionAPI;
import de.klaro.reformcloud2.permissions.util.user.PermissionUser;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeCordPermissionListener implements Listener {

    @EventHandler
    public void handle(final PermissionCheckEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        final PermissionUser permissionUser = PermissionAPI.INSTANCE.getPermissionUtil().loadUser(player.getUniqueId());
        event.setHasPermission(PermissionAPI.INSTANCE.getPermissionUtil().hasPermission(permissionUser, event.getPermission()));
    }

    @EventHandler
    public void handle(final PlayerDisconnectEvent event) {
        PermissionAPI.INSTANCE.getPermissionUtil().handleDisconnect(event.getPlayer().getUniqueId());
    }
}
