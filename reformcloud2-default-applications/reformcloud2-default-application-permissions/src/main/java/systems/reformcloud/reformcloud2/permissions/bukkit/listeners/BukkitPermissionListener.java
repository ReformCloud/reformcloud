package systems.reformcloud.reformcloud2.permissions.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.bukkit.permissible.DefaultPermissible;

import java.lang.reflect.Field;

public class BukkitPermissionListener implements Listener {

    @EventHandler
    public void handle(final PlayerJoinEvent event) {
        try {
            Field field = event.getPlayer().getClass().getDeclaredField("perm");
            field.setAccessible(true);
            field.set(event.getPlayer(), new DefaultPermissible(event.getPlayer()));
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @EventHandler
    public void handle(final PlayerQuitEvent event) {
        PermissionAPI.INSTANCE.getPermissionUtil().handleDisconnect(event.getPlayer().getUniqueId());
    }
}
