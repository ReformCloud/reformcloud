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
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.text.TextComponent;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.*;
import systems.reformcloud.reformcloud2.executor.api.velocity.VelocityExecutor;

import java.util.Optional;

public final class PlayerListenerHandler {

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final ServerPreConnectEvent event) {
        final Player player = event.getPlayer();
        if (!player.getCurrentServer().isPresent()) {
            ProcessInformation lobby = VelocityExecutor.getBestLobbyForPlayer(
                    API.getInstance().getCurrentProcessInformation(),
                    player::hasPermission
            );
            if (lobby != null) {
                Optional<RegisteredServer> server = VelocityExecutor.getInstance().getProxyServer().getServer(lobby.getProcessDetail().getName());
                if (!server.isPresent()) {
                    event.setResult(ServerPreConnectEvent.ServerResult.denied());
                    return;
                }

                event.setResult(ServerPreConnectEvent.ServerResult.allowed(server.get()));
            } else {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
            }
        }

        if (event.getResult().getServer().isPresent()) {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(sender -> sender.sendPacket(new APIBungeePacketOutPlayerServerSwitch(
                    event.getPlayer().getUniqueId(),
                    event.getResult().getServer().get().getServerInfo().getName()
            )));
            AbsoluteThread.sleep(20);
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final LoginEvent event) {
        PacketSender sender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
        if (sender == null) {
            event.setResult(ResultedEvent.ComponentResult.denied(TextComponent.of("§4§lThe current proxy is not connected to the controller")));
            return;
        }

        if (API.getInstance().getCurrentProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isOnlyProxyJoin()) {
            Player player = event.getPlayer();
            sender.sendPacket(new APIPacketOutCreateLoginRequest(
                    player.getUniqueId(),
                    player.getUsername()
            ));
        }
    }

    @Subscribe
    public void handle(final PostLoginEvent event) {
        final Player player = event.getPlayer();
        final ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        final PlayerAccessConfiguration configuration = current.getProcessGroup().getPlayerAccessConfiguration();

        if (configuration.isUseCloudPlayerLimit()
                && current.getProcessDetail().getMaxPlayers() < current.getProcessPlayerManager().getOnlineCount() + 1
                && !player.hasPermission(configuration.getFullJoinPermission())) {
            player.disconnect(TextComponent.of("§4§lThe proxy is full"));
            return;
        }

        if (configuration.isJoinOnlyPerPermission() && !player.hasPermission(configuration.getJoinPermission())) {
            player.disconnect(TextComponent.of("§4§lYou do not have permission to enter this proxy"));
            return;
        }

        if (configuration.isMaintenance() && !player.hasPermission(configuration.getMaintenanceJoinPermission())) {
            player.disconnect(TextComponent.of("§4§lThis proxy is currently in maintenance"));
            return;
        }

        if (current.getProcessDetail().getProcessState().equals(ProcessState.FULL) && !player.hasPermission(configuration.getFullJoinPermission())) {
            player.disconnect(TextComponent.of("§4§lYou are not allowed to join this server in the current state"));
            return;
        }

        if (!current.getProcessPlayerManager().onLogin(event.getPlayer().getUniqueId(), event.getPlayer().getUsername())) {
            player.disconnect(TextComponent.of("§4§lYou are not allowed to join this proxy"));
            return;
        }

        if (VelocityExecutor.getInstance().getProxyServer().getPlayerCount() >= current.getProcessDetail().getMaxPlayers()
                && !current.getProcessDetail().getProcessState().equals(ProcessState.FULL)
                && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
            current.getProcessDetail().setProcessState(ProcessState.FULL);
        }

        current.updateRuntimeInformation();
        VelocityExecutor.getInstance().setThisProcessInformation(current); //Update it directly on the current host to prevent issues
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);

        CommonHelper.EXECUTOR.execute(() -> DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new APIPacketOutPlayerLoggedIn(event.getPlayer().getUsername()))));
    }

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final KickedFromServerEvent event) {
        Player player = event.getPlayer();
        ProcessInformation lobby = VelocityExecutor.getBestLobbyForPlayer(
                API.getInstance().getCurrentProcessInformation(),
                player::hasPermission);
        if (lobby != null) {
            Optional<RegisteredServer> server = VelocityExecutor.getInstance().getProxyServer().getServer(lobby.getProcessDetail().getName());
            if (!server.isPresent()) {
                event.setResult(KickedFromServerEvent.DisconnectPlayer.create(TextComponent.of(VelocityExecutor.getInstance().getMessages().format(
                        VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable()
                ))));
                return;
            }

            event.setResult(KickedFromServerEvent.RedirectPlayer.create(server.get()));
            return;
        }

        event.setResult(KickedFromServerEvent.DisconnectPlayer.create(TextComponent.of(VelocityExecutor.getInstance().getMessages().format(
                VelocityExecutor.getInstance().getMessages().getNoHubServerAvailable()
        ))));
    }

    @Subscribe(order = PostOrder.FIRST)
    public void handle(final DisconnectEvent event) {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new APIPacketOutLogoutPlayer(
                event.getPlayer().getUniqueId(),
                event.getPlayer().getUsername()
        )));

        CommonHelper.EXECUTOR.execute(() -> {
            ProcessInformation current = API.getInstance().getCurrentProcessInformation();
            if (VelocityExecutor.getInstance().getProxyServer().getPlayerCount() < current.getProcessDetail().getMaxPlayers()
                    && !current.getProcessDetail().getProcessState().equals(ProcessState.READY)
                    && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
                current.getProcessDetail().setProcessState(ProcessState.READY);
            }

            current.updateRuntimeInformation();
            current.getProcessPlayerManager().onLogout(event.getPlayer().getUniqueId());
            VelocityExecutor.getInstance().setThisProcessInformation(current);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
        });
    }

    @Subscribe
    public void handle(final PlayerChatEvent event) {
        if (API.getInstance().getCurrentProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isPlayerControllerCommandReporting()
                && event.getMessage().startsWith("/")) {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new APIPacketOutPlayerCommandExecute(
                    event.getPlayer().getUsername(),
                    event.getPlayer().getUniqueId(),
                    event.getMessage().replaceFirst("/", "")
            )));
        }
    }

}
