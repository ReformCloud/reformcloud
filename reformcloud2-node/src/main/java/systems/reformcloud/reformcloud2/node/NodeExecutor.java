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
package systems.reformcloud.reformcloud2.node;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.command.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.packet.PacketProvider;
import systems.reformcloud.reformcloud2.executor.api.network.packet.query.QueryManager;
import systems.reformcloud.reformcloud2.executor.api.network.server.NetworkServer;
import systems.reformcloud.reformcloud2.executor.api.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.provider.*;
import systems.reformcloud.reformcloud2.executor.api.registry.service.ServiceRegistry;
import systems.reformcloud.reformcloud2.executor.api.utility.NetworkAddress;
import systems.reformcloud.reformcloud2.node.application.DefaultApplicationLoader;
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
import systems.reformcloud.reformcloud2.node.network.NodeEndpointChannelReader;
import systems.reformcloud.reformcloud2.node.network.NodeNetworkClient;
import systems.reformcloud.reformcloud2.node.player.DefaultNodePlayerProvider;
import systems.reformcloud.reformcloud2.node.process.DefaultNodeLocalProcessWrapper;
import systems.reformcloud.reformcloud2.node.process.DefaultNodeProcessProvider;
import systems.reformcloud.reformcloud2.node.process.screen.DefaultProcessScreenController;
import systems.reformcloud.reformcloud2.node.process.screen.ProcessScreenController;
import systems.reformcloud.reformcloud2.node.provider.DefaultNodeNodeInformationProvider;
import systems.reformcloud.reformcloud2.node.tick.CloudTickWorker;
import systems.reformcloud.reformcloud2.node.tick.TickedTaskScheduler;
import systems.reformcloud.reformcloud2.shared.command.DefaultCommandManager;
import systems.reformcloud.reformcloud2.shared.event.DefaultEventManager;
import systems.reformcloud.reformcloud2.shared.network.channel.DefaultChannelManager;
import systems.reformcloud.reformcloud2.shared.network.packet.DefaultPacketProvider;
import systems.reformcloud.reformcloud2.shared.network.packet.DefaultQueryManager;
import systems.reformcloud.reformcloud2.shared.network.server.DefaultNetworkServer;
import systems.reformcloud.reformcloud2.shared.registry.service.DefaultServiceRegistry;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public final class NodeExecutor extends ExecutorAPI {

    private static volatile boolean running = true;

    private final NetworkServer networkServer = new DefaultNetworkServer();
    private final NodeNetworkClient networkClient = new NodeNetworkClient();

    private final NodeExecutorConfig nodeExecutorConfig = new NodeExecutorConfig();
    private NodeConfig nodeConfig;

    private final ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
    private final DefaultNodeProcessProvider processProvider = new DefaultNodeProcessProvider();
    private final PlayerProvider playerProvider = new DefaultNodePlayerProvider();
    private final ChannelMessageProvider channelMessageProvider = new DefaultNodeChannelMessageProvider();
    private final DefaultNodeMainGroupProvider mainGroupProvider;
    private final DefaultNodeProcessGroupProvider processGroupProvider;
    private NodeInformationProvider nodeInformationProvider;

    private final TickedTaskScheduler taskScheduler = new TickedTaskScheduler();
    private final CloudTickWorker cloudTickWorker = new CloudTickWorker(this.taskScheduler);

    private DefaultNodeConsole console;
    private CloudLogger logger;

    private NodeInformation currentNodeInformation;

    NodeExecutor() {
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

        this.registerDefaultServices();

        this.mainGroupProvider = new DefaultNodeMainGroupProvider(System.getProperty("systems.reformcloud.main-group-dir", "reformcloud/groups/main"));
        this.processGroupProvider = new DefaultNodeProcessGroupProvider(System.getProperty("systems.reformcloud.sub-group-dir", "reformcloud/groups/sub"));
        this.bootstrap();
    }

    @NotNull
    public static NodeExecutor getInstance() {
        return (NodeExecutor) ExecutorAPI.getInstance();
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
    public boolean isReady() {
        return NodeNetworkClient.CONNECTIONS.isEmpty();
    }

    @NotNull
    public TickedTaskScheduler getTaskScheduler() {
        return this.taskScheduler;
    }

    private void bootstrap() {
        this.console = new DefaultNodeConsole();
        this.logger = new CloudLogger(this.console.getLineReader());

        this.nodeExecutorConfig.init();
        this.nodeConfig = this.nodeExecutorConfig.getNodeConfig();
        this.nodeInformationProvider = new DefaultNodeNodeInformationProvider(this.currentNodeInformation = new NodeInformation(
                this.nodeConfig.getName(),
                this.nodeConfig.getUniqueID(),
                System.currentTimeMillis(),
                0L,
                this.nodeConfig.getMaxMemory(),
                Collections.emptyList()
        ));

        TemplateBackendManager.registerDefaults();

        this.loadPacketHandlers();

        this.nodeConfig.getNetworkListeners().forEach(e -> this.networkServer.bind(
                e.getHost(),
                e.getPort(),
                () -> new NodeEndpointChannelReader(),
                new NodeChallengeAuthHandler(new SharedChallengeProvider(this.nodeExecutorConfig.getConnectionKey()), new NodeNetworkSuccessHandler())
        ));

        this.applicationLoader.loadApplications();

        if (this.nodeConfig.getClusterNodes().isEmpty()) {
            System.out.println(LanguageManager.get("network-node-no-other-nodes-defined"));
        } else {
            if (this.nodeExecutorConfig.getConnectionKey() == null) {
                System.out.println(LanguageManager.get("network-node-try-connect-with-no-key"));
            } else {
                this.nodeConfig.getClusterNodes().forEach(e -> {
                    if (this.networkClient.connect(
                            e.getHost(),
                            e.getPort(),
                            () -> new NodeEndpointChannelReader(),
                            new ClientChallengeAuthHandler(
                                    NodeExecutor.getInstance().getNodeExecutorConfig().getConnectionKey(),
                                    NodeExecutor.getInstance().getNodeConfig().getName(),
                                    () -> new JsonConfiguration().add("info", NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode()),
                                    context -> NodeNetworkClient.CONNECTIONS.remove(((InetSocketAddress) context.channel().remoteAddress()).getAddress().getHostAddress())
                            ))
                    ) {
                        System.out.println(LanguageManager.get(
                                "network-node-connection-to-other-node-success", e.getHost(), e.getPort()
                        ));
                        this.clusterSyncManager.getWaitingConnections().add(e.getHost());
                    } else {
                        System.out.println(LanguageManager.get(
                                "network-node-connection-to-other-node-not-successful", e.getHost(), e.getPort()
                        ));
                    }
                });
            }
        }

        this.loadCommands();
        this.cloudTickWorker.startTick();
    }

    public NodeConfig getNodeConfig() {
        return this.nodeConfig;
    }

    public NodeExecutorConfig getNodeExecutorConfig() {
        return this.nodeExecutorConfig;
    }

    private void loadCommands() {
        this.serviceRegistry.getProviderUnchecked(CommandManager.class)
                .registerCommand(new CommandProcess(), "p", "process", "sever", "proxy")
                .registerCommand(new CommandCluster(), "clu", "cluster", "c")
                .registerCommand(new CommandPlayers(), "pl", "players")
                .registerCommand(new CommandGroup(), "g", "group", "groups")
                .registerCommand(new CommandCreate(), "create")
                .registerCommand(new CommandLaunch(), "launch", "l")
                .registerCommand(new CommandStop(), "stop", "exit", "shutdown")
                .registerCommand(new CommandLog(), "log")
                .registerCommand(new CommandReload(), "reload", "rl")
                .registerCommand(new CommandClear(), "clear", "cls")
                .registerCommand(new CommandHelp(), "help", "ask", "?");
    }

    private void loadPacketHandlers() {
        PacketProvider packetProvider = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class);
    }

    public CloudTickWorker getCloudTickWorker() {
        return this.cloudTickWorker;
    }

    public boolean canStartProcesses(int neededMemory) {
        AtomicLong atomicLong = new AtomicLong(neededMemory);
        for (DefaultNodeLocalProcessWrapper processWrapper : this.processProvider.getProcessWrappers()) {
            if (processWrapper.isStarted()) {
                atomicLong.addAndGet(processWrapper.getProcessInformation().getProcessDetail().getMaxMemory());
            }
        }

        if (atomicLong.get() > this.nodeConfig.getMaxMemory()) {
            return false;
        }

        double cpuUsageSystem = CommonHelper.operatingSystemMXBean().getSystemCpuLoad();
        return cpuUsageSystem <= 0 || cpuUsageSystem * 100 < this.nodeConfig.getMaxSystemCpuUsage();
    }

    public static boolean isRunning() {
        return running;
    }

    public DefaultNodeConsole getConsole() {
        return this.console;
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

    public @NotNull NodeInformation updateCurrentNodeInformation() {
        this.currentNodeInformation.update();
        return this.currentNodeInformation;
    }

    public @NotNull NodeInformation getCurrentNodeInformation() {
        return this.currentNodeInformation;
    }

    public @NotNull NetworkAddress getAnyAddress() {
        List<NetworkAddress> networkListeners = this.nodeConfig.getNetworkListeners();
        Conditions.isTrue(!networkListeners.isEmpty(), "Try to run cloud system with no network listener configured");
        return networkListeners.size() == 1 ? networkListeners.get(0) : networkListeners.get(new Random().nextInt(networkListeners.size()));
    }

    public @NotNull String getSelfName() {
        return this.nodeConfig.getName();
    }

    public boolean isOwnIdentity(@NotNull String name) {
        return this.nodeConfig.getName().equals(name);
    }
}
