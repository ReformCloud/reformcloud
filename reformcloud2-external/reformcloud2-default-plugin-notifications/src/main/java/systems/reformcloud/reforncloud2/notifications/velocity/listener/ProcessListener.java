package systems.reformcloud.reforncloud2.notifications.velocity.listener;

import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.TextComponent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.event.priority.EventPriority;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.velocity.VelocityExecutor;
import systems.reformcloud.reforncloud2.notifications.velocity.VelocityPlugin;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

public class ProcessListener {

    private static final Collection<UUID> STARTED = new LinkedList<>();

    public ProcessListener(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    private final ProxyServer proxyServer;

    @Listener
    public void handle(final ProcessStartedEvent event) {
        this.publishNotification(
                VelocityExecutor.getInstance().getMessages().getProcessStarted(),
                event.getProcessInformation().getName()
        );
    }

    @Listener (priority = EventPriority.FIRST)
    public void handle(final ProcessUpdatedEvent event) {
        ProcessInformation processInformation = event.getProcessInformation();
        if (isNotify(processInformation)) {
            STARTED.add(processInformation.getProcessUniqueID());
            this.publishNotification(
                    VelocityExecutor.getInstance().getMessages().getProcessConnected(),
                    processInformation.getName()
            );
        }
    }

    @Listener
    public void handle(final ProcessStoppedEvent event) {
        this.publishNotification(
                VelocityExecutor.getInstance().getMessages().getProcessStopped(),
                event.getProcessInformation().getName()
        );
        STARTED.remove(event.getProcessInformation().getProcessUniqueID());
    }

    private void publishNotification(String message, Object... replacements) {
        final String replacedMessage = VelocityExecutor.getInstance().getMessages().format(message, replacements);
        proxyServer.getAllPlayers().forEach(player -> {
            if (player.hasPermission("reformcloud.notify")) {
                player.sendMessage(TextComponent.of(replacedMessage));
            }
        });
    }

    private boolean isNotify(ProcessInformation information) {
        return !STARTED.contains(information.getProcessUniqueID())
                && !VelocityPlugin.proxyServer.getServer(information.getName()).isPresent()
                && information.getNetworkInfo().isConnected();
    }
}
