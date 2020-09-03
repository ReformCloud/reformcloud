/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.proxy.bungeecord.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.event.events.process.ProcessUpdateEvent;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.proxy.ProxyConfigurationHandler;

import java.util.Arrays;
import java.util.UUID;

public final class BungeeCordListener implements Listener {

    private static final BaseComponent[] EMPTY = TextComponent.fromLegacyText("");

    private static void initTab() {
        ProxyServer.getInstance().getPlayers().forEach(BungeeCordListener::initTab0);
    }

    public static void initTab0(@NotNull ProxiedPlayer player) {
        ProxyConfigurationHandler.getInstance().getCurrentTabListConfiguration().ifPresent(tabListConfiguration -> {
            BaseComponent[] header = tabListConfiguration.getHeader() == null
                    ? EMPTY
                    : TextComponent.fromLegacyText(replaceBungeeCordPlaceHolders(player, tabListConfiguration.getHeader()));
            BaseComponent[] footer = tabListConfiguration.getFooter() == null
                    ? EMPTY
                    : TextComponent.fromLegacyText(replaceBungeeCordPlaceHolders(player, tabListConfiguration.getFooter()));

            player.setTabHeader(header, footer);
        });
    }

    @NotNull
    private static String replaceBungeeCordPlaceHolders(@NotNull ProxiedPlayer player, @NotNull String tablist) {
        tablist = tablist
                .replace("%player_server%", player.getServer() != null ? player.getServer().getInfo().getName() : "")
                .replace("%player_name%", player.getName())
                .replace("%player_unique_id%", player.getUniqueId().toString())
                .replace("%player_ping%", Long.toString(player.getPing()));
        return ProxyConfigurationHandler.getInstance().replaceTabListPlaceHolders(tablist);
    }

    @EventHandler
    public void handle(final @NotNull ProxyPingEvent event) {
        ProxyConfigurationHandler.getInstance().getBestMessageOfTheDayConfiguration().ifPresent(motdConfiguration -> {
            ServerPing serverPing = event.getResponse();

            String protocol = motdConfiguration.getProtocol() == null ? null : ProxyConfigurationHandler.getInstance().replaceMessageOfTheDayPlaceHolders(motdConfiguration.getProtocol());
            String[] players = motdConfiguration.getPlayerInfo() == null ? null : Arrays.stream(motdConfiguration.getPlayerInfo())
                    .map(ProxyConfigurationHandler.getInstance()::replaceMessageOfTheDayPlaceHolders)
                    .toArray(String[]::new);

            String first = motdConfiguration.getFirstLine() == null ? "" : motdConfiguration.getFirstLine();
            String second = motdConfiguration.getSecondLine() == null ? "" : motdConfiguration.getSecondLine();
            final String finalMotd = ProxyConfigurationHandler.getInstance().replaceMessageOfTheDayPlaceHolders(first + "\n" + second);

            ServerPing.PlayerInfo[] playerInfos = new ServerPing.PlayerInfo[players == null ? 0 : players.length];
            if (players != null) {
                for (int i = 0; i < playerInfos.length; i++) {
                    playerInfos[i] = new ServerPing.PlayerInfo(players[i], UUID.randomUUID());
                }
            }

            ProcessInformation info = Embedded.getInstance().getCurrentProcessInformation();
            int max = info.getProcessDetail().getMaxPlayers();
            int online = info.getProcessPlayerManager().getOnlineCount();

            if (players != null) {
                serverPing.setPlayers(new ServerPing.Players(max, online, playerInfos));
            }

            if (protocol != null) {
                serverPing.setVersion(new ServerPing.Protocol(protocol, 1));
            }

            serverPing.setDescriptionComponent(new TextComponent(finalMotd));
            event.setResponse(serverPing);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(final PostLoginEvent event) {
        initTab();
    }

    @EventHandler
    public void handle(final PlayerDisconnectEvent event) {
        initTab();
    }

    @EventHandler
    public void handle(final @NotNull ServerSwitchEvent event) {
        initTab0(event.getPlayer());
    }

    @systems.reformcloud.reformcloud2.executor.api.event.handler.Listener
    public void handle(final @NotNull ProcessUpdateEvent event) {
        if (event.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(Embedded.getInstance().getCurrentProcessInformation().getProcessDetail().getProcessUniqueID())) {
            initTab();
        }
    }
}
