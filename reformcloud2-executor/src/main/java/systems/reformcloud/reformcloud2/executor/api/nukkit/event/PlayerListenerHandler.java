package systems.reformcloud.reformcloud2.executor.api.nukkit.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIPacketOutHasPlayerAccess;
import systems.reformcloud.reformcloud2.executor.api.nukkit.NukkitExecutor;

import java.util.concurrent.TimeUnit;

public final class PlayerListenerHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(final PlayerLoginEvent event) {
        if (API.getInstance().getCurrentProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isOnlyProxyJoin()) {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            if (packetSender == null) {
                event.setCancelled(true);
                event.setKickMessage("§4§lThe current server is not connected to the controller");
                return;
            }

            Packet result = NukkitExecutor.getInstance().packetHandler().getQueryHandler().sendQueryAsync(packetSender, new APIPacketOutHasPlayerAccess(
                    event.getPlayer().getUniqueId(),
                    event.getPlayer().getName(),
                    event.getPlayer().getAddress()
            )).getTask().getUninterruptedly(TimeUnit.SECONDS, 5);
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        final PlayerAccessConfiguration configuration = current.getProcessGroup().getPlayerAccessConfiguration();

        if (configuration.isUseCloudPlayerLimit()
                && current.getProcessDetail().getMaxPlayers() < current.getProcessPlayerManager().getOnlineCount() + 1
                && !player.hasPermission(configuration.getFullJoinPermission())) {
            player.kick(format(
                    NukkitExecutor.getInstance().getMessages().getProcessFullMessage()
            ));
            return;
        }

        if (configuration.isJoinOnlyPerPermission() && !player.hasPermission(configuration.getJoinPermission())) {
            player.kick(format(
                    NukkitExecutor.getInstance().getMessages().getProcessEnterPermissionNotSet()
            ));
            return;
        }

        if (configuration.isMaintenance() && !player.hasPermission(configuration.getMaintenanceJoinPermission())) {
            player.kick(format(
                    NukkitExecutor.getInstance().getMessages().getProcessInMaintenanceMessage()
            ));
            return;
        }

        if (current.getProcessDetail().getProcessState().equals(ProcessState.FULL) && !player.hasPermission(configuration.getFullJoinPermission())) {
            player.kick(format(
                    NukkitExecutor.getInstance().getMessages().getProcessFullMessage()
            ));
            return;
        }

        if (!current.getProcessPlayerManager().onLogin(player.getUniqueId(), player.getName())) {
            player.kick(format(
                    NukkitExecutor.getInstance().getMessages().getAlreadyConnectedMessage()
            ));
            return;
        }

        if (Server.getInstance().getOnlinePlayers().size() >= current.getProcessDetail().getMaxPlayers()
                && !current.getProcessDetail().getProcessState().equals(ProcessState.FULL)
                && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
            current.getProcessDetail().setProcessState(ProcessState.FULL);
        }

        current.updateRuntimeInformation();
        NukkitExecutor.getInstance().setThisProcessInformation(current);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(final PlayerQuitEvent event) {
        ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        if (!current.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(event.getPlayer().getUniqueId())) {
            return;
        }

        if (Server.getInstance().getOnlinePlayers().size() < current.getProcessDetail().getMaxPlayers()
                && !current.getProcessDetail().getProcessState().equals(ProcessState.READY)
                && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
            current.getProcessDetail().setProcessState(ProcessState.READY);
        }

        current.updateRuntimeInformation();
        current.getProcessPlayerManager().onLogout(event.getPlayer().getUniqueId());
        NukkitExecutor.getInstance().setThisProcessInformation(current);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
    }

    private String format(String msg) {
        return NukkitExecutor.getInstance().getMessages().format(msg);
    }
}
