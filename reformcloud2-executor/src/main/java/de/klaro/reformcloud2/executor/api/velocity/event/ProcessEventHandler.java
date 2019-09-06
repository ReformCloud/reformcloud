package de.klaro.reformcloud2.executor.api.velocity.event;

import com.velocitypowered.api.proxy.Player;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import de.klaro.reformcloud2.executor.api.common.event.handler.Listener;
import de.klaro.reformcloud2.executor.api.velocity.VelocityExecutor;
import net.kyori.text.TextComponent;

import java.util.function.Consumer;

public final class ProcessEventHandler {

    @Listener
    public void handleStart(ProcessStartedEvent event) {
        VelocityExecutor.getInstance().getProxyServer().getAllPlayers().forEach(new Consumer<Player>() {
            @Override
            public void accept(Player player) {
                if (player.hasPermission("reformcloud2.notify")) {
                    player.sendMessage(TextComponent.of("§7[§a+§7] §6§l" + event.getProcessInformation().getName()));
                }
            }
        });
    }

    @Listener
    public void handleUpdate(ProcessUpdatedEvent event) {
        VelocityExecutor.getInstance().handleProcessUpdate(event.getProcessInformation());
    }

    @Listener
    public void handleRemove(ProcessStoppedEvent event) {
        VelocityExecutor.getInstance().handleProcessRemove(event.getProcessInformation());
    }
}
