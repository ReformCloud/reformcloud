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
package systems.reformcloud.reformcloud2.proxy.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.proxy.ProxyConfigurationHandler;

import java.util.Arrays;
import java.util.UUID;

public final class VelocityListener {

    public VelocityListener(@NotNull ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    private final ProxyServer proxyServer;

    @Subscribe
    public void handle(final @NotNull ProxyPingEvent event) {
        ProxyConfigurationHandler.getInstance().getBestMessageOfTheDayConfiguration().ifPresent(motdConfiguration -> {
            ServerPing.Builder builder = event.getPing().asBuilder();

            String protocol = motdConfiguration.getProtocol() == null ? null : ProxyConfigurationHandler.getInstance().replaceMessageOfTheDayPlaceHolders(motdConfiguration.getProtocol());
            String[] players = motdConfiguration.getPlayerInfo() == null ? null : Arrays.stream(motdConfiguration.getPlayerInfo())
                    .map(ProxyConfigurationHandler.getInstance()::replaceMessageOfTheDayPlaceHolders)
                    .toArray(String[]::new);

            String first = motdConfiguration.getFirstLine() == null ? "" : motdConfiguration.getFirstLine();
            String second = motdConfiguration.getSecondLine() == null ? "" : motdConfiguration.getSecondLine();
            String finalMotd = ProxyConfigurationHandler.getInstance().replaceMessageOfTheDayPlaceHolders(first + "\n" + second);

            ServerPing.SamplePlayer[] samplePlayers = new ServerPing.SamplePlayer[players == null ? 0 : players.length];
            if (players != null) {
                for (int i = 0; i < samplePlayers.length; i++) {
                    samplePlayers[i] = new ServerPing.SamplePlayer(players[i], UUID.randomUUID());
                }
            }

            ProcessInformation info = API.getInstance().getCurrentProcessInformation();
            int max = info.getProcessDetail().getMaxPlayers();
            int online = info.getProcessPlayerManager().getOnlineCount();

            builder
                    .description(LegacyComponentSerializer.legacyLinking().deserialize(finalMotd))
                    .maximumPlayers(max)
                    .onlinePlayers(online)
                    .build();

            if (players != null) {
                builder.clearSamplePlayers().samplePlayers(samplePlayers);
            }

            if (protocol != null) {
                builder.version(new ServerPing.Version(1, protocol));
            }

            event.setPing(builder.build());
        });
    }

    @Subscribe(order = PostOrder.LAST)
    public void handle(final PostLoginEvent event) {
        initTab();
    }

    @Subscribe
    public void handle(final DisconnectEvent event) {
        initTab();
    }

    @Subscribe
    public void handle(final @NotNull ServerConnectedEvent event) {
        initTab0(event.getPlayer());
    }

    @Listener
    public void handle(final @NotNull ProcessUpdatedEvent event) {
        if (event.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(API.getInstance().getCurrentProcessInformation().getProcessDetail().getProcessUniqueID())) {
            initTab();
        }
    }

    public void initTab() {
        this.proxyServer.getAllPlayers().forEach(VelocityListener::initTab0);
    }

    public static void initTab0(@NotNull Player player) {
        ProxyConfigurationHandler.getInstance().getCurrentTabListConfiguration().ifPresent(tabListConfiguration -> {
            Component header = tabListConfiguration.getHeader() == null
                    ? TextComponent.empty()
                    : LegacyComponentSerializer.legacyLinking().deserialize(replaceVelocityPlaceHolders(player, tabListConfiguration.getHeader()));
            Component footer = tabListConfiguration.getFooter() == null
                    ? TextComponent.empty()
                    : LegacyComponentSerializer.legacyLinking().deserialize(replaceVelocityPlaceHolders(player, tabListConfiguration.getFooter()));

            player.getTabList().setHeaderAndFooter(header, footer);
        });
    }

    @NotNull
    private static String replaceVelocityPlaceHolders(@NotNull Player player, @NotNull String tablist) {
        tablist = tablist
                .replace("%player_server%", player.getCurrentServer().isPresent() ? player.getCurrentServer().get().getServerInfo().getName() : "")
                .replace("%player_name%", player.getUsername())
                .replace("%player_unique_id%", player.getUniqueId().toString())
                .replace("%player_ping%", Long.toString(player.getPing()));
        return ProxyConfigurationHandler.getInstance().replaceTabListPlaceHolders(tablist);
    }
}
