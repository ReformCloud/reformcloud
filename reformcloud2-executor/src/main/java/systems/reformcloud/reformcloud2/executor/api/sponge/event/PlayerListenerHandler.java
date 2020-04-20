package systems.reformcloud.reformcloud2.executor.api.sponge.event;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.sponge.SpongeExecutor;

import java.util.concurrent.TimeUnit;

public final class PlayerListenerHandler {

    @Listener(order = Order.LATE)
    public void handle(final ClientConnectionEvent.Login event) {
        final User player = event.getTargetUser();
        final ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        final PlayerAccessConfiguration configuration = current.getProcessGroup().getPlayerAccessConfiguration();

        if (configuration.isUseCloudPlayerLimit()
                && configuration.getMaxPlayers() < current.getProcessPlayerManager().getOnlineCount() + 1
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

        if (current.getProcessDetail().getProcessState().equals(ProcessState.FULL) && !player.hasPermission(configuration.getFullJoinPermission())) {
            event.setCancelled(true);
            return;
        }

        if (!current.getProcessPlayerManager().onLogin(player.getUniqueId(), player.getName())) {
            event.setCancelled(true);
            return;
        }

        if (Sponge.getServer().getOnlinePlayers().size() >= current.getProcessDetail().getMaxPlayers()
                && !current.getProcessDetail().getProcessState().equals(ProcessState.FULL)
                && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
            current.getProcessDetail().setProcessState(ProcessState.FULL);
        }
    }

    @Listener(order = Order.LATE)
    public void handle(final ClientConnectionEvent.Join event) {
        final ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        current.updateRuntimeInformation();
        SpongeExecutor.getInstance().setThisProcessInformation(current);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
    }

    @Listener(order = Order.FIRST)
    public void handle(final ClientConnectionEvent.Disconnect event) {
        ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        if (!current.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(event.getTargetEntity().getUniqueId())) {
            return;
        }

        Sponge.getScheduler()
                .createSyncExecutor(SpongeExecutor.getInstance().getPlugin())
                .schedule(() -> {
                    if (Sponge.getServer().getOnlinePlayers().size() < current.getProcessDetail().getMaxPlayers()
                            && !current.getProcessDetail().getProcessState().equals(ProcessState.READY)
                            && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
                        current.getProcessDetail().setProcessState(ProcessState.READY);
                    }

                    current.updateRuntimeInformation();
                    current.getProcessPlayerManager().onLogout(event.getTargetEntity().getUniqueId());
                    SpongeExecutor.getInstance().setThisProcessInformation(current);
                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
                }, 20, TimeUnit.MILLISECONDS);
    }
}
