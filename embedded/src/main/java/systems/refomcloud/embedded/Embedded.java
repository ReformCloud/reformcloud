/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.refomcloud.embedded;

import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.config.EmbeddedConfig;
import systems.refomcloud.embedded.database.DefaultEmbeddedDatabaseProvider;
import systems.refomcloud.embedded.group.DefaultEmbeddedMainGroupProvider;
import systems.refomcloud.embedded.group.DefaultEmbeddedProcessGroupProvider;
import systems.refomcloud.embedded.messaging.DefaultEmbeddedChannelMessageProvider;
import systems.refomcloud.embedded.network.EmbeddedChannelListener;
import systems.refomcloud.embedded.node.DefaultEmbeddedNodeInformationProvider;
import systems.refomcloud.embedded.player.DefaultEmbeddedPlayerProvider;
import systems.refomcloud.embedded.process.DefaultEmbeddedProcessProvider;
import systems.refomcloud.embedded.processors.ChannelMessageProcessor;
import systems.refomcloud.embedded.processors.PacketConnectPlayerToServerProcessor;
import systems.refomcloud.embedded.processors.PacketDisconnectPlayerProcessor;
import systems.refomcloud.embedded.processors.PacketPlayEffectToPlayerProcessor;
import systems.refomcloud.embedded.processors.PacketPlaySoundToPlayerProcessor;
import systems.refomcloud.embedded.processors.PacketSendActionBarProcessor;
import systems.refomcloud.embedded.processors.PacketSendBossBarProcessor;
import systems.refomcloud.embedded.processors.PacketSendPlayerMessageProcessor;
import systems.refomcloud.embedded.processors.PacketSendPlayerTitleProcessor;
import systems.refomcloud.embedded.processors.PacketSetPlayerLocationProcessor;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.ExecutorType;
import systems.reformcloud.dependency.DependencyLoader;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.event.events.process.ProcessUpdateEvent;
import systems.reformcloud.event.handler.Listener;
import systems.reformcloud.group.messages.IngameMessages;
import systems.reformcloud.group.process.player.PlayerAccessConfiguration;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.channel.manager.ChannelManager;
import systems.reformcloud.network.client.NetworkClient;
import systems.reformcloud.network.packet.Packet;
import systems.reformcloud.network.packet.PacketProvider;
import systems.reformcloud.network.packet.query.QueryManager;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.protocol.node.ApiToNodeGetIngameMessages;
import systems.reformcloud.protocol.node.ApiToNodeGetIngameMessagesResult;
import systems.reformcloud.protocol.processor.PacketProcessorManager;
import systems.reformcloud.protocol.shared.PacketChannelMessage;
import systems.reformcloud.protocol.shared.PacketConnectPlayerToServer;
import systems.reformcloud.protocol.shared.PacketDisconnectPlayer;
import systems.reformcloud.protocol.shared.PacketPlayEffectToPlayer;
import systems.reformcloud.protocol.shared.PacketPlaySoundToPlayer;
import systems.reformcloud.protocol.shared.PacketSendActionBar;
import systems.reformcloud.protocol.shared.PacketSendBossBar;
import systems.reformcloud.protocol.shared.PacketSendPlayerMessage;
import systems.reformcloud.protocol.shared.PacketSendPlayerTitle;
import systems.reformcloud.protocol.shared.PacketSetPlayerLocation;
import systems.reformcloud.provider.ChannelMessageProvider;
import systems.reformcloud.provider.DatabaseProvider;
import systems.reformcloud.provider.MainGroupProvider;
import systems.reformcloud.provider.NodeInformationProvider;
import systems.reformcloud.provider.PlayerProvider;
import systems.reformcloud.provider.ProcessGroupProvider;
import systems.reformcloud.provider.ProcessProvider;
import systems.reformcloud.registry.service.ServiceRegistry;
import systems.reformcloud.shared.dependency.DefaultDependencyLoader;
import systems.reformcloud.shared.event.DefaultEventManager;
import systems.reformcloud.shared.json.GsonFactories;
import systems.reformcloud.shared.network.channel.DefaultChannelManager;
import systems.reformcloud.shared.network.client.DefaultNetworkClient;
import systems.reformcloud.shared.network.packet.DefaultPacketProvider;
import systems.reformcloud.shared.network.packet.DefaultQueryManager;
import systems.reformcloud.shared.platform.Platform;
import systems.reformcloud.shared.registry.service.DefaultServiceRegistry;
import systems.reformcloud.task.Task;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class can only get called if the environment is {@link ExecutorType#API}.
 * Check this by using {@link ExecutorAPI#getType()}. If the current instance is not an api instance
 * just use the default cloud api based on {@link ExecutorAPI#getInstance()}.
 */
public abstract class Embedded extends ExecutorAPI {

  protected final ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
  protected final NetworkClient networkClient = new DefaultNetworkClient();
  protected final EmbeddedConfig config;

  private final DatabaseProvider databaseProvider = new DefaultEmbeddedDatabaseProvider();
  private final ChannelMessageProvider channelMessageProvider = new DefaultEmbeddedChannelMessageProvider();
  private final NodeInformationProvider nodeInformationProvider = new DefaultEmbeddedNodeInformationProvider();
  private final PlayerProvider playerProvider = new DefaultEmbeddedPlayerProvider();
  private final MainGroupProvider mainGroupProvider = new DefaultEmbeddedMainGroupProvider();
  private final ProcessGroupProvider processGroupProvider = new DefaultEmbeddedProcessGroupProvider();
  private final ProcessProvider processProvider = new DefaultEmbeddedProcessProvider();
  private final DependencyLoader dependencyLoader = new DefaultDependencyLoader();

  protected int maxPlayers;
  protected ProcessInformation processInformation;
  protected IngameMessages ingameMessages = new IngameMessages();

  protected Embedded() {
    GsonFactories.init();
    ExecutorAPI.setInstance(this);

    this.serviceRegistry.setProvider(EventManager.class, new DefaultEventManager(), false, true);
    this.serviceRegistry.setProvider(ChannelManager.class, new DefaultChannelManager(), true);
    this.serviceRegistry.setProvider(PacketProvider.class, new DefaultPacketProvider(), false, true);
    this.serviceRegistry.setProvider(QueryManager.class, new DefaultQueryManager(), false, true);

    this.serviceRegistry.getProviderUnchecked(EventManager.class).registerListener(new CurrentProcessUpdateEventListener());

    this.config = new EmbeddedConfig();
    this.processInformation = this.config.getProcessInformation();

    Lock lock = new ReentrantLock();
    try {
      lock.lock();
      Condition condition = lock.newCondition();

      this.networkClient.connect(
        this.config.getConnectionHost(),
        this.config.getConnectionPort(),
        channel -> new EmbeddedChannelListener(channel, lock, condition)
      );

      try {
        if (!condition.await(30, TimeUnit.SECONDS)) {
          System.exit(-1);
        }
      } catch (InterruptedException exception) {
        throw new RuntimeException(exception);
      }

      if (!this.serviceRegistry.getProviderUnchecked(ChannelManager.class).getFirstChannel().isPresent()) {
        System.exit(-1);
      }
    } finally {
      lock.unlock();
    }

    this.sendSyncQuery(new ApiToNodeGetIngameMessages()).ifPresent(result -> {
      if (result instanceof ApiToNodeGetIngameMessagesResult) {
        this.ingameMessages = ((ApiToNodeGetIngameMessagesResult) result).getMessages();
      }
    });

    this.processInformation.setCurrentState(this.processInformation.getInitialState());

    PacketProcessorManager.getInstance()
      .registerProcessor(new ChannelMessageProcessor(), PacketChannelMessage.class)
      .registerProcessor(new PacketConnectPlayerToServerProcessor(), PacketConnectPlayerToServer.class)
      .registerProcessor(new PacketDisconnectPlayerProcessor(), PacketDisconnectPlayer.class)
      .registerProcessor(new PacketPlayEffectToPlayerProcessor(), PacketPlayEffectToPlayer.class)
      .registerProcessor(new PacketPlaySoundToPlayerProcessor(), PacketPlaySoundToPlayer.class)
      .registerProcessor(new PacketSendPlayerMessageProcessor(), PacketSendPlayerMessage.class)
      .registerProcessor(new PacketSendPlayerTitleProcessor(), PacketSendPlayerTitle.class)
      .registerProcessor(new PacketSendActionBarProcessor(), PacketSendActionBar.class)
      .registerProcessor(new PacketSendBossBarProcessor(), PacketSendBossBar.class)
      .registerProcessor(new PacketSetPlayerLocationProcessor(), PacketSetPlayerLocation.class);

    Runtime.getRuntime().addShutdownHook(new Thread(this.networkClient::closeSync));
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

  @NotNull
  @Override
  public DependencyLoader getDependencyLoader() {
    return this.dependencyLoader;
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
    this.processInformation.setRuntimeInformation(Platform.createProcessRuntimeInformation());
    this.updatePlayersOfEnvironment(this.processInformation);
    this.processProvider.updateProcessInformation(this.processInformation);
  }

  protected void updateMaxPlayers() {
    final PlayerAccessConfiguration configuration = this.processInformation.getProcessGroup().getPlayerAccessConfiguration();
    if (configuration.isUsePlayerLimit() && configuration.getMaxPlayers() >= 0) {
      this.maxPlayers = configuration.getMaxPlayers();
    } else {
      this.maxPlayers = this.getMaxPlayersOfEnvironment();
    }
  }

  public int getMaxPlayers() {
    this.updateMaxPlayers();
    return this.maxPlayers;
  }

  protected abstract int getMaxPlayersOfEnvironment();

  protected abstract void updatePlayersOfEnvironment(@NotNull ProcessInformation information);

  public final class CurrentProcessUpdateEventListener {

    @Listener
    public void handle(@NotNull ProcessUpdateEvent event) {
      if (Embedded.this.processInformation.getId().getUniqueId().equals(event.getProcessInformation().getId().getUniqueId())) {
        Embedded.this.processInformation = event.getProcessInformation();
      }
    }
  }
}
