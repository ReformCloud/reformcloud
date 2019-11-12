package systems.reformcloud.reformcloud2.permissions.nukkit.listeners;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.nukkit.permissible.DefaultPermissible;

import java.lang.reflect.Field;

public class NukkitPermissionListener implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void handle(final PlayerLoginEvent event) {
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
