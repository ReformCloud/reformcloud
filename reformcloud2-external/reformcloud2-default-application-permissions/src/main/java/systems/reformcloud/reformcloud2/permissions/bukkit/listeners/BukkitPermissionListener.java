package systems.reformcloud.reformcloud2.permissions.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.bukkit.BukkitUtil;

public class BukkitPermissionListener implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void handle(final PlayerLoginEvent event) {
        BukkitUtil.injectPlayer(event.getPlayer());
    }

    @EventHandler
    public void handle(final PlayerQuitEvent event) {
        PermissionAPI.getInstance().getPermissionUtil().handleDisconnect(event.getPlayer().getUniqueId());
    }
}
