package systems.reformcloud.reformcloud2.executor.api.bungee.event;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import systems.reformcloud.reformcloud2.executor.api.bungee.BungeeExecutor;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.packets.out.*;

public final class PlayerListenerHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(final ServerConnectEvent event) {
        final ProxiedPlayer proxiedPlayer = event.getPlayer();
        proxiedPlayer.setReconnectServer(null);
        if (proxiedPlayer.getServer() == null) {
            ProcessInformation lobby = BungeeExecutor.getBestLobbyForPlayer(BungeeExecutor.getInstance().getThisProcessInformation(),
                    proxiedPlayer,
                    proxiedPlayer::hasPermission
            );
            if (lobby != null) {
                event.setTarget(ProxyServer.getInstance().getServerInfo(lobby.getName()));
                return;
            }

            proxiedPlayer.disconnect(TextComponent.fromLegacyText(BungeeExecutor.getInstance().getMessages().format(
                    BungeeExecutor.getInstance().getMessages().getNoHubServerAvailable()
            )));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handle(final ServerConnectedEvent event) {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(sender -> sender.sendPacket(new APIBungeePacketOutPlayerServerSwitch(
                event.getPlayer().getUniqueId(),
                event.getServer().getInfo().getName()
        )));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(final LoginEvent event) {
        PacketSender sender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
        if (sender == null) {
            event.setCancelReason(TextComponent.fromLegacyText("§4§lThe current proxy is not connected to the controller"));
            event.setCancelled(true);
            return;
        }

        if (ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isOnlyProxyJoin()) {
            PendingConnection connection = event.getConnection();
            sender.sendPacket(new APIPacketOutCreateLoginRequest(
                    connection.getUniqueId(),
                    connection.getName()
            ));
        }
    }

    @EventHandler
    public void handle(final PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final ProcessInformation current = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation();
        final PlayerAccessConfiguration configuration = current.getProcessGroup().getPlayerAccessConfiguration();

        if (configuration.isUseCloudPlayerLimit()
                && configuration.getMaxPlayers() < current.getOnlineCount() + 1
                && !player.hasPermission(configuration.getFullJoinPermission())) {
            player.disconnect(TextComponent.fromLegacyText(format(
                    BungeeExecutor.getInstance().getMessages().getProcessFullMessage()
            )));
            return;
        }

        if (configuration.isJoinOnlyPerPermission()
                && configuration.getJoinPermission() != null
                && !player.hasPermission(configuration.getJoinPermission())) {
            player.disconnect(TextComponent.fromLegacyText(format(
                    BungeeExecutor.getInstance().getMessages().getProcessEnterPermissionNotSet()
            )));
            return;
        }

        if (configuration.isMaintenance()
                && configuration.getMaintenanceJoinPermission() != null
                && !player.hasPermission(configuration.getMaintenanceJoinPermission())) {
            player.disconnect(TextComponent.fromLegacyText(format(
                    BungeeExecutor.getInstance().getMessages().getProcessInMaintenanceMessage()
            )));
            return;
        }

        if (current.getProcessState().equals(ProcessState.FULL) && !player.hasPermission("reformcloud.join.full")) {
            player.disconnect(TextComponent.fromLegacyText(format(
                    BungeeExecutor.getInstance().getMessages().getProcessFullMessage()
            )));
            return;
        }

        if (!current.onLogin(event.getPlayer().getUniqueId(), event.getPlayer().getName())) {
            player.disconnect(TextComponent.fromLegacyText(format(
                    BungeeExecutor.getInstance().getMessages().getAlreadyConnectedMessage()
            )));
            return;
        }

        if (ProxyServer.getInstance().getOnlineCount() >= current.getMaxPlayers()
                && !current.getProcessState().equals(ProcessState.FULL)
                && !current.getProcessState().equals(ProcessState.INVISIBLE)) {
            current.setProcessState(ProcessState.FULL);
        }

        current.updateRuntimeInformation();
        BungeeExecutor.getInstance().setThisProcessInformation(current); //Update it directly on the current host to prevent issues
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);

        CommonHelper.EXECUTOR.execute(() -> DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new APIPacketOutPlayerLoggedIn(event.getPlayer().getName()))));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(final ServerKickEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();
        ProcessInformation lobby = BungeeExecutor.getBestLobbyForPlayer(BungeeExecutor.getInstance().getThisProcessInformation(),
                proxiedPlayer,
                proxiedPlayer::hasPermission);
        if (lobby != null) {
            event.setCancelServer(ProxyServer.getInstance().getServerInfo(lobby.getName()));
            event.setCancelled(true);
            return;
        }

        proxiedPlayer.disconnect(TextComponent.fromLegacyText(BungeeExecutor.getInstance().getMessages().format(
                BungeeExecutor.getInstance().getMessages().getNoHubServerAvailable()
        )));
        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(final PlayerDisconnectEvent event) {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new APIPacketOutLogoutPlayer(
                event.getPlayer().getUniqueId(),
                event.getPlayer().getName()
        )));

        CommonHelper.EXECUTOR.execute(() -> {
            ProcessInformation current = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation();
            if (ProxyServer.getInstance().getOnlineCount() < current.getMaxPlayers()
                    && !current.getProcessState().equals(ProcessState.READY)
                    && !current.getProcessState().equals(ProcessState.INVISIBLE)) {
                current.setProcessState(ProcessState.READY);
            }

            current.updateRuntimeInformation();
            current.onLogout(event.getPlayer().getUniqueId());
            BungeeExecutor.getInstance().setThisProcessInformation(current);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
        });
    }

    @EventHandler
    public void handle(final ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer) || !event.isCommand()) {
            return;
        }

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getSender();
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new APIPacketOutPlayerCommandExecute(
                proxiedPlayer.getName(),
                proxiedPlayer.getUniqueId(),
                event.getMessage().replaceFirst("/", "")
        )));
    }

    private String format(String msg) {
        return BungeeExecutor.getInstance().getMessages().format(msg);
    }
}
