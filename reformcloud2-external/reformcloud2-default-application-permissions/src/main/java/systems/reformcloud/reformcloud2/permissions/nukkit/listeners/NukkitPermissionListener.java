package systems.reformcloud.reformcloud2.permissions.nukkit.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import java.lang.reflect.Field;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.nukkit.permissible.DefaultPermissible;

public class NukkitPermissionListener implements Listener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void handle(final PlayerLoginEvent event) {
    try {
      Field field = Player.class.getDeclaredField("perm");
      field.setAccessible(true);
      field.set(event.getPlayer(), new DefaultPermissible(event.getPlayer()));
    } catch (final Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  @EventHandler
  public void handle(final PlayerQuitEvent event) {
    PermissionAPI.getInstance().getPermissionUtil().handleDisconnect(
        event.getPlayer().getUniqueId());
  }
}
