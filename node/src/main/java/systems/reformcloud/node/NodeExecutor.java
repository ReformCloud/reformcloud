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
package systems.reformcloud.node;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.ExecutorType;
import systems.reformcloud.application.ApplicationLoader;
import systems.reformcloud.base.Conditions;
import systems.reformcloud.command.CommandManager;
import systems.reformcloud.dependency.DependencyLoader;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.group.process.ProcessGroup;
import systems.reformcloud.http.server.HttpServer;
import systems.reformcloud.language.TranslationHolder;
import systems.reformcloud.network.address.DefaultNetworkAddress;
import systems.reformcloud.network.address.NetworkAddress;
import systems.reformcloud.network.channel.manager.ChannelManager;
import systems.reformcloud.network.packet.PacketProvider;
import systems.reformcloud.network.packet.query.QueryManager;
import systems.reformcloud.network.server.NetworkServer;
import systems.reformcloud.node.application.DefaultApplicationLoader;
import systems.reformcloud.node.argument.ArgumentParser;
import systems.reformcloud.node.cluster.ClusterManager;
import systems.reformcloud.node.cluster.DefaultClusterManager;
import systems.reformcloud.node.commands.CommandClear;
import systems.reformcloud.node.commands.CommandCluster;
import systems.reformcloud.node.commands.CommandCreate;
import systems.reformcloud.node.commands.CommandGroup;
import systems.reformcloud.node.commands.CommandHelp;
import systems.reformcloud.node.commands.CommandLaunch;
import systems.reformcloud.node.commands.CommandLog;
import systems.reformcloud.node.commands.CommandPlayers;
import systems.reformcloud.node.commands.CommandProcess;
import systems.reformcloud.node.commands.CommandReload;
import systems.reformcloud.node.commands.CommandStop;
import systems.reformcloud.node.commands.CommandTemplate;
import systems.reformcloud.node.config.NodeConfig;
import systems.reformcloud.node.config.NodeExecutorConfig;
import systems.reformcloud.node.console.DefaultNodeConsole;
import systems.reformcloud.node.database.H2DatabaseProvider;
import systems.reformcloud.node.factory.DefaultProcessFactoryController;
import systems.reformcloud.node.factory.ProcessFactoryController;
import systems.reformcloud.node.group.DefaultNodeMainGroupProvider;
import systems.reformcloud.node.group.DefaultNodeProcessGroupProvider;
import systems.reformcloud.node.http.server.DefaultHttpServer;
import systems.reformcloud.node.logger.CloudLogger;
import systems.reformcloud.node.messaging.DefaultNodeChannelMessageProvider;
import systems.reformcloud.node.network.NodeClientChannelListener;
import systems.reformcloud.node.network.NodeNetworkClient;
import systems.reformcloud.node.network.NodeServerChannelListener;
import systems.reformcloud.node.player.DefaultNodePlayerProvider;
import systems.reformcloud.node.process.DefaultNodeLocalProcessWrapper;
import systems.reformcloud.node.process.DefaultNodeProcessProvider;
import systems.reformcloud.node.process.configurator.ProcessConfiguratorRegistry;
import systems.reformcloud.node.processors.ApiToNodeGetIngameMessagesProcessor;
import systems.reformcloud.node.processors.ChannelMessageProcessor;
import systems.reformcloud.node.processors.NodeToNodeProcessCommandProcessor;
import systems.reformcloud.node.processors.NodeToNodePublishChannelMessageProcessor;
import systems.reformcloud.node.processors.NodeToNodeRequestNodeInformationUpdateProcessor;
import systems.reformcloud.node.processors.NodeToNodeTabCompleteCommandProcessor;
import systems.reformcloud.node.processors.player.PacketConnectPlayerToServerProcessor;
import systems.reformcloud.node.processors.player.PacketDisconnectPlayerProcessor;
import systems.reformcloud.node.processors.player.PacketPlayEffectToPlayerProcessor;
import systems.reformcloud.node.processors.player.PacketPlaySoundToPlayerProcessor;
import systems.reformcloud.node.processors.player.PacketSendActionBarProcessor;
import systems.reformcloud.node.processors.player.PacketSendBossBarProcessor;
import systems.reformcloud.node.processors.player.PacketSendPlayerMessageProcessor;
import systems.reformcloud.node.processors.player.PacketSendPlayerTitleProcessor;
import systems.reformcloud.node.processors.player.PacketSetPlayerLocationProcessor;
import systems.reformcloud.node.protocol.NodeToNodeProcessCommand;
import systems.reformcloud.node.protocol.NodeToNodePublishChannelMessage;
import systems.reformcloud.node.protocol.NodeToNodeRequestNodeInformationUpdate;
import systems.reformcloud.node.protocol.NodeToNodeTabCompleteCommand;
import systems.reformcloud.node.protocol.PacketRegister;
import systems.reformcloud.node.provider.DefaultNodeNodeInformationProvider;
import systems.reformcloud.node.runnables.AutoStartRunnable;
import systems.reformcloud.node.runnables.NodeInformationUpdateRunnable;
import systems.reformcloud.node.runnables.OnlinePercentCheckerTask;
import systems.reformcloud.node.runnables.ProcessScreenTickRunnable;
import systems.reformcloud.node.runnables.ServerWatchdogRunnable;
import systems.reformcloud.node.sentry.SentryLoggingLoader;
import systems.reformcloud.node.template.TemplateBackendManager;
import systems.reformcloud.node.template.VersionInstallerRegistry;
import systems.reformcloud.node.tick.CloudTickWorker;
import systems.reformcloud.node.tick.TickedTaskScheduler;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.protocol.node.ApiToNodeGetIngameMessages;
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
import systems.reformcloud.registry.service.ServiceRegistry;
import systems.reformcloud.shared.Constants;
import systems.reformcloud.shared.command.DefaultCommandManager;
import systems.reformcloud.shared.event.DefaultEventManager;
import systems.reformcloud.shared.io.IOUtils;
import systems.reformcloud.shared.network.channel.DefaultChannelManager;
import systems.reformcloud.shared.network.packet.DefaultPacketProvider;
import systems.reformcloud.shared.network.packet.DefaultQueryManager;
import systems.reformcloud.shared.network.server.DefaultNetworkServer;
import systems.reformcloud.shared.network.transport.TransportType;
import systems.reformcloud.shared.node.DefaultNodeInformation;
import systems.reformcloud.shared.platform.Platform;
import systems.reformcloud.shared.random.ThreadLocalFastRandom;
import systems.reformcloud.shared.registry.service.DefaultServiceRegistry;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NodeExecutor extends ExecutorAPI {

  private static final AtomicBoolean RUNNING = new AtomicBoolean(true);

  private final DependencyLoader dependencyLoader;

  private final HttpServer httpServer = new DefaultHttpServer();
  private final NetworkServer networkServer = new DefaultNetworkServer();
  private final NodeNetworkClient networkClient = new NodeNetworkClient();

  private final NodeExecutorConfig nodeExecutorConfig = new NodeExecutorConfig();
  private final ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
  private final DefaultNodeProcessProvider processProvider = new DefaultNodeProcessProvider();
  private final PlayerProvider playerProvider = new DefaultNodePlayerProvider();
  private final ChannelMessageProvider channelMessageProvider = new DefaultNodeChannelMessageProvider();
  private final TickedTaskScheduler taskScheduler = new TickedTaskScheduler();
  private final CloudTickWorker cloudTickWorker = new CloudTickWorker(this.taskScheduler);

  private NodeConfig nodeConfig;
  private DefaultNodeMainGroupProvider mainGroupProvider;
  private DefaultNodeProcessGroupProvider processGroupProvider;
  private DefaultNodeNodeInformationProvider nodeInformationProvider;
  private DefaultNodeConsole console;
  private CloudLogger logger;
  private ArgumentParser argumentParser;

  private DefaultNodeInformation currentNodeInformation;

  protected NodeExecutor(DependencyLoader dependencyLoader) {
    Conditions.isTrue(Paths.get("").toAbsolutePath().toString().indexOf('!') == -1, "Cannot run ReformCloud in directory with ! in path.");

    ExecutorAPI.setInstance(this);
    super.type = ExecutorType.NODE;

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        this.shutdown();
      } catch (final Throwable throwable) {
        throwable.printStackTrace();
      }
    }, "Shutdown-Hook"));

    this.dependencyLoader = dependencyLoader;
    this.registerDefaultServices();
  }

  @NotNull
  public static NodeExecutor getInstance() {
    return (NodeExecutor) ExecutorAPI.getInstance();
  }

  public static boolean isRunning() {
    return RUNNING.get();
  }

  protected synchronized void bootstrap(@NotNull ArgumentParser argumentParser) {
    this.console = new DefaultNodeConsole();
    this.logger = new CloudLogger(this.console.getLineReader());
    this.argumentParser = argumentParser;

    PacketRegister.register();
    this.registerDefaultPacketProcessors();

    this.mainGroupProvider = new DefaultNodeMainGroupProvider(System.getProperty("systems.reformcloud.main-group-dir", "reformcloud/groups/main"));
    this.processGroupProvider = new DefaultNodeProcessGroupProvider(System.getProperty("systems.reformcloud.sub-group-dir", "reformcloud/groups/sub"));

    this.nodeExecutorConfig.init();
    this.nodeConfig = this.nodeExecutorConfig.getNodeConfig();
    SentryLoggingLoader.loadSentryLogging(this); // load after config
    this.nodeInformationProvider = new DefaultNodeNodeInformationProvider(this.currentNodeInformation = new DefaultNodeInformation(
      this.nodeConfig.getName(),
      this.nodeConfig.getUniqueID(),
      System.currentTimeMillis(),
      0L,
      this.nodeConfig.getMaxMemory(),
      NetworkAddress.fromInetAddress(this.nodeConfig.getStartHost(), 0)
    ));

    for (String mainGroupName : this.mainGroupProvider.getMainGroupNames()) {
      System.out.println(TranslationHolder.translate("loading-main-group", mainGroupName));
    }

    for (String processGroupName : this.processGroupProvider.getProcessGroupNames()) {
      System.out.println(TranslationHolder.translate("loading-process-group", processGroupName));
    }

    this.serviceRegistry.setProvider(ClusterManager.class, new DefaultClusterManager(
      this.nodeInformationProvider,
      this.processProvider,
      this.processGroupProvider,
      this.mainGroupProvider,
      this.currentNodeInformation
    ), false, true);

    this.serviceRegistry.getProviderUnchecked(ApplicationLoader.class).detectApplications();
    this.serviceRegistry.getProviderUnchecked(ApplicationLoader.class).loadApplications();

    TemplateBackendManager.registerDefaults();
    VersionInstallerRegistry.registerDefaults();
    ProcessConfiguratorRegistry.registerDefaults();

    this.startNetworkListeners();

    this.taskScheduler.addPermanentTask(new AutoStartRunnable());
    this.taskScheduler.addPermanentTask(new NodeInformationUpdateRunnable());
    this.taskScheduler.addPermanentTask(new ServerWatchdogRunnable());
    this.taskScheduler.addPermanentTask(new ProcessScreenTickRunnable());
    this.taskScheduler.addPermanentTask(new OnlinePercentCheckerTask());

    this.loadCommands();
    this.serviceRegistry.getProviderUnchecked(ApplicationLoader.class).enableApplications();
  }

  public synchronized void reload() {
    System.out.println(TranslationHolder.translate("runtime-try-reload"));

    final long startTime = System.currentTimeMillis();
    this.serviceRegistry.getProviderUnchecked(ApplicationLoader.class).disableApplications();

    this.mainGroupProvider.reload();
    this.processGroupProvider.reload();

    for (ProcessGroup processGroup : this.processGroupProvider.getProcessGroups()) {
      for (ProcessInformation information : this.processProvider.getProcessesByProcessGroup(processGroup.getName())) {
        information.setProcessGroup(processGroup);
        this.processProvider.updateProcessInformation(information);
      }
    }

    this.nodeConfig = this.nodeExecutorConfig.reload();

    this.currentNodeInformation = new DefaultNodeInformation(
      this.currentNodeInformation.getName(),
      this.currentNodeInformation.getUniqueId(),
      this.currentNodeInformation.getStartupMillis(),
      this.currentNodeInformation.getUsedMemory(),
      this.nodeConfig.getMaxMemory(),
      NetworkAddress.fromInetAddress(this.nodeConfig.getStartHost(), 0)
    );

    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishProcessGroupSet(
      ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroups()
    );
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishMainGroupSet(
      ExecutorAPI.getInstance().getMainGroupProvider().getMainGroups()
    );

    this.serviceRegistry.getProviderUnchecked(ApplicationLoader.class).detectApplications();
    this.serviceRegistry.getProviderUnchecked(ApplicationLoader.class).loadApplications();
    this.serviceRegistry.getProviderUnchecked(ApplicationLoader.class).enableApplications();

    System.out.println(TranslationHolder.translate("runtime-reload-done", Constants.TWO_POINT_THREE_DECIMAL_FORMAT.format((System.currentTimeMillis() - startTime) / 1000d)));
  }

  public void shutdown() throws Exception {
    // prevent duplicate shutdown requests
    synchronized (this) {
      if (!RUNNING.getAndSet(false)) {
        return;
      }
    }

    System.out.println(TranslationHolder.translate("application-stop"));

    System.out.println(TranslationHolder.translate("application-net-server-close"));
    this.networkServer.closeAll();
    this.httpServer.closeAll();
    System.out.println(TranslationHolder.translate("application-net-client-close"));
    this.networkClient.closeSync();

    System.out.println(TranslationHolder.translate("application-stop-task-scheduler"));
    this.taskScheduler.close();
    this.serviceRegistry.getProviderUnchecked(ApplicationLoader.class).disableApplications();

    System.out.println(TranslationHolder.translate("application-stop-processes"));
    this.processProvider.closeNow(); // important to close the scheduler BEFORE the processes to prevent new processes to start
    System.out.println(TranslationHolder.translate("application-stop-remove-temp-dir"));
    IOUtils.deleteDirectorySilently(Paths.get("reformcloud/temp"));

    System.out.println(TranslationHolder.translate("application-stop-finished"));

    this.logger.close();
    this.console.close();

    if (!Thread.currentThread().getName().equals("Shutdown-Hook")) {
      // now call all other shutdown hooks
      System.exit(0);
    }
  }

  private void startNetworkListeners() {
    System.out.println(TranslationHolder.translate("network-transport-type-choose", TransportType.BEST_TYPE.getName()));

    for (NetworkAddress networkListener : this.nodeConfig.getNetworkListeners()) {
      this.networkServer.bind(networkListener.getHost(), networkListener.getPort(), NodeServerChannelListener::new);
    }

    for (NetworkAddress httpNetworkListener : this.nodeConfig.getHttpNetworkListeners()) {
      this.httpServer.bind(httpNetworkListener.getHost(), httpNetworkListener.getPort());
    }

    for (NetworkAddress clusterNode : this.nodeConfig.getClusterNodes()) {
      if (this.networkClient.connect(
        clusterNode.getHost(),
        clusterNode.getPort(),
        NodeClientChannelListener::new
      )) {
        System.out.println(TranslationHolder.translate(
          "network-node-connection-to-other-node-success", clusterNode.getHost(), clusterNode.getPort()
        ));
      } else {
        System.out.println(TranslationHolder.translate(
          "network-node-connection-to-other-node-not-successful", clusterNode.getHost(), clusterNode.getPort()
        ));
      }
    }
  }

  @NotNull
  @Override
  public ChannelMessageProvider getChannelMessageProvider() {
    return this.channelMessageProvider;
  }

  @NotNull
  @Override
  public DatabaseProvider getDatabaseProvider() {
    return this.serviceRegistry.getProvider(DatabaseProvider.class).orElseThrow(() -> new RuntimeException("Database provider was unregistered"));
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
  public DefaultNodeProcessProvider getProcessProvider() {
    return this.processProvider;
  }

  @NotNull
  @Override
  public ServiceRegistry getServiceRegistry() {
    return this.serviceRegistry;
  }

  @Override
  public @NotNull DependencyLoader getDependencyLoader() {
    return this.dependencyLoader;
  }

  @Override
  public boolean isReady() {
    return NodeNetworkClient.CONNECTIONS.isEmpty();
  }

  @NotNull
  public TickedTaskScheduler getTaskScheduler() {
    return this.taskScheduler;
  }

  @NotNull
  public CloudTickWorker getCloudTickWorker() {
    return this.cloudTickWorker;
  }

  @NotNull
  public NodeConfig getNodeConfig() {
    return this.nodeConfig;
  }

  @NotNull
  public NodeExecutorConfig getNodeExecutorConfig() {
    return this.nodeExecutorConfig;
  }

  @NotNull
  public DefaultNodeInformation updateCurrentNodeInformation() {
    this.currentNodeInformation.update();
    return this.currentNodeInformation;
  }

  @NotNull
  public DefaultNodeInformation getCurrentNodeInformation() {
    return this.currentNodeInformation;
  }

  @NotNull
  public ArgumentParser getArgumentParser() {
    return this.argumentParser;
  }

  @NotNull
  public NetworkAddress getAnyAddress() {
    List<DefaultNetworkAddress> networkListeners = this.nodeConfig.getNetworkListeners();
    Conditions.isTrue(!networkListeners.isEmpty(), "Try to run cloud system with no network listener configured");
    return networkListeners.size() == 1 ? networkListeners.get(0) : networkListeners.get(ThreadLocalFastRandom.current().nextInt(networkListeners.size()));
  }

  @NotNull
  public String getSelfName() {
    return this.nodeConfig.getName();
  }

  @NotNull
  public DefaultNodeConsole getConsole() {
    return this.console;
  }

  @NotNull
  public DefaultNodeMainGroupProvider getDefaultMainGroupProvider() {
    return this.mainGroupProvider;
  }

  @NotNull
  public DefaultNodeProcessGroupProvider getDefaultProcessGroupProvider() {
    return this.processGroupProvider;
  }

  @NotNull
  public DefaultNodeProcessProvider getDefaultNodeProcessProvider() {
    return this.processProvider;
  }

  @NotNull
  public HttpServer getHttpServer() {
    return this.httpServer;
  }

  public boolean isOwnIdentity(@NotNull String name) {
    return this.nodeConfig.getName().equals(name);
  }

  private void loadCommands() {
    this.serviceRegistry.getProviderUnchecked(CommandManager.class)
      .registerCommand(new CommandProcess(), "Management of local and remote processes", "p", "process", "server", "proxy")
      .registerCommand(new CommandCluster(), "Management of nodes in the cluster", "clu", "cluster", "c")
      .registerCommand(new CommandPlayers(), "Management of players on processes", "pl", "players")
      .registerCommand(new CommandGroup(), "Administration of Main/Sub groups", "g", "group", "groups")
      .registerCommand(new CommandCreate(), "Creation of sub/main groups", "create")
      .registerCommand(new CommandLaunch(), "Starting or preparing processes", "launch", "l")
      .registerCommand(new CommandStop(), "Terminates the program", "stop", "exit", "shutdown")
      .registerCommand(new CommandLog(), "Uploading log files of running processes", "log")
      .registerCommand(new CommandReload(), "Reloads the system", "reload", "rl")
      .registerCommand(new CommandClear(), "Empties the console", "clear", "cls")
      .registerCommand(new CommandTemplate(), "Manages the templates", "template", "t", "templates")
      .registerCommand(new CommandHelp(), "Shows an overview of all available commands and their aliases", "help", "ask", "?");
  }

  public boolean canStartProcesses(int neededMemory) {
    for (DefaultNodeLocalProcessWrapper processWrapper : this.processProvider.getProcessWrappers()) {
      if (processWrapper.isStarted()) {
        neededMemory += processWrapper.getMemory();
      }
    }

    if (neededMemory >= this.nodeConfig.getMaxMemory()) {
      return false;
    }

    double cpuUsageSystem = Platform.getOperatingSystemMxBean().getSystemCpuLoad();
    return cpuUsageSystem <= 0 || cpuUsageSystem * 100 < this.nodeConfig.getMaxSystemCpuUsage();
  }

  private void registerDefaultServices() {
    this.serviceRegistry.setProvider(CommandManager.class, new DefaultCommandManager(), false, true);
    this.serviceRegistry.setProvider(ApplicationLoader.class, new DefaultApplicationLoader(), false, true);
    this.serviceRegistry.setProvider(DatabaseProvider.class, new H2DatabaseProvider(), false, true);
    this.serviceRegistry.setProvider(EventManager.class, new DefaultEventManager(), false, true);
    this.serviceRegistry.setProvider(ChannelManager.class, new DefaultChannelManager(), false, true);
    this.serviceRegistry.setProvider(PacketProvider.class, new DefaultPacketProvider(), false, true);
    this.serviceRegistry.setProvider(QueryManager.class, new DefaultQueryManager(), false, true);
    this.serviceRegistry.setProvider(ProcessFactoryController.class, new DefaultProcessFactoryController(this.processProvider), false, true);
  }

  private void registerDefaultPacketProcessors() {
    PacketProcessorManager.getInstance()
      .registerProcessor(new PacketConnectPlayerToServerProcessor(), PacketConnectPlayerToServer.class)
      .registerProcessor(new PacketDisconnectPlayerProcessor(), PacketDisconnectPlayer.class)
      .registerProcessor(new PacketPlayEffectToPlayerProcessor(), PacketPlayEffectToPlayer.class)
      .registerProcessor(new PacketPlaySoundToPlayerProcessor(), PacketPlaySoundToPlayer.class)
      .registerProcessor(new PacketSendBossBarProcessor(), PacketSendBossBar.class)
      .registerProcessor(new PacketSendActionBarProcessor(), PacketSendActionBar.class)
      .registerProcessor(new PacketSendPlayerMessageProcessor(), PacketSendPlayerMessage.class)
      .registerProcessor(new PacketSendPlayerTitleProcessor(), PacketSendPlayerTitle.class)
      .registerProcessor(new PacketSetPlayerLocationProcessor(), PacketSetPlayerLocation.class)
      .registerProcessor(new ApiToNodeGetIngameMessagesProcessor(), ApiToNodeGetIngameMessages.class)
      .registerProcessor(new ChannelMessageProcessor(), PacketChannelMessage.class)
      .registerProcessor(new NodeToNodeProcessCommandProcessor(), NodeToNodeProcessCommand.class)
      .registerProcessor(new NodeToNodePublishChannelMessageProcessor(), NodeToNodePublishChannelMessage.class)
      .registerProcessor(new NodeToNodeRequestNodeInformationUpdateProcessor(), NodeToNodeRequestNodeInformationUpdate.class)
      .registerProcessor(new NodeToNodeTabCompleteCommandProcessor(), NodeToNodeTabCompleteCommand.class);
  }
}
