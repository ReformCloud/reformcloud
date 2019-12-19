package systems.reformcloud.reformcloud2.executor.api.spigot.event;

import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.packets.out.APIPacketOutHasPlayerAccess;
import systems.reformcloud.reformcloud2.executor.api.spigot.SpigotExecutor;

public final class PlayerListenerHandler implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  public void handle(final PlayerLoginEvent event) {
    if (ExecutorAPI.getInstance()
            .getSyncAPI()
            .getProcessSyncAPI()
            .getThisProcessInformation()
            .getProcessGroup()
            .getPlayerAccessConfiguration()
            .isOnlyProxyJoin()) {
      PacketSender packetSender =
          DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
      if (packetSender == null || !ExecutorAPI.getInstance()
                                       .getSyncAPI()
                                       .getProcessSyncAPI()
                                       .getThisProcessInformation()
                                       .getNetworkInfo()
                                       .isConnected()) {
        event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
        event.setKickMessage(
            "§4§lThe current server is not connected to the controller");
        return;
      }

      Packet result =
          SpigotExecutor.getInstance()
              .packetHandler()
              .getQueryHandler()
              .sendQueryAsync(packetSender, new APIPacketOutHasPlayerAccess(
                                                event.getPlayer().getUniqueId(),
                                                event.getPlayer().getName()))
              .getTask()
              .getUninterruptedly(TimeUnit.SECONDS, 2);
      if (result != null && result.content().getBoolean("access")) {
        event.setResult(PlayerLoginEvent.Result.ALLOWED);
      } else {
        event.setKickMessage(format(SpigotExecutor.getInstance()
                                        .getMessages()
                                        .getAlreadyConnectedMessage()));
        event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
      }
    }

    final Player player = event.getPlayer();
    final ProcessInformation current = ExecutorAPI.getInstance()
                                           .getSyncAPI()
                                           .getProcessSyncAPI()
                                           .getThisProcessInformation();
    final PlayerAccessConfiguration configuration =
        current.getProcessGroup().getPlayerAccessConfiguration();

    if (configuration.isUseCloudPlayerLimit() &&
        configuration.getMaxPlayers() < current.getOnlineCount() + 1 &&
        !player.hasPermission(configuration.getFullJoinPermission())) {
      event.setKickMessage(format(
          SpigotExecutor.getInstance().getMessages().getProcessFullMessage()));
      event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
      return;
    }

    if (configuration.isJoinOnlyPerPermission() &&
        !player.hasPermission(configuration.getJoinPermission())) {
      event.setKickMessage(format(SpigotExecutor.getInstance()
                                      .getMessages()
                                      .getProcessEnterPermissionNotSet()));
      event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
      return;
    }

    if (configuration.isMaintenance() &&
        !player.hasPermission(configuration.getMaintenanceJoinPermission())) {
      event.setKickMessage(format(SpigotExecutor.getInstance()
                                      .getMessages()
                                      .getProcessInMaintenanceMessage()));
      event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
      return;
    }

    if (current.getProcessState().equals(ProcessState.FULL) &&
        !player.hasPermission(configuration.getFullJoinPermission())) {
      event.setKickMessage(format(
          SpigotExecutor.getInstance().getMessages().getProcessFullMessage()));
      event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
      return;
    }

    if (current.isPlayerOnline(player.getUniqueId())) {
      event.setKickMessage(format(SpigotExecutor.getInstance()
                                      .getMessages()
                                      .getAlreadyConnectedMessage()));
      event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
      return;
    }

    if (Bukkit.getOnlinePlayers().size() >= current.getMaxPlayers() &&
        !current.getProcessState().equals(ProcessState.FULL) &&
        !current.getProcessState().equals(ProcessState.INVISIBLE)) {
      current.setProcessState(ProcessState.FULL);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void handle(final PlayerJoinEvent event) {
    final ProcessInformation current = ExecutorAPI.getInstance()
                                           .getSyncAPI()
                                           .getProcessSyncAPI()
                                           .getThisProcessInformation();
    current.onLogin(event.getPlayer().getUniqueId(),
                    event.getPlayer().getName());
    current.updateRuntimeInformation();
    SpigotExecutor.getInstance().setThisProcessInformation(current);
    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void handle(final PlayerQuitEvent event) {
    ProcessInformation current = ExecutorAPI.getInstance()
                                     .getSyncAPI()
                                     .getProcessSyncAPI()
                                     .getThisProcessInformation();
    if (!current.isPlayerOnline(event.getPlayer().getUniqueId())) {
      return;
    }

    if (Bukkit.getOnlinePlayers().size() < current.getMaxPlayers() &&
        !current.getProcessState().equals(ProcessState.READY) &&
        !current.getProcessState().equals(ProcessState.INVISIBLE)) {
      current.setProcessState(ProcessState.READY);
    }

    current.updateRuntimeInformation();
    current.onLogout(event.getPlayer().getUniqueId());
    SpigotExecutor.getInstance().setThisProcessInformation(current);
    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
  }

  private String format(String msg) {
    return SpigotExecutor.getInstance().getMessages().format(msg);
  }
}
