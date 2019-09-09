package de.klaro.reformcloud2.executor.api.velocity.event;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import net.kyori.text.TextComponent;

public final class ExtraListenerHandler {

    @Subscribe (order = PostOrder.LAST)
    public void handle(final ProxyPingEvent event) {
        final ProcessInformation processInformation = ExecutorAPI.getInstance().getThisProcessInformation();
        ServerPing serverPing = event.getPing();
        serverPing = serverPing.asBuilder()
                .description(TextComponent.of(processInformation.getMotd()))
                .onlinePlayers(processInformation.getOnlineCount())
                .maximumPlayers(processInformation.getMaxPlayers())
                .build();
        event.setPing(serverPing);
    }
}
