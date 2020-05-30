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
package systems.refomcloud.reformcloud2.embedded.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.refomcloud.reformcloud2.embedded.bungee.event.PlayerListenerHandler;
import systems.refomcloud.reformcloud2.embedded.bungee.event.ProcessEventHandler;
import systems.refomcloud.reformcloud2.embedded.bungee.executor.BungeePlayerAPIExecutor;
import systems.refomcloud.reformcloud2.embedded.bungee.reconnect.ReformCloudReconnectHandler;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.refomcloud.reformcloud2.embedded.network.api.PacketAPIConnectPlayerToServer;
import systems.refomcloud.reformcloud2.embedded.network.api.PacketAPIKickPlayer;
import systems.refomcloud.reformcloud2.embedded.network.api.PacketAPISendMessage;
import systems.refomcloud.reformcloud2.embedded.network.api.PacketAPISendTitle;
import systems.refomcloud.reformcloud2.embedded.network.channel.APIEndpointChannelReader;
import systems.refomcloud.reformcloud2.embedded.network.packets.out.APIPacketOutRequestIngameMessages;
import systems.refomcloud.reformcloud2.embedded.network.packets.out.APIPacketOutRequestIngameMessagesResult;
import systems.refomcloud.reformcloud2.embedded.shared.SharedInvalidPlayerFixer;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.api.basic.events.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.executor.api.network.challenge.shared.ClientChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.network.packet.PacketProvider;
import systems.reformcloud.reformcloud2.executor.api.network.packet.defaults.DefaultPacketProvider;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.shared.event.DefaultEventManager;
import systems.reformcloud.reformcloud2.shared.network.client.DefaultNetworkClient;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BungeeExecutor extends API {

    private static BungeeExecutor instance;
    private static boolean waterdog;
    private static boolean waterdogPE;
    private final List<ProcessInformation> cachedLobbyServices = new CopyOnWriteArrayList<>();
    private final List<ProcessInformation> cachedProxyServices = new CopyOnWriteArrayList<>();
    private final PacketProvider packetProvider = new DefaultPacketProvider();
    private final NetworkClient networkClient = new DefaultNetworkClient();
    private final Plugin plugin;
    private ProcessInformation thisProcessInformation;
    private IngameMessages messages = new IngameMessages();

    BungeeExecutor(Plugin plugin) {
        super.type = ExecutorType.API;
        super.loadPacketHandlers();
        PlayerAPIExecutor.setInstance(new BungeePlayerAPIExecutor());

        this.plugin = plugin;
        instance = this;

        new ExternalEventBusHandler(this.packetProvider, new DefaultEventManager());
        this.getEventManager().registerListener(new ProcessEventHandler());
        this.getEventManager().registerListener(this);

        this.packetProvider.registerNetworkHandlers(
                PacketAPIConnectPlayerToServer.class,
                PacketAPIKickPlayer.class,
                PacketAPISendMessage.class,
                PacketAPISendTitle.class,
                APIPacketOutRequestIngameMessagesResult.class
        );

        String connectionKey = JsonConfiguration.read("reformcloud/.connection/key.json").getString("key");
        IOUtils.deleteFile(new File("reformcloud/.connection/key.json"));
        JsonConfiguration connectionConfig = JsonConfiguration.read("reformcloud/.connection/connection.json");

        this.thisProcessInformation = connectionConfig.get("startInfo", ProcessInformation.TYPE);
        if (this.thisProcessInformation == null) {
            System.exit(0);
            return;
        }

        waterdog = this.thisProcessInformation.getProcessDetail().getTemplate().getVersion().equals(Version.WATERDOG)
                || this.thisProcessInformation.getProcessDetail().getTemplate().getVersion().equals(Version.WATERDOG_PE);
        waterdogPE = this.thisProcessInformation.getProcessDetail().getTemplate().getVersion().equals(Version.WATERDOG_PE);

        this.networkClient.connect(
                connectionConfig.getString("controller-host"),
                connectionConfig.getInteger("controller-port"),
                () -> new APIEndpointChannelReader(),
                new ClientChallengeAuthHandler(
                        connectionKey,
                        this.thisProcessInformation.getProcessDetail().getName(),
                        () -> new JsonConfiguration(),
                        context -> {
                        } // unused here
                )
        );

        ExecutorAPI.setInstance(this);
        ProxyServer.getInstance().setReconnectHandler(new ReformCloudReconnectHandler());
        this.awaitConnectionAndUpdate();
    }

    @NotNull
    public static BungeeExecutor getInstance() {
        return instance;
    }

    public List<ProcessInformation> getCachedLobbyServices() {
        return this.cachedLobbyServices;
    }

    public List<ProcessInformation> getCachedProxyServices() {
        return this.cachedProxyServices;
    }

    static void clearHandlers() {
        ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> listenerInfo.getServerPriority().clear());
        ProxyServer.getInstance().getConfig().getServers().clear();
    }

    public static void registerServer(@NotNull ProcessInformation processInformation) {
        if (processInformation.isLobby()) {
            Streams.filterToReference(
                    BungeeExecutor.getInstance().getCachedLobbyServices(),
                    e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID())
            ).ifPresent(info -> {
                BungeeExecutor.getInstance().getCachedLobbyServices().remove(info);
                BungeeExecutor.getInstance().getCachedLobbyServices().add(processInformation);
            }).ifEmpty(v -> BungeeExecutor.getInstance().getCachedLobbyServices().add(processInformation));
        }

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
        }
    }

    public static void unregisterServer(ProcessInformation processInformation) {
        ProxyServer.getInstance().getServers().remove(processInformation.getProcessDetail().getName());
        BungeeExecutor.getInstance().getCachedProxyServices().removeIf(e -> e.equals(processInformation));
        if (processInformation.isLobby()) {
            BungeeExecutor.getInstance().getCachedLobbyServices().removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
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
                "ReformCloud2",
                false
        );
    }

    // ===============

    @NotNull
    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    NetworkClient getNetworkClient() {
        return this.networkClient;
    }

    @Override
    public PacketProvider packetHandler() {
        return this.packetProvider;
    }

    @NotNull
    @Override
    public ProcessInformation getCurrentProcessInformation() {
        return this.thisProcessInformation;
    }

    private void awaitConnectionAndUpdate() {
        Task.EXECUTOR.execute(() -> {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            while (packetSender == null) {
                packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                AbsoluteThread.sleep(20);
            }

            this.getAllProcesses().forEach(process -> {
                if (!process.getProcessDetail().getTemplate().isServer()) {
                    this.cachedProxyServices.add(process);
                    return;
                }

                BungeeExecutor.registerServer(process);
            });
            ProxyServer.getInstance().getPluginManager().registerListener(this.plugin, new PlayerListenerHandler());

            this.thisProcessInformation.updateMaxPlayers(ProxyServer.getInstance().getConfig().getPlayerLimit());
            this.thisProcessInformation.updateRuntimeInformation();
            this.thisProcessInformation.getNetworkInfo().setConnected(true);
            this.thisProcessInformation.getProcessDetail().setProcessState(this.thisProcessInformation.getProcessDetail().getInitialState());
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(this.thisProcessInformation);

            this.fixInvalidPlayers();

            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(controller -> this.packetProvider.getQueryHandler().sendQueryAsync(
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
                () -> ProxyServer.getInstance().getOnlineCount(),
                information -> this.thisProcessInformation = information
        );
    }

    public IngameMessages getMessages() {
        return this.messages;
    }

    private void setMessages(@NotNull IngameMessages messages) {
        this.messages = messages;
    }

    @Listener
    public void handle(final ProcessUpdatedEvent event) {
        if (event.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(this.thisProcessInformation.getProcessDetail().getProcessUniqueID())) {
            this.thisProcessInformation = event.getProcessInformation();
        }

        if (!event.getProcessInformation().getProcessDetail().getTemplate().isServer()) {
            this.cachedProxyServices.removeIf(e -> e.equals(event.getProcessInformation()));
            this.cachedProxyServices.add(event.getProcessInformation());
        }
    }
}