package systems.reformcloud.reformcloud2.proxy.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.proxy.config.MotdConfiguration;
import systems.reformcloud.reformcloud2.proxy.config.TabListConfiguration;
import systems.reformcloud.reformcloud2.proxy.plugin.PluginConfigHandler;
import systems.reformcloud.reformcloud2.proxy.velocity.VelocityPlugin;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class VelocityListener {

    public VelocityListener() {
        Task.EXECUTOR.execute(() -> {
            while (!Thread.interrupted()) {
                if (PluginConfigHandler.getConfiguration().getTabListConfigurations().isEmpty()) {
                    return;
                }

                if (PluginConfigHandler.getConfiguration().getTabListConfigurations().size() == 1) {
                    return;
                }

                if ((PluginConfigHandler.getConfiguration().getTabListConfigurations().size() - 1) == ATOMIC_INTEGERS[0].get()) {
                    ATOMIC_INTEGERS[0].set(0);
                    initTab();
                    AbsoluteThread.sleep(TimeUnit.SECONDS.toMillis(getCurrentTabConfig().getWaitUntilNextInSeconds()));
                    continue;
                }

                initTab();
                AbsoluteThread.sleep(TimeUnit.SECONDS.toMillis(getCurrentTabConfig().getWaitUntilNextInSeconds()));
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

    @Subscribe
    public void handle(final ProxyPingEvent event) {
        ServerPing.Builder builder = event.getPing().asBuilder();

        // ====

        MotdConfiguration current = getCurrentMotdConfig();
        String[] players = replaceAll(current.getPlayerInfo());
        String protocol = replaceMotdString(current.getProtocol());
        String first = current.getFirstLine() == null ? "" : current.getFirstLine();
        String second = current.getSecondLine() == null ? "" : current.getSecondLine();

        String finalMotd = replaceMotdString(first + "\n" + second);

        // ====

        ServerPing.SamplePlayer[] samplePlayers = new ServerPing.SamplePlayer[players == null ? 0 : players.length];
        if (players != null) {
            for (int i = 0; i < samplePlayers.length; i++) {
                samplePlayers[i] = new ServerPing.SamplePlayer(players[i], UUID.randomUUID());
            }
        }

        // ====

        ProcessInformation info = API.getInstance().getCurrentProcessInformation();
        int max = info.getProcessDetail().getMaxPlayers();
        int online = info.getProcessPlayerManager().getOnlineCount();

        // ====

        builder
                .clearMods()
                .description(TextComponent.of(finalMotd))
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
    }

    @Subscribe
    public void handle(final PostLoginEvent event) {
        initTab0(event.getPlayer());
    }

    @Subscribe
    public void handle(final ServerConnectedEvent event) {
        initTab0(event.getPlayer());
    }

    @systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener
    public void handle(final ProcessUpdatedEvent event) {
        initTab();
    }

    private static void initTab() {
        VelocityPlugin.proxyServer.getAllPlayers().forEach(VelocityListener::initTab0);
    }

    private static void initTab0(Player player) {
        TabListConfiguration current = getCurrentTabConfig();

        Component header = TextComponent.of(current.getHeader() == null ? "" : replaceTabList(player, current.getHeader()));
        Component footer = TextComponent.of(current.getFooter() == null ? "" : replaceTabList(player, current.getFooter()));

        player.getTabList().setHeaderAndFooter(header, footer);
    }

    /* =================================== */

    private static final AtomicInteger[] ATOMIC_INTEGERS = new AtomicInteger[]{
            new AtomicInteger(0),
            new AtomicInteger(0),
            new AtomicInteger(0)
    };

    public static TabListConfiguration getCurrentTabConfig() {
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
        return text
                .replace("%proxy_name%", current.getName())
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

        return Arrays.stream(in).map(VelocityListener::replaceMotdString).toArray(String[]::new);
    }

    /* ==================================== */

    private static String replaceTabList(Player player, String line) {
        ProcessInformation info = API.getInstance().getCurrentProcessInformation();
        int max = info.getProcessDetail().getMaxPlayers();
        int online = info.getProcessPlayerManager().getOnlineCount();

        return line
                .replace("%player_server%", player.getCurrentServer().isPresent()
                        ? player.getCurrentServer().get().getServerInfo().getName() : "")
                .replace("%player_name%", player.getUsername())
                .replace("%player_unique_id%", player.getUniqueId().toString())
                .replace("%player_ping%", Long.toString(player.getPing()))
                .replace("%proxy_online_count%", Integer.toString(online))
                .replace("%proxy_max_players%", Integer.toString(max));
    }
}
