/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.reformcloud.reformcloud2.node;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.command.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.language.loading.LanguageLoader;
import systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.packet.PacketProvider;
import systems.reformcloud.reformcloud2.executor.api.network.packet.query.QueryManager;
import systems.reformcloud.reformcloud2.executor.api.network.server.NetworkServer;
import systems.reformcloud.reformcloud2.executor.api.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.provider.*;
import systems.reformcloud.reformcloud2.executor.api.registry.service.ServiceRegistry;
import systems.reformcloud.reformcloud2.executor.api.utility.NetworkAddress;
import systems.reformcloud.reformcloud2.node.application.DefaultApplicationLoader;
import systems.reformcloud.reformcloud2.node.argument.ArgumentParser;
import systems.reformcloud.reformcloud2.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.node.cluster.DefaultClusterManager;
import systems.reformcloud.reformcloud2.node.commands.*;
import systems.reformcloud.reformcloud2.node.config.NodeConfig;
import systems.reformcloud.reformcloud2.node.config.NodeExecutorConfig;
import systems.reformcloud.reformcloud2.node.console.DefaultNodeConsole;
import systems.reformcloud.reformcloud2.node.database.H2DatabaseProvider;
import systems.reformcloud.reformcloud2.node.factory.DefaultProcessFactoryController;
import systems.reformcloud.reformcloud2.node.factory.ProcessFactoryController;
import systems.reformcloud.reformcloud2.node.group.DefaultNodeMainGroupProvider;
import systems.reformcloud.reformcloud2.node.group.DefaultNodeProcessGroupProvider;
import systems.reformcloud.reformcloud2.node.logger.CloudLogger;
import systems.reformcloud.reformcloud2.node.messaging.DefaultNodeChannelMessageProvider;
import systems.reformcloud.reformcloud2.node.network.NodeClientEndpointChannelReader;
import systems.reformcloud.reformcloud2.node.network.NodeNetworkClient;
import systems.reformcloud.reformcloud2.node.network.NodeServerEndpointChannelReader;
import systems.reformcloud.reformcloud2.node.player.DefaultNodePlayerProvider;
import systems.reformcloud.reformcloud2.node.process.DefaultNodeLocalProcessWrapper;
import systems.reformcloud.reformcloud2.node.process.DefaultNodeProcessProvider;
import systems.reformcloud.reformcloud2.node.process.screen.DefaultProcessScreenController;
import systems.reformcloud.reformcloud2.node.process.screen.ProcessScreenController;
import systems.reformcloud.reformcloud2.node.processors.*;
import systems.reformcloud.reformcloud2.node.processors.player.*;
import systems.reformcloud.reformcloud2.node.protocol.*;
import systems.reformcloud.reformcloud2.node.provider.DefaultNodeNodeInformationProvider;
import systems.reformcloud.reformcloud2.node.runnables.*;
import systems.reformcloud.reformcloud2.node.sentry.SentryLoggingLoader;
import systems.reformcloud.reformcloud2.node.tick.CloudTickWorker;
import systems.reformcloud.reformcloud2.node.tick.TickedTaskScheduler;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetIngameMessages;
import systems.reformcloud.reformcloud2.protocol.processor.PacketProcessorManager;
import systems.reformcloud.reformcloud2.protocol.shared.*;
import systems.reformcloud.reformcloud2.shared.command.DefaultCommandManager;
import systems.reformcloud.reformcloud2.shared.event.DefaultEventManager;
import systems.reformcloud.reformcloud2.shared.network.channel.DefaultChannelManager;
import systems.reformcloud.reformcloud2.shared.network.packet.DefaultPacketProvider;
import systems.reformcloud.reformcloud2.shared.network.packet.DefaultQueryManager;
import systems.reformcloud.reformcloud2.shared.network.server.DefaultNetworkServer;
import systems.reformcloud.reformcloud2.shared.registry.service.DefaultServiceRegistry;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public final class NodeExecutor extends ExecutorAPI {

    private static volatile boolean running = true;

    private final DependencyLoader dependencyLoader;

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

    private NodeInformation currentNodeInformation;

    protected NodeExecutor(DependencyLoader dependencyLoader) {
        Conditions.isTrue(new File(".").getAbsolutePath().indexOf('!') == -1, "Cannot run ReformCloud in directory with ! in path.");

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
        return running;
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
        this.nodeInformationProvider = new DefaultNodeNodeInformationProvider(this.currentNodeInformation = new NodeInformation(
            this.nodeConfig.getName(),
            this.nodeConfig.getUniqueID(),
            System.currentTimeMillis(),
            0L,
            this.nodeConfig.getMaxMemory()
        ));

        for (String mainGroupName : this.mainGroupProvider.getMainGroupNames()) {
            System.out.println(LanguageManager.get("loading-main-group", mainGroupName));
        }

        for (String processGroupName : this.processGroupProvider.getProcessGroupNames()) {
            System.out.println(LanguageManager.get("loading-process-group", processGroupName));
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
        System.out.println(LanguageManager.get("runtime-try-reload"));

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

        LanguageLoader.doReload();
        this.nodeConfig = this.nodeExecutorConfig.reload();

        this.currentNodeInformation = new NodeInformation(
            this.currentNodeInformation.getName(),
            this.currentNodeInformation.getNodeUniqueID(),
            this.currentNodeInformation.getStartupTime(),
            this.currentNodeInformation.getUsedMemory(),
            this.nodeConfig.getMaxMemory()
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

        System.out.println(LanguageManager.get("runtime-reload-done", CommonHelper.DECIMAL_FORMAT.format((System.currentTimeMillis() - startTime) / 1000d)));
    }

    public void shutdown() throws Exception {
        // prevent duplicate shutdown requests
        synchronized (this) {
            if (running) {
                running = false;
            } else {
                return;
            }
        }

        System.out.println(LanguageManager.get("application-stop"));

        System.out.println(LanguageManager.get("application-net-server-close"));
        this.networkServer.closeAll();
        System.out.println(LanguageManager.get("application-net-client-close"));
        this.networkClient.disconnect();

        System.out.println(LanguageManager.get("application-stop-task-scheduler"));
        this.taskScheduler.close();
        this.serviceRegistry.getProviderUnchecked(ApplicationLoader.class).disableApplications();

        System.out.println(LanguageManager.get("application-stop-processes"));
        this.processProvider.closeNow(); // important to close the scheduler BEFORE the processes to prevent new processes to start
        System.out.println(LanguageManager.get("application-stop-remove-temp-dir"));
        IOUtils.deleteDirectory(Paths.get("reformcloud/temp"));

        System.out.println(LanguageManager.get("application-stop-finished"));

        this.logger.close();
        this.console.close();

        if (!Thread.currentThread().getName().equals("Shutdown-Hook")) {
            // now call all other shutdown hooks
            System.exit(0);
        }
    }

    private void startNetworkListeners() {
        System.out.println(LanguageManager.get("network-transport-type-choose", NetworkUtil.TRANSPORT_TYPE.getName()));

        for (NetworkAddress networkListener : this.nodeConfig.getNetworkListeners()) {
            if (networkListener.getHost() == null || networkListener.getPort() < 0) {
                System.err.println(LanguageManager.get(
                    "startup-bind-net-listener-fail", networkListener.getHost(), networkListener.getPort()
                ));
                continue;
            }

            this.networkServer.bind(
                networkListener.getHost(),
                networkListener.getPort(),
                NodeServerEndpointChannelReader::new
            );
        }

        for (NetworkAddress clusterNode : this.nodeConfig.getClusterNodes()) {
            if (clusterNode.getHost() == null || clusterNode.getPort() < 0) {
                System.err.println(LanguageManager.get(
                    "startup-connect-node-fail", clusterNode.getHost(), clusterNode.getPort()
                ));
                continue;
            }

            if (this.networkClient.connect(
                clusterNode.getHost(),
                clusterNode.getPort(),
                NodeClientEndpointChannelReader::new
            )) {
                System.out.println(LanguageManager.get(
                    "network-node-connection-to-other-node-success", clusterNode.getHost(), clusterNode.getPort()
                ));
            } else {
                System.out.println(LanguageManager.get(
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
    public NodeInformation updateCurrentNodeInformation() {
        this.currentNodeInformation.update();
        return this.currentNodeInformation;
    }

    @NotNull
    public NodeInformation getCurrentNodeInformation() {
        return this.currentNodeInformation;
    }

    @NotNull
    public ArgumentParser getArgumentParser() {
        return this.argumentParser;
    }

    @NotNull
    public NetworkAddress getAnyAddress() {
        List<NetworkAddress> networkListeners = this.nodeConfig.getNetworkListeners();
        Conditions.isTrue(!networkListeners.isEmpty(), "Try to run cloud system with no network listener configured");
        return networkListeners.size() == 1 ? networkListeners.get(0) : networkListeners.get(new Random().nextInt(networkListeners.size()));
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

    public boolean isOwnIdentity(@NotNull String name) {
        return this.nodeConfig.getName().equals(name);
    }

    private void loadCommands() {
        this.serviceRegistry.getProviderUnchecked(CommandManager.class)
            .registerCommand(new CommandProcess(), "Management of local and remote processes", "p", "process", "sever", "proxy")
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
                neededMemory += processWrapper.getProcessInformation().getProcessDetail().getMaxMemory();
            }
        }

        if (neededMemory >= this.nodeConfig.getMaxMemory()) {
            return false;
        }

        double cpuUsageSystem = CommonHelper.operatingSystemMXBean().getSystemCpuLoad();
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
        this.serviceRegistry.setProvider(ProcessScreenController.class, new DefaultProcessScreenController(), false, true);
    }

    private void registerDefaultPacketProcessors() {
        PacketProcessorManager.getInstance()
            .registerProcessor(new PacketConnectPlayerToServerProcessor(), PacketConnectPlayerToServer.class)
            .registerProcessor(new PacketDisconnectPlayerProcessor(), PacketDisconnectPlayer.class)
            .registerProcessor(new PacketPlayEffectToPlayerProcessor(), PacketPlayEffectToPlayer.class)
            .registerProcessor(new PacketPlaySoundToPlayerProcessor(), PacketPlaySoundToPlayer.class)
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
