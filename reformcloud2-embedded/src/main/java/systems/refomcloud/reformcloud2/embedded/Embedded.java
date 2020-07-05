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
package systems.refomcloud.reformcloud2.embedded;

import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.config.EmbeddedConfig;
import systems.refomcloud.reformcloud2.embedded.database.DefaultEmbeddedDatabaseProvider;
import systems.refomcloud.reformcloud2.embedded.group.DefaultEmbeddedMainGroupProvider;
import systems.refomcloud.reformcloud2.embedded.group.DefaultEmbeddedProcessGroupProvider;
import systems.refomcloud.reformcloud2.embedded.messaging.DefaultEmbeddedChannelMessageProvider;
import systems.refomcloud.reformcloud2.embedded.network.EmbeddedEndpointChannelReader;
import systems.refomcloud.reformcloud2.embedded.node.DefaultEmbeddedNodeInformationProvider;
import systems.refomcloud.reformcloud2.embedded.player.DefaultEmbeddedPlayerProvider;
import systems.refomcloud.reformcloud2.embedded.process.DefaultEmbeddedProcessProvider;
import systems.refomcloud.reformcloud2.embedded.processors.*;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.event.events.process.ProcessUpdateEvent;
import systems.reformcloud.reformcloud2.executor.api.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.network.packet.PacketProvider;
import systems.reformcloud.reformcloud2.executor.api.network.packet.query.QueryManager;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.provider.*;
import systems.reformcloud.reformcloud2.executor.api.registry.service.ServiceRegistry;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetIngameMessages;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetIngameMessagesResult;
import systems.reformcloud.reformcloud2.protocol.processor.PacketProcessorManager;
import systems.reformcloud.reformcloud2.protocol.shared.*;
import systems.reformcloud.reformcloud2.shared.event.DefaultEventManager;
import systems.reformcloud.reformcloud2.shared.network.channel.DefaultChannelManager;
import systems.reformcloud.reformcloud2.shared.network.client.DefaultNetworkClient;
import systems.reformcloud.reformcloud2.shared.network.packet.DefaultPacketProvider;
import systems.reformcloud.reformcloud2.shared.network.packet.DefaultQueryManager;
import systems.reformcloud.reformcloud2.shared.registry.service.DefaultServiceRegistry;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class can only get called if the environment is {@link systems.reformcloud.reformcloud2.executor.api.ExecutorType#API}.
 * Check this by using {@link ExecutorAPI#getType()}. If the current instance is not an api instance
 * just use the default cloud api based on {@link ExecutorAPI#getInstance()}.
 */
public abstract class Embedded extends ExecutorAPI {

    protected ProcessInformation processInformation;
    protected IngameMessages ingameMessages = new IngameMessages();

    protected final ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
    protected final NetworkClient networkClient = new DefaultNetworkClient();
    protected final EmbeddedConfig config = new EmbeddedConfig();

    private final DatabaseProvider databaseProvider = new DefaultEmbeddedDatabaseProvider();
    private final ChannelMessageProvider channelMessageProvider = new DefaultEmbeddedChannelMessageProvider();
    private final NodeInformationProvider nodeInformationProvider = new DefaultEmbeddedNodeInformationProvider();
    private final PlayerProvider playerProvider = new DefaultEmbeddedPlayerProvider();
    private final MainGroupProvider mainGroupProvider = new DefaultEmbeddedMainGroupProvider();
    private final ProcessGroupProvider processGroupProvider = new DefaultEmbeddedProcessGroupProvider();
    private final ProcessProvider processProvider = new DefaultEmbeddedProcessProvider();

    protected Embedded() {
        ExecutorAPI.setInstance(this);

        this.serviceRegistry.setProvider(EventManager.class, new DefaultEventManager(), false, true);
        this.serviceRegistry.setProvider(ChannelManager.class, new DefaultChannelManager(), true);
        this.serviceRegistry.setProvider(PacketProvider.class, new DefaultPacketProvider(), false, true);
        this.serviceRegistry.setProvider(QueryManager.class, new DefaultQueryManager(), false, true);

        this.serviceRegistry.getProviderUnchecked(EventManager.class).registerListener(new CurrentProcessUpdateEventListener());
        this.processInformation = this.config.getProcessInformation();

        Lock lock = new ReentrantLock();
        try {
            lock.lock();
            Condition condition = lock.newCondition();

            this.networkClient.connectSync(
                    this.config.getConnectionHost(),
                    this.config.getConnectionPort(),
                    () -> new EmbeddedEndpointChannelReader(lock, condition)
            );

            try {
                condition.await(30, TimeUnit.SECONDS);
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }

            if (!this.serviceRegistry.getProviderUnchecked(ChannelManager.class).getFirstChannel().isPresent()) {
                System.exit(-1);
                return;
            }
        } finally {
            lock.unlock();
        }

        this.sendSyncQuery(new ApiToNodeGetIngameMessages()).ifPresent(result -> {
            if (result instanceof ApiToNodeGetIngameMessagesResult) {
                this.ingameMessages = ((ApiToNodeGetIngameMessagesResult) result).getMessages();
            }
        });

        this.processInformation.getProcessDetail().setProcessState(this.processInformation.getProcessDetail().getInitialState());
        this.processInformation.getNetworkInfo().setConnected(true);

        if (this.processInformation.getProcessDetail().getMaxPlayers() < 0) {
            this.processInformation.updateMaxPlayers(this.getMaxPlayersOfEnvironment());
        }

        PacketProcessorManager.getInstance()
                .registerProcessor(new ChannelMessageProcessor(), PacketChannelMessage.class)
                .registerProcessor(new PacketConnectPlayerToServerProcessor(), PacketConnectPlayerToServer.class)
                .registerProcessor(new PacketDisconnectPlayerProcessor(), PacketDisconnectPlayer.class)
                .registerProcessor(new PacketPlayEffectToPlayerProcessor(), PacketPlayEffectToPlayer.class)
                .registerProcessor(new PacketPlaySoundToPlayerProcessor(), PacketPlaySoundToPlayer.class)
                .registerProcessor(new PacketSendPlayerMessageProcessor(), PacketSendPlayerMessage.class)
                .registerProcessor(new PacketSendPlayerTitleProcessor(), PacketSendPlayerTitle.class)
                .registerProcessor(new PacketSetPlayerLocationProcessor(), PacketSetPlayerLocation.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.networkClient.disconnect()));
        this.updateCurrentProcessInformation();
    }

    @NotNull
    public static Embedded getInstance() {
        return (Embedded) ExecutorAPI.getInstance();
    }

    @NotNull
    @Override
    public ChannelMessageProvider getChannelMessageProvider() {
        return this.channelMessageProvider;
    }

    @NotNull
    @Override
    public DatabaseProvider getDatabaseProvider() {
        return this.databaseProvider;
    }

    @NotNull
    @Override
    public MainGroupProvider getMainGroupProvider() {
        return this.mainGroupProvider;
    }

    @NotNull
    @Override
    public NodeInformationProvider getNodeInformationProvider() {
        return this.nodeInformationProvider;
    }

    @NotNull
    @Override
    public PlayerProvider getPlayerProvider() {
        return this.playerProvider;
    }

    @NotNull
    @Override
    public ProcessGroupProvider getProcessGroupProvider() {
        return this.processGroupProvider;
    }

    @NotNull
    @Override
    public ProcessProvider getProcessProvider() {
        return this.processProvider;
    }

    @NotNull
    @Override
    public ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    @Override
    public boolean isReady() {
        return this.serviceRegistry.getProviderUnchecked(ChannelManager.class).getFirstChannel().isPresent();
    }

    @NotNull
    public ProcessInformation getCurrentProcessInformation() {
        return this.processInformation;
    }

    @NotNull
    public EmbeddedConfig getConfig() {
        return this.config;
    }

    @NotNull
    public IngameMessages getIngameMessages() {
        return this.ingameMessages;
    }

    public void sendPacket(@NotNull Packet packet) {
        this.serviceRegistry.getProviderUnchecked(ChannelManager.class).getFirstChannel().ifPresent(e -> e.sendPacket(packet));
    }

    @NotNull
    public Task<Packet> sendQuery(@NotNull Packet packet) {
        Optional<NetworkChannel> channel = this.serviceRegistry.getProviderUnchecked(ChannelManager.class).getFirstChannel();
        return channel
                .map(networkChannel -> this.serviceRegistry.getProviderUnchecked(QueryManager.class).sendPacketQuery(networkChannel, packet))
                .orElseGet(() -> Task.completedTask(null));
    }

    @NotNull
    public Optional<Packet> sendSyncQuery(@NotNull Packet packet) {
        Packet result = this.sendQuery(packet).getUninterruptedly(TimeUnit.SECONDS, 5);
        return Optional.ofNullable(result);
    }

    public void updateCurrentProcessInformation() {
        this.processInformation.updateRuntimeInformation();
        this.processProvider.updateProcessInformation(this.processInformation);
    }

    protected abstract int getMaxPlayersOfEnvironment();

    public final class CurrentProcessUpdateEventListener {

        @Listener
        public void handle(@NotNull ProcessUpdateEvent event) {
            if (Embedded.this.processInformation.getProcessDetail().getProcessUniqueID().equals(event.getProcessInformation().getProcessDetail().getProcessUniqueID())) {
                Embedded.this.processInformation = event.getProcessInformation();
            }
        }
    }
}
