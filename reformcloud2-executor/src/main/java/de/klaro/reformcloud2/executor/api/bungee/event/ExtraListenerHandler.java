package de.klaro.reformcloud2.executor.api.bungee.event;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public final class ExtraListenerHandler implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void handle(final ProxyPingEvent event) {
        final ProcessInformation processInformation = ExecutorAPI.getInstance().getThisProcessInformation();
        if (processInformation.getMotd() == null) {
            return;
        }

        final ServerPing serverPing = event.getResponse();

        serverPing.setDescriptionComponent(
                new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', processInformation.getMotd())))
        );
        serverPing.setPlayers(new ServerPing.Players(
                processInformation.getMaxPlayers(),
                processInformation.getOnlineCount(),
                new ServerPing.PlayerInfo[0]
        ));
        event.setResponse(serverPing);
    }
}
