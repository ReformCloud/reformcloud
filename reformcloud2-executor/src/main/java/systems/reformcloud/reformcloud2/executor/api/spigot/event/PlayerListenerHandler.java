package systems.reformcloud.reformcloud2.executor.api.spigot.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIPacketOutHasPlayerAccess;
import systems.reformcloud.reformcloud2.executor.api.spigot.SpigotExecutor;

import java.util.concurrent.TimeUnit;

public final class PlayerListenerHandler implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void handle(final PlayerLoginEvent event) {
        if (API.getInstance().getCurrentProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isOnlyProxyJoin()) {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            if (packetSender == null || !API.getInstance().getCurrentProcessInformation().getNetworkInfo().isConnected()) {
                event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
                event.setKickMessage("§4§lThe current server is not connected to the controller");
                return;
            }

            Packet result = SpigotExecutor.getInstance().packetHandler().getQueryHandler().sendQueryAsync(packetSender, new APIPacketOutHasPlayerAccess(
                    event.getPlayer().getUniqueId(),
                    event.getPlayer().getName(),
                    event.getRealAddress().getHostAddress()
            )).getTask().getUninterruptedly(TimeUnit.SECONDS, 5);
            if (result != null && result.content().getBoolean("access")) {
                event.setResult(PlayerLoginEvent.Result.ALLOWED);
            } else {
                event.setKickMessage(format(
                        SpigotExecutor.getInstance().getMessages().getAlreadyConnectedMessage()
                ));
                event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            }
        }

        final Player player = event.getPlayer();
        final ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        final PlayerAccessConfiguration configuration = current.getProcessGroup().getPlayerAccessConfiguration();

        if (configuration.isUseCloudPlayerLimit()
                && configuration.getMaxPlayers() < current.getProcessPlayerManager().getOnlineCount() + 1
                && !player.hasPermission(configuration.getFullJoinPermission())) {
            event.setKickMessage(format(
                    SpigotExecutor.getInstance().getMessages().getProcessFullMessage()
            ));
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            return;
        }

        if (configuration.isJoinOnlyPerPermission() && !player.hasPermission(configuration.getJoinPermission())) {
            event.setKickMessage(format(
                    SpigotExecutor.getInstance().getMessages().getProcessEnterPermissionNotSet()
            ));
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            return;
        }

        if (configuration.isMaintenance() && !player.hasPermission(configuration.getMaintenanceJoinPermission())) {
            event.setKickMessage(format(
                    SpigotExecutor.getInstance().getMessages().getProcessInMaintenanceMessage()
            ));
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            return;
        }

        if (current.getProcessDetail().getProcessState().equals(ProcessState.FULL) && !player.hasPermission(configuration.getFullJoinPermission())) {
            event.setKickMessage(format(
                    SpigotExecutor.getInstance().getMessages().getProcessFullMessage()
            ));
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            return;
        }

        if (current.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(player.getUniqueId())) {
            event.setKickMessage(format(
                    SpigotExecutor.getInstance().getMessages().getAlreadyConnectedMessage()
            ));
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            return;
        }

        if (Bukkit.getOnlinePlayers().size() >= current.getProcessDetail().getMaxPlayers()
                && !current.getProcessDetail().getProcessState().equals(ProcessState.FULL)
                && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
            current.getProcessDetail().setProcessState(ProcessState.FULL);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void handle(final PlayerJoinEvent event) {
        final ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        current.getProcessPlayerManager().onLogin(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        current.updateRuntimeInformation();
        SpigotExecutor.getInstance().setThisProcessInformation(current);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void handle(final PlayerQuitEvent event) {
        ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        if (!current.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(event.getPlayer().getUniqueId())) {
            return;
        }

        if (Bukkit.getOnlinePlayers().size() < current.getProcessDetail().getMaxPlayers()
                && !current.getProcessDetail().getProcessState().equals(ProcessState.READY)
                && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
            current.getProcessDetail().setProcessState(ProcessState.READY);
        }

        current.updateRuntimeInformation();
        current.getProcessPlayerManager().onLogout(event.getPlayer().getUniqueId());
        SpigotExecutor.getInstance().setThisProcessInformation(current);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
    }

    private String format(String msg) {
        return SpigotExecutor.getInstance().getMessages().format(msg);
    }
}
