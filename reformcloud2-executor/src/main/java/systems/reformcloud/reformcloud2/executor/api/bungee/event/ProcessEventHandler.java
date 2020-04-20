package systems.reformcloud.reformcloud2.executor.api.bungee.event;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.bungee.BungeeExecutor;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

public final class ProcessEventHandler {

    @Listener
    public void handleStart(final @NotNull ProcessStartedEvent event) {
        BungeeExecutor.registerServer(event.getProcessInformation());
    }

    @Listener
    public void handleUpdate(final @NotNull ProcessUpdatedEvent event) {
        BungeeExecutor.registerServer(event.getProcessInformation());

        ProcessInformation old = Streams.filter(BungeeExecutor.LOBBY_SERVERS, e -> e.equals(event.getProcessInformation()));
        if (old != null) {
            BungeeExecutor.LOBBY_SERVERS.remove(old);
            BungeeExecutor.LOBBY_SERVERS.add(event.getProcessInformation());
        }
    }

    @Listener
    public void handleRemove(final @NotNull ProcessStoppedEvent event) {
        BungeeExecutor.unregisterServer(event.getProcessInformation());
    }
}
