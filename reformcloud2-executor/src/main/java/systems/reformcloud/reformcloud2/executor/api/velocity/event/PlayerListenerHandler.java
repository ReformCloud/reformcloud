package systems.reformcloud.reformcloud2.executor.api.velocity.event;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.text.TextComponent;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.packets.out.APIPacketOutCreateLoginRequest;
import systems.reformcloud.reformcloud2.executor.api.packets.out.APIPacketOutLogoutPlayer;
import systems.reformcloud.reformcloud2.executor.api.packets.out.APIPacketOutPlayerCommandExecute;
import systems.reformcloud.reformcloud2.executor.api.packets.out.APIPacketOutPlayerLoggedIn;
import systems.reformcloud.reformcloud2.executor.api.velocity.VelocityExecutor;

public final class PlayerListenerHandler {

  @Subscribe(order = PostOrder.FIRST)
  public void handleConnect(final ServerPreConnectEvent event) {
    final Player player = event.getPlayer();
    ProcessInformation lobby = VelocityExecutor.getBestLobbyForPlayer(
        VelocityExecutor.getInstance().getThisProcessInformation(), player,
        player::hasPermission);
    if (lobby != null) {
      event.setResult(ServerPreConnectEvent.ServerResult.allowed(
          VelocityExecutor.getInstance()
              .getProxyServer()
              .getServer(lobby.getName())
              .get()));
      return;
    }

    event.setResult(ServerPreConnectEvent.ServerResult.denied());
  }

  @Subscribe(order = PostOrder.FIRST)
  public void handle(final LoginEvent event) {
    PacketSender sender =
        DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
    if (sender == null) {
      event.setResult(ResultedEvent.ComponentResult.denied(TextComponent.of(
          "§4§lThe current proxy is not connected to the controller")));
      return;
    }

    if (ExecutorAPI.getInstance()
            .getSyncAPI()
            .getProcessSyncAPI()
            .getThisProcessInformation()
            .getProcessGroup()
            .getPlayerAccessConfiguration()
            .isOnlyProxyJoin()) {
      Player player = event.getPlayer();
      sender.sendPacket(new APIPacketOutCreateLoginRequest(
          player.getUniqueId(), player.getUsername()));
    }
  }

  @Subscribe
  public void handle(final PostLoginEvent event) {
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
      player.disconnect(TextComponent.of("§4§lThe proxy is full"));
      return;
    }

    if (configuration.isJoinOnlyPerPermission() &&
        !player.hasPermission(configuration.getJoinPermission())) {
      player.disconnect(TextComponent.of(
          "§4§lYou do not have permission to enter this proxy"));
      return;
    }

    if (configuration.isMaintenance() &&
        !player.hasPermission(configuration.getMaintenanceJoinPermission())) {
      player.disconnect(
          TextComponent.of("§4§lThis proxy is currently in maintenance"));
      return;
    }

    if (current.getProcessState().equals(ProcessState.FULL) &&
        !player.hasPermission(configuration.getFullJoinPermission())) {
      player.disconnect(TextComponent.of(
          "§4§lYou are not allowed to join this server in the current state"));
      return;
    }

    if (!current.onLogin(event.getPlayer().getUniqueId(),
                         event.getPlayer().getUsername())) {
      player.disconnect(
          TextComponent.of("§4§lYou are not allowed to join this proxy"));
      return;
    }

    if (VelocityExecutor.getInstance().getProxyServer().getPlayerCount() >=
            current.getMaxPlayers() &&
        !current.getProcessState().equals(ProcessState.FULL) &&
        !current.getProcessState().equals(ProcessState.INVISIBLE)) {
      current.setProcessState(ProcessState.FULL);
    }

    current.updateRuntimeInformation();
    VelocityExecutor.getInstance().setThisProcessInformation(
        current); // Update it directly on the current host to prevent issues
    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);

    CommonHelper.EXECUTOR.execute(
        ()
            -> DefaultChannelManager.INSTANCE.get("Controller")
                   .ifPresent(packetSender
                              -> packetSender.sendPacket(
                                  new APIPacketOutPlayerLoggedIn(
                                      event.getPlayer().getUsername()))));
  }

  @Subscribe(order = PostOrder.FIRST)
  public void handle(final KickedFromServerEvent event) {
    Player player = event.getPlayer();
    ProcessInformation lobby = VelocityExecutor.getBestLobbyForPlayer(
        VelocityExecutor.getInstance().getThisProcessInformation(), player,
        player::hasPermission);
    if (lobby != null) {
      event.setResult(KickedFromServerEvent.RedirectPlayer.create(
          VelocityExecutor.getInstance()
              .getProxyServer()
              .getServer(lobby.getName())
              .get()));
      return;
    }

    event.setResult(KickedFromServerEvent.DisconnectPlayer.create(
        TextComponent.of(VelocityExecutor.getInstance().getMessages().format(
            VelocityExecutor.getInstance()
                .getMessages()
                .getNoHubServerAvailable()))));
  }

  @Subscribe(order = PostOrder.FIRST)
  public void handle(final DisconnectEvent event) {
    DefaultChannelManager.INSTANCE.get("Controller")
        .ifPresent(packetSender
                   -> packetSender.sendPacket(new APIPacketOutLogoutPlayer(
                       event.getPlayer().getUniqueId(),
                       event.getPlayer().getUsername())));

    CommonHelper.EXECUTOR.execute(() -> {
      ProcessInformation current = ExecutorAPI.getInstance()
                                       .getSyncAPI()
                                       .getProcessSyncAPI()
                                       .getThisProcessInformation();
      if (VelocityExecutor.getInstance().getProxyServer().getPlayerCount() <
              current.getMaxPlayers() &&
          !current.getProcessState().equals(ProcessState.READY) &&
          !current.getProcessState().equals(ProcessState.INVISIBLE)) {
        current.setProcessState(ProcessState.READY);
      }

      current.updateRuntimeInformation();
      current.onLogout(event.getPlayer().getUniqueId());
      VelocityExecutor.getInstance().setThisProcessInformation(current);
      ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(
          current);
    });
  }

  @Subscribe
  public void handle(final PlayerChatEvent event) {
    if (ExecutorAPI.getInstance()
            .getSyncAPI()
            .getProcessSyncAPI()
            .getThisProcessInformation()
            .getProcessGroup()
            .getPlayerAccessConfiguration()
            .isPlayerControllerCommandReporting() &&
        event.getMessage().startsWith("/")) {
      DefaultChannelManager.INSTANCE.get("Controller")
          .ifPresent(
              packetSender
              -> packetSender.sendPacket(new APIPacketOutPlayerCommandExecute(
                  event.getPlayer().getUsername(),
                  event.getPlayer().getUniqueId(),
                  event.getMessage().replaceFirst("/", ""))));
    }
  }
}
