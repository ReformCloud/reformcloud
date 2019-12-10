package systems.reformcloud.reformcloud2.executor.api.nukkit.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.nukkit.NukkitExecutor;
import systems.reformcloud.reformcloud2.executor.api.packets.out.APIPacketOutHasPlayerAccess;

import java.util.concurrent.TimeUnit;

public final class PlayerListenerHandler implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void handle(final PlayerLoginEvent event) {
        if (ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isOnlyProxyJoin()) {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            if (packetSender == null) {
                event.setCancelled(true);
                event.setKickMessage("§4§lThe current server is not connected to the controller");
                return;
            }

            Packet result = NukkitExecutor.getInstance().packetHandler().getQueryHandler().sendQueryAsync(packetSender, new APIPacketOutHasPlayerAccess(
                    event.getPlayer().getUniqueId(),
                    event.getPlayer().getName()
            )).getTask().getUninterruptedly(TimeUnit.SECONDS, 2);
            if (result != null && result.content().getBoolean("access")) {
                event.setCancelled(false);
            } else {
                event.setKickMessage(format(
                        NukkitExecutor.getInstance().getMessages().getAlreadyConnectedMessage()
                ));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void handle(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final ProcessInformation current = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation();
        final PlayerAccessConfiguration configuration = current.getProcessGroup().getPlayerAccessConfiguration();

        if (configuration.isUseCloudPlayerLimit()
                && configuration.getMaxPlayers() < current.getOnlineCount() + 1
                && !player.hasPermission(configuration.getJoinPermission())) {
            player.kick(format(
                    NukkitExecutor.getInstance().getMessages().getProcessFullMessage()
            ));
            return;
        }

        if (configuration.isJoinOnlyPerPermission()
                && configuration.getJoinPermission() != null
                && !player.hasPermission(configuration.getJoinPermission())) {
            player.kick(format(
                    NukkitExecutor.getInstance().getMessages().getProcessEnterPermissionNotSet()
            ));
            return;
        }

        if (configuration.isMaintenance()
                && configuration.getMaintenanceJoinPermission() != null
                && !player.hasPermission(configuration.getMaintenanceJoinPermission())) {
            player.kick(format(
                    NukkitExecutor.getInstance().getMessages().getProcessInMaintenanceMessage()
            ));
            return;
        }

        if (current.getProcessState().equals(ProcessState.FULL) && !player.hasPermission("reformcloud.join.full")) {
            player.kick(format(
                    NukkitExecutor.getInstance().getMessages().getProcessFullMessage()
            ));
            return;
        }

        if (!current.onLogin(player.getUniqueId(), player.getName())) {
            player.kick(format(
                    NukkitExecutor.getInstance().getMessages().getAlreadyConnectedMessage()
            ));
            return;
        }

        if (Server.getInstance().getOnlinePlayers().size() >= current.getMaxPlayers()
                && !current.getProcessState().equals(ProcessState.FULL)
                && !current.getProcessState().equals(ProcessState.INVISIBLE)) {
            current.setProcessState(ProcessState.FULL);
        }

        current.updateRuntimeInformation();
        NukkitExecutor.getInstance().setThisProcessInformation(current);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void handle(final PlayerQuitEvent event) {
        ProcessInformation current = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation();
        if (!current.isPlayerOnline(event.getPlayer().getUniqueId())) {
            return;
        }

        if (Server.getInstance().getOnlinePlayers().size() < current.getMaxPlayers()
                && !current.getProcessState().equals(ProcessState.READY)
                && !current.getProcessState().equals(ProcessState.INVISIBLE)) {
            current.setProcessState(ProcessState.READY);
        }

        current.updateRuntimeInformation();
        current.onLogout(event.getPlayer().getUniqueId());
        NukkitExecutor.getInstance().setThisProcessInformation(current);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
    }

    private String format(String msg) {
        return NukkitExecutor.getInstance().getMessages().format(msg);
    }
}
