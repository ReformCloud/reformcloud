package de.klaro.reformcloud2.executor.api.bungee.event;

import de.klaro.reformcloud2.executor.api.bungee.BungeeExecutor;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import de.klaro.reformcloud2.executor.api.common.event.handler.Listener;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Version;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.function.Consumer;

public final class ProcessEventHandler {

    @Listener
    public void handleStart(ProcessStartedEvent event) {
        ProxyServer.getInstance().getPlayers().forEach(new Consumer<ProxiedPlayer>() {
            @Override
            public void accept(ProxiedPlayer proxiedPlayer) {
                if (proxiedPlayer.hasPermission("reformcloud2.notify")) {
                    proxiedPlayer.sendMessage(TextComponent.fromLegacyText(
                            "§7[§a+§7] §6§l" + event.getProcessInformation().getName()
                    ));
                }
            }
        });
    }

    @Listener
    public void handleUpdate(ProcessUpdatedEvent event) {
        if (!event.getProcessInformation().getTemplate().isServer() || event.getProcessInformation().getTemplate().getVersion().equals(Version.NUKKIT_X)) {
            return;
        }

        BungeeExecutor.registerServer(event.getProcessInformation());
    }

    @Listener
    public void handleRemove(ProcessStoppedEvent event) {
        ProxyServer.getInstance().getServers().remove(event.getProcessInformation().getName());
        if (event.getProcessInformation().isLobby()) {
            ProxyServer.getInstance().getConfig().getListeners().forEach(new Consumer<ListenerInfo>() {
                @Override
                public void accept(ListenerInfo listenerInfo) {
                    listenerInfo.getServerPriority().remove(event.getProcessInformation().getName());
                }
            });
        }
    }
}
