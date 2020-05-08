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
package systems.reformcloud.reformcloud2.executor.api.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.APIConstants;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.bungee.event.PlayerListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.bungee.event.ProcessEventHandler;
import systems.reformcloud.reformcloud2.executor.api.bungee.reconnect.ReformCloudReconnectHandler;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared.ClientChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.executor.PlayerAPIExecutor;
import systems.reformcloud.reformcloud2.executor.api.network.api.PacketAPIConnectPlayerToServer;
import systems.reformcloud.reformcloud2.executor.api.network.api.PacketAPIKickPlayer;
import systems.reformcloud.reformcloud2.executor.api.network.api.PacketAPISendMessage;
import systems.reformcloud.reformcloud2.executor.api.network.api.PacketAPISendTitle;
import systems.reformcloud.reformcloud2.executor.api.network.channel.APINetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIPacketOutRequestIngameMessages;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIPacketOutRequestIngameMessagesResult;
import systems.reformcloud.reformcloud2.executor.api.shared.SharedInvalidPlayerFixer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public final class BungeeExecutor extends API implements PlayerAPIExecutor {

    public static final List<ProcessInformation> LOBBY_SERVERS = new CopyOnWriteArrayList<>();

    private static BungeeExecutor instance;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private final Plugin plugin;

    private ProcessInformation thisProcessInformation;

    private IngameMessages messages = new IngameMessages();

    private static boolean waterdog;

    private static boolean waterdogPE;

    BungeeExecutor(Plugin plugin) {
        super.type = ExecutorType.API;
        super.loadPacketHandlers();
        APIConstants.playerAPIExecutor = this;

        this.plugin = plugin;
        instance = this;

        new ExternalEventBusHandler(packetHandler, new DefaultEventManager());
        this.getEventManager().registerListener(new ProcessEventHandler());
        this.getEventManager().registerListener(this);

        this.packetHandler.registerNetworkHandlers(
                PacketAPIConnectPlayerToServer.class,
                PacketAPIKickPlayer.class,
                PacketAPISendMessage.class,
                PacketAPISendTitle.class,
                APIPacketOutRequestIngameMessagesResult.class
        );

        String connectionKey = JsonConfiguration.read("reformcloud/.connection/key.json").getString("key");
        SystemHelper.deleteFile(new File("reformcloud/.connection/key.json"));
        JsonConfiguration connectionConfig = JsonConfiguration.read("reformcloud/.connection/connection.json");

        this.thisProcessInformation = connectionConfig.get("startInfo", ProcessInformation.TYPE);
        if (this.thisProcessInformation == null) {
            System.exit(0);
            return;
        }

        waterdog = thisProcessInformation.getProcessDetail().getTemplate().getVersion().equals(Version.WATERDOG)
                || thisProcessInformation.getProcessDetail().getTemplate().getVersion().equals(Version.WATERDOG_PE);
        waterdogPE = thisProcessInformation.getProcessDetail().getTemplate().getVersion().equals(Version.WATERDOG_PE);

        this.networkClient.connect(
                connectionConfig.getString("controller-host"),
                connectionConfig.getInteger("controller-port"),
                () -> new APINetworkChannelReader(),
                new ClientChallengeAuthHandler(
                        connectionKey,
                        thisProcessInformation.getProcessDetail().getName(),
                        () -> new JsonConfiguration(),
                        context -> {
                        } // unused here
                )
        );

        ExecutorAPI.setInstance(this);
        ProxyServer.getInstance().setReconnectHandler(new ReformCloudReconnectHandler());
        awaitConnectionAndUpdate();
    }

    @NotNull
    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
    }

    public Plugin getPlugin() {
        return plugin;
    }

    NetworkClient getNetworkClient() {
        return networkClient;
    }

    @Override
    public PacketHandler packetHandler() {
        return packetHandler;
    }

    @NotNull
    @Override
    public ProcessInformation getCurrentProcessInformation() {
        return this.thisProcessInformation;
    }

    @NotNull
    public static BungeeExecutor getInstance() {
        return instance;
    }

    // ===============

    static void clearHandlers() {
        ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> listenerInfo.getServerPriority().clear());
        ProxyServer.getInstance().getConfig().getServers().clear();
    }

    private void awaitConnectionAndUpdate() {
        Task.EXECUTOR.execute(() -> {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            while (packetSender == null) {
                packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                AbsoluteThread.sleep(20);
            }

            getAllProcesses().forEach(BungeeExecutor::registerServer);
            ProxyServer.getInstance().getPluginManager().registerListener(plugin, new PlayerListenerHandler());

            thisProcessInformation.updateMaxPlayers(ProxyServer.getInstance().getConfig().getPlayerLimit());
            thisProcessInformation.updateRuntimeInformation();
            thisProcessInformation.getNetworkInfo().setConnected(true);
            thisProcessInformation.getProcessDetail().setProcessState(thisProcessInformation.getProcessDetail().getInitialState());
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(thisProcessInformation);

            this.fixInvalidPlayers();

            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(controller -> packetHandler.getQueryHandler().sendQueryAsync(
                    controller,
                    new APIPacketOutRequestIngameMessages()
            ).onComplete(packet -> {
                if (packet instanceof APIPacketOutRequestIngameMessagesResult) {
                    this.setMessages(((APIPacketOutRequestIngameMessagesResult) packet).getIngameMessages());
                }
            }));
        });
    }

    private void fixInvalidPlayers() {
        SharedInvalidPlayerFixer.start(
                uuid -> ProxyServer.getInstance().getPlayer(uuid) != null,
                () -> ProxyServer.getInstance().getOnlineCount()
        );
    }

    public static void registerServer(@NotNull ProcessInformation processInformation) {
        ServerInfo oldInfo = ProxyServer.getInstance().getServerInfo(processInformation.getProcessDetail().getName());
        if (oldInfo != null) {
            if (!(oldInfo.getSocketAddress() instanceof InetSocketAddress)) {
                return;
            }

            int port = ((InetSocketAddress) oldInfo.getSocketAddress()).getPort();
            if (processInformation.getNetworkInfo().getPort() == port) {
                return;
            }

            unregisterServer(processInformation);
        }

        if (processInformation.getNetworkInfo().isConnected() && processInformation.getProcessDetail().getTemplate().isServer()) {
            ServerInfo serverInfo = constructServerInfo(processInformation);
            if (serverInfo == null) {
                return;
            }

            ProxyServer.getInstance().getServers().put(processInformation.getProcessDetail().getName(), serverInfo);
            if (processInformation.isLobby()) {
                LOBBY_SERVERS.add(processInformation);
                ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> listenerInfo.getServerPriority().add(processInformation.getProcessDetail().getName()));
            }
        }
    }

    public static void unregisterServer(ProcessInformation processInformation) {
        ProxyServer.getInstance().getServers().remove(processInformation.getProcessDetail().getName());
        if (processInformation.isLobby()) {
            BungeeExecutor.LOBBY_SERVERS.remove(processInformation);
            ProxyServer.getInstance().getConfig().getListeners().forEach(
                    listenerInfo -> listenerInfo.getServerPriority().remove(processInformation.getProcessDetail().getName())
            );
        }
    }

    @Nullable
    private static ServerInfo constructServerInfo(@NotNull ProcessInformation processInformation) {
        if (waterdog) {
            if (waterdogPE && processInformation.getProcessDetail().getTemplate().getVersion().getId() != 3) {
                return null;
            } else if (!waterdogPE && processInformation.getProcessDetail().getTemplate().getVersion().getId() == 3) {
                return null;
            }

            try {
                Method method = ProxyServer.class.getMethod("constructServerInfo", String.class, SocketAddress.class, String.class, boolean.class, boolean.class, String.class);
                method.setAccessible(true);
                return (ServerInfo) method.invoke(ProxyServer.getInstance(),
                        processInformation.getProcessDetail().getName(),
                        processInformation.getNetworkInfo().toInet(),
                        "ReformCloud2",
                        false,
                        processInformation.getProcessDetail().getTemplate().getVersion().getId() == 3,
                        "default"
                );
            } catch (final InvocationTargetException | IllegalAccessException | NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }

        return ProxyServer.getInstance().constructServerInfo(
                processInformation.getProcessDetail().getName(),
                processInformation.getNetworkInfo().toInet(),
                "ReformCloud2", false);
    }

    @Nullable
    public static ProcessInformation getBestLobbyForPlayer(@NotNull ProcessInformation current,
                                                           @NotNull Function<String, Boolean> permissionCheck,
                                                           @Nullable String excluded) {
        final List<ProcessInformation> lobbies = new ArrayList<>(LOBBY_SERVERS);

        // Filter all non java servers if this is a java proxy else all mcpe servers
        Streams.others(lobbies, e -> {
            Version version = e.getProcessDetail().getTemplate().getVersion();
            if (version.equals(Version.NUKKIT_X) && current.getProcessDetail().getTemplate().getVersion().equals(Version.WATERDOG_PE)) {
                return true;
            }

            return version.getId() == 1 && current.getProcessDetail().getTemplate().getVersion().getId() == 2;
        }).forEach(lobbies::remove);

        // Filter out all lobbies with join permission which the player does not
        // have
        Streams.others(lobbies, e -> {
            final PlayerAccessConfiguration configuration = e.getProcessGroup().getPlayerAccessConfiguration();
            if (!configuration.isJoinOnlyPerPermission()) {
                return true;
            }

            return permissionCheck.apply(configuration.getJoinPermission());
        }).forEach(lobbies::remove);

        // Filter out all lobbies which are in maintenance and not joinable for the
        // player
        Streams.others(lobbies, e -> {
            final PlayerAccessConfiguration configuration = e.getProcessGroup().getPlayerAccessConfiguration();
            if (!configuration.isMaintenance()) {
                return true;
            }

            return permissionCheck.apply(configuration.getMaintenanceJoinPermission());
        }).forEach(lobbies::remove);

        // Filter out all full server which the player cannot access
        Streams.others(lobbies, e -> {
            final PlayerAccessConfiguration configuration = e.getProcessGroup().getPlayerAccessConfiguration();
            if (!configuration.isUseCloudPlayerLimit()) {
                return true;
            }

            if (e.getProcessPlayerManager().getOnlineCount() < e.getProcessDetail().getMaxPlayers()) {
                return true;
            }

            return permissionCheck.apply("reformcloud.join.full");
        }).forEach(lobbies::remove);

        // Filter out all excluded servers
        if (excluded != null) {
            Streams.allOf(lobbies, e -> e.getProcessDetail().getName().equals(excluded)).forEach(lobbies::remove);
        }

        if (lobbies.isEmpty()) {
            return null;
        }

        if (lobbies.size() == 1) {
            return lobbies.get(0);
        }

        return lobbies.get(new Random().nextInt(lobbies.size()));
    }

    public void setThisProcessInformation(ProcessInformation thisProcessInformation) {
        this.thisProcessInformation = thisProcessInformation;
    }

    public IngameMessages getMessages() {
        return messages;
    }

    private void setMessages(@NotNull IngameMessages messages) {
        this.messages = messages;
    }

    @Listener
    public void handle(final ProcessUpdatedEvent event) {
        if (event.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(thisProcessInformation.getProcessDetail().getProcessUniqueID())) {
            thisProcessInformation = event.getProcessInformation();
        }
    }

    /* ======================== Player API ======================== */

    @Override
    public void executeSendMessage(UUID player, String message) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer != null) {
            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(message));
        }
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer != null) {
            proxiedPlayer.disconnect(TextComponent.fromLegacyText(message));
        }
    }

    @Override
    public void executePlaySound(UUID player, String sound, float f1, float f2) {
        throw new UnsupportedOperationException("Not supported on proxy server");
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer != null) {
            ProxyServer.getInstance()
                    .createTitle()
                    .title(TextComponent.fromLegacyText(title))
                    .subTitle(TextComponent.fromLegacyText(subTitle))
                    .fadeIn(fadeIn)
                    .stay(stay)
                    .fadeOut(fadeOut)
                    .send(proxiedPlayer);
        }
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
        throw new UnsupportedOperationException("Not supported on proxy server");
    }

    @Override
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        throw new UnsupportedOperationException("Not supported on proxy server");
    }

    @Override
    public void executeConnect(UUID player, String server) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
        if (proxiedPlayer != null && serverInfo != null) {
            proxiedPlayer.connect(serverInfo);
        }
    }

    @Override
    public void executeConnect(UUID player, ProcessInformation server) {
        executeConnect(player, server.getProcessDetail().getName());
    }

    @Override
    public void executeConnect(UUID player, UUID target) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);
        if (proxiedPlayer != null && targetPlayer != null) {
            proxiedPlayer.connect(targetPlayer.getServer().getInfo());
        }
    }
}
