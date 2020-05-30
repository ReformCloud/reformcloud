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
package systems.refomcloud.reformcloud2.embedded.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.refomcloud.reformcloud2.embedded.network.api.PacketAPIConnectPlayerToServer;
import systems.refomcloud.reformcloud2.embedded.network.api.PacketAPIKickPlayer;
import systems.refomcloud.reformcloud2.embedded.network.api.PacketAPISendMessage;
import systems.refomcloud.reformcloud2.embedded.network.api.PacketAPISendTitle;
import systems.refomcloud.reformcloud2.embedded.network.channel.APIEndpointChannelReader;
import systems.refomcloud.reformcloud2.embedded.network.packets.out.APIPacketOutRequestIngameMessages;
import systems.refomcloud.reformcloud2.embedded.network.packets.out.APIPacketOutRequestIngameMessagesResult;
import systems.refomcloud.reformcloud2.embedded.shared.SharedInvalidPlayerFixer;
import systems.refomcloud.reformcloud2.embedded.velocity.event.PlayerListenerHandler;
import systems.refomcloud.reformcloud2.embedded.velocity.event.ProcessEventHandler;
import systems.refomcloud.reformcloud2.embedded.velocity.executor.VelocityPlayerAPIExecutor;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.api.basic.events.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.groups.messages.IngameMessages;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public final class VelocityExecutor extends API {

    private static VelocityExecutor instance;

    private final List<ProcessInformation> cachedLobbyServices = new CopyOnWriteArrayList<>();
    private final List<ProcessInformation> cachedProxyServices = new CopyOnWriteArrayList<>();
    private final ProxyServer proxyServer;
    private final PacketProvider packetProvider = new DefaultPacketProvider();
    private final NetworkClient networkClient = new DefaultNetworkClient();
    private final VelocityLauncher plugin;
    private IngameMessages messages = new IngameMessages();
    private ProcessInformation thisProcessInformation;

    VelocityExecutor(VelocityLauncher launcher, ProxyServer proxyServer) {
        super.type = ExecutorType.API;
        super.loadPacketHandlers();
        PlayerAPIExecutor.setInstance(new VelocityPlayerAPIExecutor(proxyServer));

        instance = this;
        this.proxyServer = proxyServer;

        new ExternalEventBusHandler(this.packetProvider, new DefaultEventManager());
        this.getEventManager().registerListener(new ProcessEventHandler());
        this.getEventManager().registerListener(this);
        proxyServer.getEventManager().register(this.plugin = launcher, new PlayerListenerHandler());

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
        this.awaitConnectionAndUpdate();
    }

    @NotNull
    public static VelocityExecutor getInstance() {
        return instance;
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
    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
    }

    public ProxyServer getProxyServer() {
        return this.proxyServer;
    }

    @NotNull
    @Override
    public ProcessInformation getCurrentProcessInformation() {
        return this.thisProcessInformation;
    }

    @NotNull
    public List<ProcessInformation> getCachedLobbyServices() {
        return this.cachedLobbyServices;
    }

    public List<ProcessInformation> getCachedProxyServices() {
        return this.cachedProxyServices;
    }

    public void handleProcessUpdate(@NotNull ProcessInformation processInformation) {
        Optional<RegisteredServer> server = this.proxyServer.getServer(processInformation.getProcessDetail().getName());
        if (processInformation.isLobby()) {
            Streams.filterToReference(
                    this.cachedLobbyServices,
                    e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID())
            ).ifPresent(info -> {
                this.cachedLobbyServices.remove(info);
                this.cachedLobbyServices.add(processInformation);
            }).ifEmpty(v -> this.cachedLobbyServices.add(processInformation));
        }

        if (server.isPresent()) {
            if (server.get().getServerInfo().getAddress().getPort() == processInformation.getNetworkInfo().getPort()) {
                return;
            }

            this.handleProcessRemove(processInformation);
        }

        if (processInformation.getNetworkInfo().isConnected() && processInformation.getProcessDetail().getTemplate().getVersion().getId() == 1) {
            ServerInfo serverInfo = new ServerInfo(
                    processInformation.getProcessDetail().getName(),
                    processInformation.getNetworkInfo().toInet()
            );
            this.proxyServer.registerServer(serverInfo);
        }
    }

    public void handleProcessRemove(@NotNull ProcessInformation processInformation) {
        this.proxyServer.getServer(processInformation.getProcessDetail().getName())
                .ifPresent(registeredServer -> this.proxyServer.unregisterServer(registeredServer.getServerInfo()));

        if (processInformation.isLobby()) {
            this.cachedLobbyServices.removeIf(e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()));
        }
    }

    private boolean isServerRegistered(String name) {
        return this.proxyServer.getServer(name).isPresent();
    }

    private void awaitConnectionAndUpdate() {
        Task.EXECUTOR.execute(() -> {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            while (packetSender == null) {
                packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                AbsoluteThread.sleep(100);
            }

            this.getAllProcesses().forEach(process -> {
                if (!process.getProcessDetail().getTemplate().isServer()) {
                    this.cachedProxyServices.add(process);
                    return;
                }

                this.handleProcessUpdate(process);
            });

            this.thisProcessInformation.updateMaxPlayers(this.proxyServer.getConfiguration().getShowMaxPlayers());
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
                uuid -> this.proxyServer.getPlayer(uuid).isPresent(),
                () -> this.proxyServer.getPlayerCount(),
                information -> this.thisProcessInformation = information
        );
    }

    public VelocityLauncher getPlugin() {
        return this.plugin;
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

    public IngameMessages getMessages() {
        return this.messages;
    }

    private void setMessages(IngameMessages messages) {
        this.messages = messages;
    }

    public void setThisProcessInformation(ProcessInformation thisProcessInformation) {
        this.thisProcessInformation = thisProcessInformation;
    }
}