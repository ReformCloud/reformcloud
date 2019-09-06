package de.klaro.reformcloud2.executor.api.proxprox.event;

import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import de.klaro.reformcloud2.executor.api.common.event.handler.Listener;
import de.klaro.reformcloud2.executor.api.proxprox.ProxProxExecutor;
import io.gomint.proxprox.api.ProxProx;
import io.gomint.proxprox.api.entity.Player;

import java.util.function.Consumer;

public final class ProcessEventHandler {

    @Listener
    public void handleStart(ProcessStartedEvent event) {
        ProxProx.getProxy().getPlayers().forEach(new Consumer<Player>() {
            @Override
            public void accept(Player player) {
                if (player.hasPermission("reformcloud2.notify")) {
                    player.sendMessage("§7[§a+§7] §6§l" + event.getProcessInformation().getName());
                }
            }
        });
    }

    @Listener
    public void handleUpdate(ProcessUpdatedEvent event) {
        ProxProxExecutor.handleProcessUpdate(event.getProcessInformation());
    }

    @Listener
    public void handleRemove(ProcessStoppedEvent event) {
        ProxProxExecutor.handleProcessRemove(event.getProcessInformation());
    }
}
