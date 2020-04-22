package systems.reformcloud.reforncloud2.notifications.velocity.listener;

import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.velocity.VelocityExecutor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ProcessListener {

    private static final Map<UUID, ProcessInformation> REGISTERED = new ConcurrentHashMap<>();

    public ProcessListener(@NotNull ProxyServer server) {
        Task.EXECUTOR.execute(() -> {
            while (DefaultChannelManager.INSTANCE.get("Controller").isEmpty()) {
                AbsoluteThread.sleep(50);
            }

            ExecutorAPI.getInstance()
                    .getSyncAPI()
                    .getProcessSyncAPI()
                    .getAllProcesses()
                    .forEach(e -> REGISTERED.put(e.getProcessDetail().getProcessUniqueID(), e));
        });

        this.proxyServer = server;
    }

    private final ProxyServer proxyServer;

    @Listener
    public void handle(final ProcessStartedEvent event) {
        this.publishNotification(
                VelocityExecutor.getInstance().getMessages().getProcessStarted(),
                event.getProcessInformation().getProcessDetail().getName()
        );
    }

    @Listener
    public void handle(final ProcessStoppedEvent event) {
        if (!REGISTERED.containsKey(event.getProcessInformation().getProcessDetail().getProcessUniqueID())) {
            return;
        }

        this.publishNotification(
                VelocityExecutor.getInstance().getMessages().getProcessStopped(),
                event.getProcessInformation().getProcessDetail().getName()
        );
        REGISTERED.remove(event.getProcessInformation().getProcessDetail().getProcessUniqueID());
    }

    @Listener
    public void handle(final ProcessUpdatedEvent event) {
        ProcessInformation old = REGISTERED.put(
                event.getProcessInformation().getProcessDetail().getProcessUniqueID(),
                event.getProcessInformation()
        );
        if (old != null) {
            if (!old.getNetworkInfo().isConnected() && event.getProcessInformation().getNetworkInfo().isConnected()) {
                this.publishNotification(
                        VelocityExecutor.getInstance().getMessages().getProcessConnected(),
                        event.getProcessInformation().getProcessDetail().getName()
                );
            }

            return;
        }

        this.publishNotification(
                VelocityExecutor.getInstance().getMessages().getProcessRegistered(),
                event.getProcessInformation().getProcessDetail().getName()
        );
    }

    private void publishNotification(String message, Object... replacements) {
        String replacedMessage = VelocityExecutor.getInstance().getMessages().format(message, replacements);
        this.proxyServer.getAllPlayers()
                .stream()
                .filter(e -> e.hasPermission("reformcloud.notify"))
                .forEach(player -> player.sendMessage(TextComponent.of(replacedMessage)));
    }
}
