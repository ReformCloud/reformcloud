package systems.reformcloud.reforncloud2.notifications.bungeecord.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import systems.reformcloud.reformcloud2.executor.api.bungee.BungeeExecutor;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.event.priority.EventPriority;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

public class ProcessListener {

    private static final Collection<UUID> STARTED = new LinkedList<>();

    @Listener
    public void handle(final ProcessStartedEvent event) {
        this.publishNotification(
                BungeeExecutor.getInstance().getMessages().getProcessStarted(),
                event.getProcessInformation().getName()
        );
    }

    @Listener (priority = EventPriority.FIRST)
    public void handle(final ProcessUpdatedEvent event) {
        ProcessInformation processInformation = event.getProcessInformation();
        if (isNotify(processInformation)) {
            STARTED.add(processInformation.getProcessDetail().getProcessUniqueID());
            this.publishNotification(
                    BungeeExecutor.getInstance().getMessages().getProcessConnected(),
                    processInformation.getName()
            );
        }
    }

    @Listener
    public void handle(final ProcessStoppedEvent event) {
        this.publishNotification(
                BungeeExecutor.getInstance().getMessages().getProcessStopped(),
                event.getProcessInformation().getName()
        );
        STARTED.remove(event.getProcessInformation().getProcessDetail().getProcessUniqueID());
    }

    private void publishNotification(String message, Object... replacements) {
        final String replacedMessage = BungeeExecutor.getInstance().getMessages().format(message, replacements);
        ProxyServer.getInstance().getPlayers().forEach(player -> {
            if (player.hasPermission("reformcloud.notify")) {
                player.sendMessage(TextComponent.fromLegacyText(replacedMessage));
            }
        });
    }

    private boolean isNotify(ProcessInformation information) {
        return !STARTED.contains(information.getProcessDetail().getProcessUniqueID())
                && ProxyServer.getInstance().getServerInfo(information.getName()) == null
                && information.getNetworkInfo().isConnected();
    }
}
