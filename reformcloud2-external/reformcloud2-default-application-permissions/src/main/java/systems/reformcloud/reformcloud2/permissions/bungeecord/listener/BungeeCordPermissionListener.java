package systems.reformcloud.reformcloud2.permissions.bungeecord.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.util.group.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

public class BungeeCordPermissionListener implements Listener {

  @EventHandler
  public void handle(final LoginEvent event) {
    // Push user into name to unique ID DB
    PermissionAPI.getInstance().getPermissionUtil().loadUser(
        event.getConnection().getUniqueId(), event.getConnection().getName());
  }

  @EventHandler
  public void handle(final PostLoginEvent event) {
    final PermissionUser permissionUser =
        PermissionAPI.getInstance().getPermissionUtil().loadUser(
            event.getPlayer().getUniqueId());
    Task.EXECUTOR.execute(() -> {
      PermissionAPI.getInstance()
          .getPermissionUtil()
          .getDefaultGroups()
          .forEach(e -> {
            if (Links
                    .filterToReference(
                        permissionUser.getGroups(),
                        g -> g.getGroupName().equals(e.getName()))
                    .isPresent()) {
              return;
            }

            permissionUser.getGroups().add(
                new NodeGroup(System.currentTimeMillis(), -1, e.getName()));
          });

      PermissionAPI.getInstance().getPermissionUtil().updateUser(
          permissionUser);
    });
  }

  @EventHandler
  public void handle(final PermissionCheckEvent event) {
    if (!(event.getSender() instanceof ProxiedPlayer)) {
      return;
    }

    final ProxiedPlayer player = (ProxiedPlayer)event.getSender();
    final PermissionUser permissionUser =
        PermissionAPI.getInstance().getPermissionUtil().loadUser(
            player.getUniqueId());
    event.setHasPermission(permissionUser.hasPermission(event.getPermission()));
  }

  @EventHandler
  public void handle(final PlayerDisconnectEvent event) {
    PermissionAPI.getInstance().getPermissionUtil().handleDisconnect(
        event.getPlayer().getUniqueId());
  }
}
