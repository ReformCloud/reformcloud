package systems.reformcloud.reformcloud2.executor.api.velocity.event;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.text.TextComponent;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public final class ExtraListenerHandler {

    @Subscribe (order = PostOrder.LAST)
    public void handle(final ProxyPingEvent event) {
        final ProcessInformation processInformation = ExecutorAPI.getInstance().getThisProcessInformation();
        if (processInformation.getMotd() == null) {
            return;
        }

        ServerPing serverPing = event.getPing();
        serverPing = serverPing.asBuilder()
                .description(TextComponent.of(processInformation.getMotd().replace("&", "§")))
                .onlinePlayers(processInformation.getOnlineCount())
                .maximumPlayers(processInformation.getMaxPlayers())
                .build();
        event.setPing(serverPing);
    }
}
