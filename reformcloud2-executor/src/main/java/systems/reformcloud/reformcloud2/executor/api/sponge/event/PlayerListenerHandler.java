package systems.reformcloud.reformcloud2.executor.api.sponge.event;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.packets.out.APIPacketOutHasPlayerAccess;
import systems.reformcloud.reformcloud2.executor.api.sponge.SpongeExecutor;

import java.util.concurrent.TimeUnit;

public final class PlayerListenerHandler {

    @Listener (order = Order.LATE)
    public void handle(final ClientConnectionEvent.Login event) {
        if (ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isOnlyProxyJoin()) {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            if (packetSender == null || !ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation().getNetworkInfo().isConnected()) {
                event.setCancelled(true);
                return;
            }

            Packet result = SpongeExecutor.getInstance().packetHandler().getQueryHandler().sendQueryAsync(packetSender, new APIPacketOutHasPlayerAccess(
                    event.getProfile().getUniqueId(),
                    event.getProfile().getName().get()
            )).getTask().getUninterruptedly(TimeUnit.SECONDS, 2);
            if (result != null && result.content().getBoolean("access")) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        }

        final User player = event.getTargetUser();
        final ProcessInformation current = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation();
        final PlayerAccessConfiguration configuration = current.getProcessGroup().getPlayerAccessConfiguration();

        if (configuration.isUseCloudPlayerLimit()
                && configuration.getMaxPlayers() < current.getOnlineCount() + 1
                && !player.hasPermission(configuration.getFullJoinPermission())) {
            event.setCancelled(true);
            return;
        }

        if (configuration.isJoinOnlyPerPermission() && !player.hasPermission(configuration.getJoinPermission())) {
            event.setCancelled(true);
            return;
        }

        if (configuration.isMaintenance() && !player.hasPermission(configuration.getMaintenanceJoinPermission())) {
            event.setCancelled(true);
            return;
        }

        if (current.getProcessState().equals(ProcessState.FULL) && !player.hasPermission(configuration.getFullJoinPermission())) {
            event.setCancelled(true);
            return;
        }

        if (!current.onLogin(player.getUniqueId(), player.getName())) {
            event.setCancelled(true);
            return;
        }

        if (Sponge.getServer().getOnlinePlayers().size() >= current.getMaxPlayers()
                && !current.getProcessState().equals(ProcessState.FULL)
                && !current.getProcessState().equals(ProcessState.INVISIBLE)) {
            current.setProcessState(ProcessState.FULL);
        }
    }

    @Listener (order = Order.LATE)
    public void handle(final ClientConnectionEvent.Join event) {
        final ProcessInformation current = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation();
        current.updateRuntimeInformation();
        SpongeExecutor.getInstance().setThisProcessInformation(current);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
    }

    @Listener (order = Order.FIRST)
    public void handle(final ClientConnectionEvent.Disconnect event) {
        ProcessInformation current = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation();
        if (!current.isPlayerOnline(event.getTargetEntity().getUniqueId())) {
            return;
        }

        if (Sponge.getServer().getOnlinePlayers().size() < current.getMaxPlayers()
                && !current.getProcessState().equals(ProcessState.READY)
                && !current.getProcessState().equals(ProcessState.INVISIBLE)) {
            current.setProcessState(ProcessState.READY);
        }

        current.updateRuntimeInformation();
        current.onLogout(event.getTargetEntity().getUniqueId());
        SpongeExecutor.getInstance().setThisProcessInformation(current);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
    }
}
