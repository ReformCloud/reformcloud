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

import net.md_5.bungee.api.ChatColor;
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
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.proxy.config.MotdConfiguration;
import systems.reformcloud.reformcloud2.proxy.config.TabListConfiguration;
import systems.reformcloud.reformcloud2.proxy.plugin.PluginConfigHandler;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BungeeCordListener implements Listener {

    public BungeeCordListener() {
        Task.EXECUTOR.execute(() -> {
            while (!Thread.interrupted()) {
                if (PluginConfigHandler.getConfiguration().getTabListConfigurations().isEmpty()) {
                    return;
                }

                if (PluginConfigHandler.getConfiguration().getTabListConfigurations().size() == 1) {
                    return;
                }

                TabListConfiguration currentTabConfig = getCurrentTabConfig();
                if (currentTabConfig == null) {
                    continue;
                }

                if ((PluginConfigHandler.getConfiguration().getTabListConfigurations().size() - 1) == ATOMIC_INTEGERS[0].get()) {
                    ATOMIC_INTEGERS[0].set(0);
                    initTab();
                    AbsoluteThread.sleep(TimeUnit.SECONDS.toMillis(currentTabConfig.getWaitUntilNextInSeconds()));
                    continue;
                }

                initTab();
                AbsoluteThread.sleep(TimeUnit.SECONDS.toMillis(currentTabConfig.getWaitUntilNextInSeconds()));
                ATOMIC_INTEGERS[0].incrementAndGet();
            }
        });

        Task.EXECUTOR.execute(() -> {
            while (!Thread.interrupted()) {
                if (PluginConfigHandler.getConfiguration().getMotdDefaultConfig().isEmpty()) {
                    return;
                }

                if (PluginConfigHandler.getConfiguration().getMotdDefaultConfig().size() == 1) {
                    return;
                }

                if ((PluginConfigHandler.getConfiguration().getMotdDefaultConfig().size() - 1) == ATOMIC_INTEGERS[1].get()) {
                    ATOMIC_INTEGERS[1].set(0);
                    AbsoluteThread.sleep(TimeUnit.SECONDS.toMillis(getDefaultConfig().getWaitUntilNextInSeconds()));
                    continue;
                }

                AbsoluteThread.sleep(TimeUnit.SECONDS.toMillis(getDefaultConfig().getWaitUntilNextInSeconds()));
                ATOMIC_INTEGERS[1].incrementAndGet();
            }
        });

        Task.EXECUTOR.execute(() -> {
            while (!Thread.interrupted()) {
                if (PluginConfigHandler.getConfiguration().getMotdMaintenanceConfig().isEmpty()) {
                    return;
                }

                if (PluginConfigHandler.getConfiguration().getMotdMaintenanceConfig().size() == 1) {
                    return;
                }

                if ((PluginConfigHandler.getConfiguration().getMotdMaintenanceConfig().size() - 1) == ATOMIC_INTEGERS[2].get()) {
                    ATOMIC_INTEGERS[2].set(0);
                    AbsoluteThread.sleep(TimeUnit.SECONDS.toMillis(getMaintenanceConfig().getWaitUntilNextInSeconds()));
                    continue;
                }

                AbsoluteThread.sleep(TimeUnit.SECONDS.toMillis(getMaintenanceConfig().getWaitUntilNextInSeconds()));
                ATOMIC_INTEGERS[2].incrementAndGet();
            }
        });
    }

    @EventHandler
    public void handle(final ProxyPingEvent event) {
        ServerPing serverPing = event.getResponse();

        // ====

        MotdConfiguration current = getCurrentMotdConfig();
        String[] players = replaceAll(current.getPlayerInfo());
        String protocol = replaceMotdString(current.getProtocol());
        String first = current.getFirstLine() == null ? "" : current.getFirstLine();
        String second = current.getSecondLine() == null ? "" : current.getSecondLine();

        String finalMotd = replaceMotdString(first + "\n" + second);

        // ====

        ServerPing.PlayerInfo[] playerInfos = new ServerPing.PlayerInfo[players == null ? 0 : players.length];
        if (players != null) {
            for (int i = 0; i < playerInfos.length; i++) {
                playerInfos[i] = new ServerPing.PlayerInfo(players[i], UUID.randomUUID());
            }
        }

        // ====

        ProcessInformation info = API.getInstance().getCurrentProcessInformation();
        int max = info.getProcessDetail().getMaxPlayers();
        int online = info.getProcessPlayerManager().getOnlineCount();

        // ====

        if (players != null) {
            serverPing.setPlayers(new ServerPing.Players(
                    max, online, playerInfos
            ));
        }

        if (protocol != null) {
            serverPing.setVersion(new ServerPing.Protocol(protocol, 1));
        }

        serverPing.setDescriptionComponent(new TextComponent(finalMotd));
        event.setResponse(serverPing);
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
    public void handle(final ServerSwitchEvent event) {
        initTab0(event.getPlayer());
    }

    @systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener
    public void handle(final ProcessUpdatedEvent event) {
        if (event.getProcessInformation().getProcessDetail().getProcessUniqueID()
                .equals(API.getInstance().getCurrentProcessInformation().getProcessDetail().getProcessUniqueID())) {
            initTab();
        }
    }

    private static void initTab() {
        ProxyServer.getInstance().getPlayers().forEach(BungeeCordListener::initTab0);
    }

    private static void initTab0(ProxiedPlayer player) {
        TabListConfiguration current = getCurrentTabConfig();
        if (current == null) {
            return;
        }

        BaseComponent[] header = TextComponent.fromLegacyText(current.getHeader() == null ? "" : replaceTabList(player, current.getHeader()));
        BaseComponent[] footer = TextComponent.fromLegacyText(current.getFooter() == null ? "" : replaceTabList(player, current.getFooter()));

        player.setTabHeader(header, footer);
    }

    /* =================================== */

    private static final AtomicInteger[] ATOMIC_INTEGERS = new AtomicInteger[]{
            new AtomicInteger(0),
            new AtomicInteger(0),
            new AtomicInteger(0)
    };

    @Nullable
    public static TabListConfiguration getCurrentTabConfig() {
        if (PluginConfigHandler.getConfiguration().getTabListConfigurations().isEmpty()) {
            return null;
        }

        return PluginConfigHandler.getConfiguration().getTabListConfigurations().get(ATOMIC_INTEGERS[0].get());
    }

    public static MotdConfiguration getCurrentMotdConfig() {
        boolean maintenance = API.getInstance().getCurrentProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isMaintenance();
        return maintenance
                ? PluginConfigHandler.getConfiguration().getMotdMaintenanceConfig().get(ATOMIC_INTEGERS[2].get())
                : PluginConfigHandler.getConfiguration().getMotdDefaultConfig().get(ATOMIC_INTEGERS[1].get());
    }

    private static MotdConfiguration getDefaultConfig() {
        return PluginConfigHandler.getConfiguration().getMotdDefaultConfig().get(ATOMIC_INTEGERS[1].get());
    }

    private static MotdConfiguration getMaintenanceConfig() {
        return PluginConfigHandler.getConfiguration().getMotdMaintenanceConfig().get(ATOMIC_INTEGERS[2].get());
    }

    /* ==================================== */

    private static String replaceMotdString(String text) {
        if (text == null) {
            return null;
        }

        ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        return ChatColor.translateAlternateColorCodes('&', text)
                .replace("%proxy_name%", current.getProcessDetail().getName())
                .replace("%proxy_display_name%", current.getProcessDetail().getDisplayName())
                .replace("%proxy_unique_id%", current.getProcessDetail().getProcessUniqueID().toString())
                .replace("%proxy_id%", Integer.toString(current.getProcessDetail().getId()))
                .replace("%proxy_online_players%", Integer.toString(current.getProcessPlayerManager().getOnlineCount()))
                .replace("%proxy_max_players%", Integer.toString(current.getProcessDetail().getMaxPlayers()))
                .replace("%proxy_group%", current.getProcessGroup().getName())
                .replace("%proxy_parent%", current.getProcessDetail().getParentName());
    }

    private static String[] replaceAll(String[] in) {
        if (in == null) {
            return null;
        }

        return Arrays.stream(in).map(BungeeCordListener::replaceMotdString).toArray(String[]::new);
    }

    /* ==================================== */

    private static String replaceTabList(ProxiedPlayer player, String line) {
        ProcessInformation info = API.getInstance().getCurrentProcessInformation();
        int max = info.getProcessDetail().getMaxPlayers();
        int online = info.getProcessPlayerManager().getOnlineCount();

        return ChatColor.translateAlternateColorCodes('&', line)
                .replace("%player_server%", player.getServer() != null
                        ? player.getServer().getInfo().getName() : "")
                .replace("%player_name%", player.getName())
                .replace("%player_unique_id%", player.getUniqueId().toString())
                .replace("%player_ping%", Long.toString(player.getPing()))
                .replace("%proxy_online_count%", Integer.toString(online))
                .replace("%proxy_max_players%", Integer.toString(max));
    }
}
