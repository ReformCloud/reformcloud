package systems.reformcloud.reformcloud2.executor.node;

import io.netty.channel.ChannelHandlerContext;
import org.reflections.Reflections;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.CommandClear;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.CommandHelp;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.CommandReload;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.CommandStop;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.dump.CommandDump;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.manager.DefaultCommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.file.FileDatabase;
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.h2.H2Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mongo.MongoDatabase;
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mysql.MySQLDatabase;
import systems.reformcloud.reformcloud2.executor.api.common.database.config.DatabaseConfig;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupEnvironment;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.ColouredLoggerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.logger.other.DefaultLoggerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.NetworkType;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.defaults.DefaultAuth;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.defaults.DefaultServerAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.defaults.DefaultPacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.DefaultNetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.NetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.basic.DefaultWebServerAuth;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.http.server.DefaultWebServer;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.http.server.WebServer;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults.DefaultRequestListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.user.WebUser;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.Double;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.node.Node;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterSyncManager;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.SyncAction;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.node.cluster.DefaultClusterManager;
import systems.reformcloud.reformcloud2.executor.node.cluster.DefaultNodeInternalCluster;
import systems.reformcloud.reformcloud2.executor.node.cluster.sync.DefaultClusterSyncManager;
import systems.reformcloud.reformcloud2.executor.node.commands.CommandReformCloud;
import systems.reformcloud.reformcloud2.executor.node.config.NodeConfig;
import systems.reformcloud.reformcloud2.executor.node.config.NodeExecutorConfig;
import systems.reformcloud.reformcloud2.executor.node.dump.NodeDumpUtil;
import systems.reformcloud.reformcloud2.executor.node.network.DefaultNodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.node.network.client.NodeNetworkClient;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutExecuteCommand;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.api.NodeAPIAction;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.api.NodePluginAction;
import systems.reformcloud.reformcloud2.executor.node.process.LocalAutoStartupHandler;
import systems.reformcloud.reformcloud2.executor.node.process.LocalNodeProcessManager;
import systems.reformcloud.reformcloud2.executor.node.process.log.LogLineReader;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;
import systems.reformcloud.reformcloud2.executor.node.process.startup.LocalProcessQueue;
import systems.reformcloud.reformcloud2.executor.node.process.watchdog.WatchdogThread;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class NodeExecutor extends Node {

    private static NodeExecutor instance;

    private static volatile boolean running = false;

    private final CommandManager commandManager = new DefaultCommandManager();

    private final CommandSource console = new ConsoleCommandSource(commandManager);

    private final ApplicationLoader applicationLoader = new DefaultApplicationLoader();

    private final NetworkServer networkServer = new DefaultNetworkServer();

    private final WebServer webServer = new DefaultWebServer();

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NodeExecutorConfig nodeExecutorConfig = new NodeExecutorConfig();

    private final DatabaseConfig databaseConfig = new DatabaseConfig();

    private final LocalAutoStartupHandler localAutoStartupHandler = new LocalAutoStartupHandler();

    private final EventManager eventManager = new DefaultEventManager();

    private LocalProcessQueue localProcessQueue;

    private LogLineReader logLineReader;

    private WatchdogThread watchdogThread;

    private NetworkClient networkClient;

    private NodeConfig nodeConfig;

    private Database database;

    private NodeNetworkManager nodeNetworkManager;

    private ClusterSyncManager clusterSyncManager;

    private LoggerBase loggerBase;

    private RequestListenerHandler requestListenerHandler;

    NodeExecutor() {
        Node.setInstance(this);
        ExecutorAPI.setInstance(this);
        super.type = ExecutorType.NODE;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                shutdown();
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }, "Shutdown-Hook"));

        bootstrap();
    }

    @Override
    protected void bootstrap() {
        final long current = System.currentTimeMillis();
        instance = this;

        try {
            if (Boolean.getBoolean("reformcloud2.disable.colours")) {
                this.loggerBase = new DefaultLoggerHandler();
            } else {
                this.loggerBase = new ColouredLoggerHandler();
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        SystemHelper.deleteDirectory(Paths.get("reformcloud/temp"));

        this.nodeExecutorConfig.init();
        this.nodeConfig = this.nodeExecutorConfig.getNodeConfig();

        this.nodeNetworkManager = new DefaultNodeNetworkManager(
                new LocalNodeProcessManager(),
                new DefaultNodeInternalCluster(new DefaultClusterManager(), nodeExecutorConfig.getSelf(), packetHandler)
        );
        this.nodeNetworkManager.getCluster().getClusterManager().init();

        this.requestListenerHandler = new DefaultRequestListenerHandler(new DefaultWebServerAuth(this));

        final NodeNetworkClient nodeNetworkClient = new NodeNetworkClient();
        this.networkClient = nodeNetworkClient;
        this.clusterSyncManager = new DefaultClusterSyncManager(nodeNetworkClient);

        this.clusterSyncManager.getProcessGroups().addAll(nodeExecutorConfig.getProcessGroups());
        this.clusterSyncManager.getMainGroups().addAll(nodeExecutorConfig.getMainGroups());

        this.applicationLoader.detectApplications();
        this.applicationLoader.installApplications();

        TemplateBackendManager.registerDefaults();

        this.logLineReader = new LogLineReader();
        this.watchdogThread = new WatchdogThread();

        this.loadPacketHandlers();

        this.nodeConfig.getNetworkListener().forEach(e -> e.forEach((ip, port) -> {
            this.networkServer.bind(ip, port, new DefaultServerAuthHandler(
                    packetHandler,
                    packetSender -> this.nodeNetworkManager.getCluster().getClusterManager().handleNodeDisconnect(
                                this.nodeNetworkManager.getCluster(),
                                packetSender.getName()
                    ),
                    packet -> {
                        DefaultAuth auth = packet.content().get("auth", Auth.TYPE);
                        if (auth == null) {
                            System.out.println(LanguageManager.get("network-channel-auth-failed", "unknown"));
                            return new Double<>("unknown", false);
                        }

                        if (auth.type().equals(NetworkType.CLIENT)) {
                            return new Double<>(auth.getName(), false);
                        }

                        AtomicReference<Double<String, Boolean>> result = new AtomicReference<>();
                        if (auth.type().equals(NetworkType.NODE)) {
                            NodeInformation nodeInformation = auth.extra().get("info", NodeInformation.TYPE);
                            if (nodeInformation == null) {
                                return new Double<>(auth.getName(), false);
                            }

                            if (nodeExecutorConfig.getConnectionKey() == null || !auth.key().equals(nodeExecutorConfig.getConnectionKey())) {
                                System.out.println(LanguageManager.get("network-channel-auth-failed", auth.getName()));
                                return new Double<>(auth.getName(), false);
                            }

                            this.nodeNetworkManager.getCluster().getClusterManager().handleConnect(
                                    this.nodeNetworkManager.getCluster(),
                                    nodeInformation,
                                    (aBoolean, s) -> result.set(new Double<>(nodeInformation.getName(), aBoolean))
                            );
                        } else if (auth.type().equals(NetworkType.PROCESS)) {
                            ProcessInformation information = nodeNetworkManager.getNodeProcessHelper()
                                    .getLocalCloudProcess(auth.getName());
                            if (information == null) {
                                return new Double<>(auth.getName(), false);
                            }

                            if (!auth.key().equals(nodeExecutorConfig.getCurrentNodeConnectionKey())) {
                                System.out.println(LanguageManager.get("network-channel-auth-failed", auth.getName()));
                                return new Double<>(auth.getName(), false);
                            }

                            result.set(new Double<>(auth.getName(), true));

                            information.getNetworkInfo().setConnected(true);
                            information.setProcessState(ProcessState.READY);
                            nodeNetworkManager.getNodeProcessHelper().update(information);
                            nodeNetworkManager.getNodeProcessHelper().handleProcessConnection(information);
                            System.out.println(LanguageManager.get("process-connected", auth.getName(), auth.parent()));
                        }

                        System.out.println(LanguageManager.get("network-channel-auth-success", auth.getName(), auth.parent()));
                        return result.get();
                    }
            ) {
                @Nonnull
                @Override
                public BiFunction<String, ChannelHandlerContext, PacketSender> onSuccess() {
                    return (s, context) -> {
                        ProcessInformation process = nodeNetworkManager.getNodeProcessHelper()
                                .getLocalCloudProcess(s);

                        JsonConfiguration result = new JsonConfiguration().add("access", true);
                        if (process == null) {
                            // Node
                            result.add("name", nodeExecutorConfig.getSelf().getName());
                        }
                        context.channel().writeAndFlush(new DefaultPacket(-511, result));

                        PacketSender sender = new DefaultPacketSender(context.channel());
                        sender.setName(s);
                        clusterSyncManager.getWaitingConnections().remove(sender.getAddress());

                        Links.filterToReference(nodeConfig.getOtherNodes(), e -> e.keySet().stream().anyMatch(c -> c.equals(
                                sender.getAddress()
                        ))).ifPresent(e -> e.forEach((key, value) -> networkClient.connect(
                                key, value, new DefaultAuth(
                                        nodeExecutorConfig.getConnectionKey(),
                                        null,
                                        NetworkType.NODE,
                                        nodeNetworkManager.getCluster().getSelfNode().getName(),
                                        new JsonConfiguration().add("info", nodeNetworkManager.getCluster().getSelfNode())
                                ), createReader(sender1 -> {
                                    NodeInformation information = nodeNetworkManager.getCluster().getNode(sender1.getName());
                                    if (information == null) {
                                        nodeNetworkManager.getNodeProcessHelper().handleProcessDisconnect(sender1.getName());
                                    } else {
                                        nodeNetworkManager.getCluster().getClusterManager().handleNodeDisconnect(
                                                nodeNetworkManager.getCluster(),
                                                sender1.getName()
                                        );
                                    }

                                    NodeNetworkClient.CONNECTIONS.remove(sender1.getAddress());
                                }))));
                        sync(sender);
                        return sender;
                    };
                }

                @Nonnull
                @Override
                public Consumer<ChannelHandlerContext> onAuthFailure() {
                    return context -> {
                        String host = ((InetSocketAddress) context.channel().remoteAddress()).getAddress().getHostAddress();
                        clusterSyncManager.getWaitingConnections().remove(host);
                        context.channel().writeAndFlush(new DefaultPacket(-511, new JsonConfiguration().add("access", false))).syncUninterruptibly().channel().close();
                    };
                }
            });
        }));

        this.nodeConfig.getHttpNetworkListener().forEach(map -> map.forEach((host, port) -> {
                    this.webServer.add(host, port, this.requestListenerHandler);
                })
        );

        this.applicationLoader.loadApplications();

        databaseConfig.load();
        switch (databaseConfig.getType()) {
            case FILE: {
                this.database = new FileDatabase();
                this.databaseConfig.connect(this.database);
                break;
            }

            case H2: {
                this.database = new H2Database();
                this.databaseConfig.connect(this.database);
                break;
            }

            case MONGO: {
                this.database = new MongoDatabase();
                this.databaseConfig.connect(this.database);
                break;
            }

            case MYSQL: {
                this.database = new MySQLDatabase();
                this.databaseConfig.connect(this.database);
                break;
            }
        }

        createDatabase("internal_users");
        if (this.nodeExecutorConfig.isFirstStartup()) {
            final String token = StringUtil.generateString(2);
            WebUser webUser = new WebUser("admin", token, Collections.singletonList("*"));
            insert("internal_users", webUser.getName(), "", new JsonConfiguration().add("user", webUser));

            System.out.println(LanguageManager.get("setup-created-default-user", webUser.getName(), token));
        }

        if (this.nodeConfig.getOtherNodes().isEmpty()) {
            System.out.println(LanguageManager.get("network-node-no-other-nodes-defined"));
        } else {
            if (this.nodeExecutorConfig.getConnectionKey() == null) {
                System.out.println(LanguageManager.get("network-node-try-connect-with-no-key"));
            } else {
                this.nodeConfig.getOtherNodes().forEach(e -> e.forEach((ip, port) -> {
                    if (this.networkClient.connect(ip, port, new DefaultAuth(
                            this.nodeExecutorConfig.getConnectionKey(),
                            null,
                            NetworkType.NODE,
                            this.nodeNetworkManager.getCluster().getSelfNode().getName(),
                            new JsonConfiguration().add("info", this.nodeNetworkManager.getCluster().getSelfNode())
                    ), createReader(sender -> {
                        NodeInformation information = this.nodeNetworkManager.getCluster().getNode(sender.getName());
                        if (information == null) {
                            this.nodeNetworkManager.getNodeProcessHelper().handleProcessDisconnect(sender.getName());
                        } else {
                            this.nodeNetworkManager.getCluster().getClusterManager().handleNodeDisconnect(
                                    this.nodeNetworkManager.getCluster(),
                                    sender.getName()
                            );
                        }

                        NodeNetworkClient.CONNECTIONS.remove(sender.getAddress());
                    }))) {
                        System.out.println(LanguageManager.get(
                                "network-node-connection-to-other-node-success", ip, Integer.toString(port)
                        ));
                        this.clusterSyncManager.getWaitingConnections().add(ip);
                    } else {
                        System.out.println(LanguageManager.get(
                                "network-node-connection-to-other-node-not-successful", ip, Integer.toString(port)
                        ));
                    }
                }));
            }
        }

        this.localProcessQueue = new LocalProcessQueue();
        this.localAutoStartupHandler.update();
        this.localAutoStartupHandler.doStart();
        this.applicationLoader.enableApplications();

        this.loadCommands();
        this.sendGroups();

        running = true;
        System.out.println(LanguageManager.get("startup-done", Long.toString(System.currentTimeMillis() - current)));

        this.awaitConnectionsAndUpdate();
        this.runConsole();
    }

    public static NodeExecutor getInstance() {
        return instance;
    }

    public LoggerBase getLoggerBase() {
        return loggerBase;
    }

    public NodeNetworkManager getNodeNetworkManager() {
        return nodeNetworkManager;
    }

    public ClusterSyncManager getClusterSyncManager() {
        return clusterSyncManager;
    }

    public NodeConfig getNodeConfig() {
        return nodeConfig;
    }

    public NodeExecutorConfig getNodeExecutorConfig() {
        return nodeExecutorConfig;
    }

    public LocalAutoStartupHandler getLocalAutoStartupHandler() {
        return localAutoStartupHandler;
    }

    @Nonnull
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public boolean isReady() {
        return this.clusterSyncManager.isConnectedAndSyncWithCluster();
    }

    public Double<String, Integer> getConnectHost() {
        if (this.nodeConfig.getNetworkListener().size() == 1) {
            Map.Entry<String, Integer> result = nodeConfig.getNetworkListener().get(0).entrySet().iterator().next();
            return new Double<>(result.getKey(), result.getValue());
        }

        Map.Entry<String, Integer> result = nodeConfig.getNetworkListener().get(new Random().nextInt(nodeConfig.getNetworkListener().size())).entrySet().iterator().next();
        return new Double<>(result.getKey(), result.getValue());
    }

    @Override
    public void shutdown() throws Exception {
        if (running) {
            running = false;
        } else {
            return;
        }

        System.out.println(LanguageManager.get("runtime-try-shutdown"));

        this.clusterSyncManager.disconnectFromCluster();

        this.networkServer.closeAll();
        this.networkClient.disconnect();
        this.webServer.close();

        this.watchdogThread.interrupt();
        this.logLineReader.interrupt();
        this.localProcessQueue.interrupt();
        this.localAutoStartupHandler.interrupt();

        LocalProcessManager.close();

        this.database.disconnect();

        this.applicationLoader.disableApplications();
        this.loggerBase.close();

        SystemHelper.deleteDirectory(Paths.get("reformcloud/temp"));
    }

    private void runConsole() {
        String line;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                loggerBase.getConsoleReader().setPrompt("");
                loggerBase.getConsoleReader().resetPromptLine("", "", 0);

                while ((line = loggerBase.readLine()) != null && !line.trim().isEmpty() && running) {
                    loggerBase.getConsoleReader().setPrompt("");
                    commandManager.dispatchCommand(console, AllowedCommandSources.ALL, line, System.out::println);
                }
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    @Override
    public NetworkServer getNetworkServer() {
        return networkServer;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Nonnull
    @Override
    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    @Nonnull
    @Override
    public Task<Boolean> loadApplicationAsync(@Nonnull InstallableApplication application) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(applicationLoader.doSpecificApplicationInstall(application)));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> unloadApplicationAsync(@Nonnull LoadedApplication application) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(applicationLoader.doSpecificApplicationUninstall(application)));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> unloadApplicationAsync(@Nonnull String application) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(applicationLoader.doSpecificApplicationUninstall(application)));
        return task;
    }

    @Nonnull
    @Override
    public Task<LoadedApplication> getApplicationAsync(@Nonnull String name) {
        Task<LoadedApplication> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(applicationLoader.getApplication(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<LoadedApplication>> getApplicationsAsync() {
        Task<List<LoadedApplication>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(applicationLoader.getApplications()));
        return task;
    }

    @Override
    public boolean loadApplication(@Nonnull InstallableApplication application) {
        return applicationLoader.doSpecificApplicationInstall(application);
    }

    @Override
    public boolean unloadApplication(@Nonnull LoadedApplication application) {
        return applicationLoader.doSpecificApplicationUninstall(application);
    }

    @Override
    public boolean unloadApplication(@Nonnull String application) {
        return applicationLoader.doSpecificApplicationUninstall(application);
    }

    @Override
    public LoadedApplication getApplication(@Nonnull String name) {
        return applicationLoader.getApplication(name);
    }

    @Override
    public List<LoadedApplication> getApplications() {
        return applicationLoader.getApplications();
    }

    @Nonnull
    @Override
    public Task<Boolean> isClientConnectedAsync(String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(this.nodeNetworkManager.getCluster().getNode(name) != null));
        return task;
    }

    @Nonnull
    @Override
    public Task<String> getClientStartHostAsync(String name) {
        return null;
    }

    @Nonnull
    @Override
    public Task<Integer> getMaxMemoryAsync(String name) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete((int) this.nodeNetworkManager.getCluster().getNode(name).getMaxMemory()));
        return task;
    }

    @Nonnull
    @Override
    public Task<Integer> getMaxProcessesAsync(String name) {
        return null;
    }

    @Nonnull
    @Override
    public Task<ClientRuntimeInformation> getClientInformationAsync(String name) {
        return null;
    }

    @Override
    public boolean isClientConnected(@Nonnull String name) {
        return this.nodeNetworkManager.getCluster().getNode(name) != null;
    }

    @Override
    public String getClientStartHost(@Nonnull String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxMemory(@Nonnull String name) {
        if (!isClientConnected(name)) {
            return -1;
        }

        return (int) nodeNetworkManager.getCluster().getNode(name).getMaxMemory();
    }

    @Override
    public int getMaxProcesses(@Nonnull String name) {
        throw new UnsupportedOperationException("There is no config option for max node processes");
    }

    @Override
    public ClientRuntimeInformation getClientInformation(@Nonnull String name) {
        return null;
    }

    @Nonnull
    @Override
    public Task<Void> sendColouredLineAsync(@Nonnull String line) throws IllegalAccessException {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            System.out.println(line);
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> sendRawLineAsync(@Nonnull String line) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            System.out.println(line);
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<String> dispatchCommandAndGetResultAsync(@Nonnull String commandLine) {
        Task<String> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.commandManager.dispatchCommand(console, AllowedCommandSources.ALL, commandLine, task::complete));
        return task;
    }

    @Nonnull
    @Override
    public Task<Command> getCommandAsync(@Nonnull String name) {
        Task<Command> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(commandManager.getCommand(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> isCommandRegisteredAsync(@Nonnull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(commandManager.getCommand(name) != null));
        return task;
    }

    @Override
    public void sendColouredLine(@Nonnull String line) throws IllegalAccessException {
        sendColouredLineAsync(line).awaitUninterruptedly();
    }

    @Override
    public void sendRawLine(@Nonnull String line) {
        sendRawLineAsync(line).awaitUninterruptedly();
    }

    @Override
    public String dispatchCommandAndGetResult(@Nonnull String commandLine) {
        return dispatchCommandAndGetResultAsync(commandLine).getUninterruptedly();
    }

    @Override
    public Command getCommand(@Nonnull String name) {
        return getCommandAsync(name).getUninterruptedly();
    }

    @Override
    public boolean isCommandRegistered(@Nonnull String name) {
        return isCommandRegisteredAsync(name).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<JsonConfiguration> findAsync(@Nonnull String table, @Nonnull String key, String identifier) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            JsonConfiguration jsonConfiguration = this.database.createForTable(table).find(key).getUninterruptedly();
            if (jsonConfiguration == null) {
                jsonConfiguration = this.database.createForTable(table).findIfAbsent(identifier).getUninterruptedly();
            }

            task.complete(jsonConfiguration);
        });
        return task;
    }

    @Nonnull
    @Override
    public <T> Task<T> findAsync(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull Function<JsonConfiguration, T> function) {
        Task<T> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(function.apply(find(table, key, identifier))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> insertAsync(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull JsonConfiguration data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.database.createForTable(table).insert(key, identifier, data);
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> updateAsync(@Nonnull String table, @Nonnull String key, @Nonnull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(this.database.createForTable(table).update(key, newData).getUninterruptedly()));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> updateIfAbsentAsync(@Nonnull String table, @Nonnull String identifier, @Nonnull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(this.database.createForTable(table).updateIfAbsent(identifier, newData).getUninterruptedly()));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> removeAsync(@Nonnull String table, @Nonnull String key) {
        return database.createForTable(table).remove(key);
    }

    @Nonnull
    @Override
    public Task<Void> removeIfAbsentAsync(@Nonnull String table, @Nonnull String identifier) {
        return database.createForTable(table).removeIfAbsent(identifier);
    }

    @Nonnull
    @Override
    public Task<Boolean> createDatabaseAsync(@Nonnull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(database.createDatabase(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> deleteDatabaseAsync(@Nonnull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(database.deleteDatabase(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> containsAsync(@Nonnull String table, @Nonnull String key) {
        return database.createForTable(table).contains(key);
    }

    @Nonnull
    @Override
    public Task<Integer> sizeAsync(@Nonnull String table) {
        return database.createForTable(table).size();
    }

    @Override
    public JsonConfiguration find(@Nonnull String table, @Nonnull String key, String identifier) {
        return findAsync(table, key, identifier).getUninterruptedly();
    }

    @Override
    public <T> T find(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull Function<JsonConfiguration, T> function) {
        return findAsync(table, key, identifier, function).getUninterruptedly();
    }

    @Override
    public void insert(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull JsonConfiguration data) {
        insertAsync(table, key, identifier, data).awaitUninterruptedly();
    }

    @Override
    public boolean update(@Nonnull String table, @Nonnull String key, @Nonnull JsonConfiguration newData) {
        return updateAsync(table, key, newData).getUninterruptedly();
    }

    @Override
    public boolean updateIfAbsent(@Nonnull String table, @Nonnull String identifier, @Nonnull JsonConfiguration newData) {
        return updateIfAbsentAsync(table, identifier, newData).getUninterruptedly();
    }

    @Override
    public void remove(@Nonnull String table, @Nonnull String key) {
        removeAsync(table, key).awaitUninterruptedly();
    }

    @Override
    public void removeIfAbsent(@Nonnull String table, @Nonnull String identifier) {
        removeIfAbsentAsync(table, identifier).awaitUninterruptedly();
    }

    @Override
    public boolean createDatabase(@Nonnull String name) {
        return createDatabaseAsync(name).getUninterruptedly();
    }

    @Override
    public boolean deleteDatabase(@Nonnull String name) {
        return deleteDatabaseAsync(name).getUninterruptedly();
    }

    @Override
    public boolean contains(@Nonnull String table, @Nonnull String key) {
        return containsAsync(table, key).getUninterruptedly();
    }

    @Override
    public int size(@Nonnull String table) {
        return sizeAsync(table).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<MainGroup> createMainGroupAsync(@Nonnull String name) {
        return createMainGroupAsync(name, new ArrayList<>());
    }

    @Nonnull
    @Override
    public Task<MainGroup> createMainGroupAsync(@Nonnull String name, @Nonnull List<String> subgroups) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            if (this.clusterSyncManager.existsMainGroup(name)) {
                task.complete(null);
                return;
            }

            MainGroup mainGroup = new MainGroup(name, subgroups);
            this.clusterSyncManager.syncMainGroupCreate(mainGroup);
            task.complete(mainGroup);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@Nonnull String name) {
        return createProcessGroupAsync(name, new ArrayList<>());
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@Nonnull String name, @Nonnull List<Template> templates) {
        return createProcessGroupAsync(name, templates, new StartupConfiguration(
                -1, 1, 1, 41000, StartupEnvironment.JAVA_RUNTIME, true, new ArrayList<>()
        ));
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@Nonnull String name, @Nonnull List<Template> templates, @Nonnull StartupConfiguration startupConfiguration) {
        return createProcessGroupAsync(name, templates, startupConfiguration, new PlayerAccessConfiguration(
                false, "reformcloud.join.maintenance", false,
                null, true, true, true, 50
        ));
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@Nonnull String name, @Nonnull List<Template> templates, @Nonnull StartupConfiguration startupConfiguration, @Nonnull PlayerAccessConfiguration playerAccessConfiguration) {
        return createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration, false);
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@Nonnull String name, @Nonnull List<Template> templates, @Nonnull StartupConfiguration startupConfiguration, @Nonnull PlayerAccessConfiguration playerAccessConfiguration, boolean staticGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessGroup processGroup = new ProcessGroup(
                    name,
                    true,
                    startupConfiguration,
                    templates,
                    playerAccessConfiguration,
                    staticGroup
            );
            task.complete(createProcessGroupAsync(processGroup).getUninterruptedly());
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> createProcessGroupAsync(@Nonnull ProcessGroup processGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            if (clusterSyncManager.existsProcessGroup(processGroup.getName())) {
                task.complete(null);
                return;
            }

            this.clusterSyncManager.syncProcessGroupCreate(processGroup);
            task.complete(processGroup);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<MainGroup> updateMainGroupAsync(@Nonnull MainGroup mainGroup) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.clusterSyncManager.syncMainGroupUpdate(mainGroup);
            task.complete(mainGroup);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> updateProcessGroupAsync(@Nonnull ProcessGroup processGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.clusterSyncManager.syncProcessGroupUpdate(processGroup);
            task.complete(processGroup);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<MainGroup> getMainGroupAsync(@Nonnull String name) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.filterToReference(clusterSyncManager.getMainGroups(), e -> e.getName().equals(name)).orNothing()));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> getProcessGroupAsync(@Nonnull String name) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.filterToReference(clusterSyncManager.getProcessGroups(), e -> e.getName().equals(name)).orNothing()));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> deleteMainGroupAsync(@Nonnull String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.clusterSyncManager.syncMainGroupDelete(name);
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> deleteProcessGroupAsync(@Nonnull String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.clusterSyncManager.syncProcessGroupDelete(name);
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<List<MainGroup>> getMainGroupsAsync() {
        Task<List<MainGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.newList(clusterSyncManager.getMainGroups())));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<ProcessGroup>> getProcessGroupsAsync() {
        Task<List<ProcessGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.newList(clusterSyncManager.getProcessGroups())));
        return task;
    }

    @Nonnull
    @Override
    public MainGroup createMainGroup(@Nonnull String name) {
        return createMainGroupAsync(name).getUninterruptedly();
    }

    @Nonnull
    @Override
    public MainGroup createMainGroup(@Nonnull String name, @Nonnull List<String> subgroups) {
        return createMainGroupAsync(name, subgroups).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup createProcessGroup(@Nonnull String name) {
        return createProcessGroupAsync(name).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup createProcessGroup(@Nonnull String name, @Nonnull List<Template> templates) {
        return createProcessGroupAsync(name, templates).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup createProcessGroup(@Nonnull String name, @Nonnull List<Template> templates, @Nonnull StartupConfiguration startupConfiguration) {
        return createProcessGroupAsync(name, templates, startupConfiguration).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup createProcessGroup(@Nonnull String name, @Nonnull List<Template> templates, @Nonnull StartupConfiguration startupConfiguration, @Nonnull PlayerAccessConfiguration playerAccessConfiguration) {
        return createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup createProcessGroup(@Nonnull String name, @Nonnull List<Template> templates, @Nonnull StartupConfiguration startupConfiguration, @Nonnull PlayerAccessConfiguration playerAccessConfiguration, boolean staticGroup) {
        return createProcessGroupAsync(name, templates, startupConfiguration, playerAccessConfiguration, staticGroup).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup createProcessGroup(@Nonnull ProcessGroup processGroup) {
        return createProcessGroupAsync(processGroup).getUninterruptedly();
    }

    @Nonnull
    @Override
    public MainGroup updateMainGroup(@Nonnull MainGroup mainGroup) {
        return updateMainGroupAsync(mainGroup).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessGroup updateProcessGroup(@Nonnull ProcessGroup processGroup) {
        return updateProcessGroupAsync(processGroup).getUninterruptedly();
    }

    @Nullable
    @Override
    public MainGroup getMainGroup(@Nonnull String name) {
        return getMainGroupAsync(name).getUninterruptedly();
    }

    @Nullable
    @Override
    public ProcessGroup getProcessGroup(@Nonnull String name) {
        return getProcessGroupAsync(name).getUninterruptedly();
    }

    @Override
    public void deleteMainGroup(@Nonnull String name) {
        deleteMainGroupAsync(name).awaitUninterruptedly();
    }

    @Override
    public void deleteProcessGroup(@Nonnull String name) {
        deleteProcessGroupAsync(name).awaitUninterruptedly();
    }

    @Nonnull
    @Override
    public List<MainGroup> getMainGroups() {
        return getMainGroupsAsync().getUninterruptedly();
    }

    @Nonnull
    @Override
    public List<ProcessGroup> getProcessGroups() {
        return getProcessGroupsAsync().getUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<Void> sendMessageAsync(@Nonnull UUID player, @Nonnull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnProxy(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getParent())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.SEND_MESSAGE, Arrays.asList(player, message)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.SEND_MESSAGE, Arrays.asList(player, message)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> kickPlayerAsync(@Nonnull UUID player, @Nonnull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnProxy(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getParent())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.KICK_PLAYER, Arrays.asList(player, message)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.KICK_PLAYER, Arrays.asList(player, message)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> kickPlayerFromServerAsync(@Nonnull UUID player, @Nonnull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getParent())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.KICK_PLAYER, Arrays.asList(player, message)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.KICK_PLAYER, Arrays.asList(player, message)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> playSoundAsync(@Nonnull UUID player, @Nonnull String sound, float f1, float f2) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getParent())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.PLAY_SOUND, Arrays.asList(player, sound, f1, f2)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.PLAY_SOUND, Arrays.asList(player, sound, f1, f2)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> sendTitleAsync(@Nonnull UUID player, @Nonnull String title, @Nonnull String subTitle, int fadeIn, int stay, int fadeOut) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnProxy(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getParent())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.SEND_TITLE, Arrays.asList(player, title, subTitle, fadeIn, stay, fadeOut)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.SEND_TITLE, Arrays.asList(player, title, subTitle, fadeIn, stay, fadeOut)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> playEffectAsync(@Nonnull UUID player, @Nonnull String entityEffect) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getParent())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.PLAY_ENTITY_EFFECT, Arrays.asList(player, entityEffect)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.PLAY_ENTITY_EFFECT, Arrays.asList(player, entityEffect)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public <T> Task<Void> playEffectAsync(@Nonnull UUID player, @Nonnull String effect, T data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getParent())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.PLAY_EFFECT, Arrays.asList(player, effect, data)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.PLAY_EFFECT, Arrays.asList(player, effect, data)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> respawnAsync(@Nonnull UUID player) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getParent())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.RESPAWN, Collections.singletonList(player)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.RESPAWN, Collections.singletonList(player)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> teleportAsync(@Nonnull UUID player, @Nonnull String world, double x, double y, double z, float yaw, float pitch) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getParent())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.LOCATION_TELEPORT, Arrays.asList(player, world, x, y, z, yaw, pitch)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.LOCATION_TELEPORT, Arrays.asList(player, world, x, y, z, yaw, pitch)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> connectAsync(@Nonnull UUID player, @Nonnull String server) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnProxy(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getParent())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.CONNECT, Arrays.asList(player, server)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.CONNECT, Arrays.asList(player, server)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> connectAsync(@Nonnull UUID player, @Nonnull ProcessInformation server) {
        return connectAsync(player, server.getName());
    }

    @Nonnull
    @Override
    public Task<Void> connectAsync(@Nonnull UUID player, @Nonnull UUID target) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation targetServer = getPlayerOnServer(target);
            if (targetServer != null) {
                connectAsync(player, targetServer).awaitUninterruptedly();
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> setResourcePackAsync(@Nonnull UUID player, @Nonnull String pack) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getParent())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.SET_RESOURCE_PACK, Arrays.asList(player, pack)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.SET_RESOURCE_PACK, Arrays.asList(player, pack)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @Override
    public void sendMessage(@Nonnull UUID player, @Nonnull String message) {
        sendMessageAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void kickPlayer(@Nonnull UUID player, @Nonnull String message) {
        kickPlayerAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void kickPlayerFromServer(@Nonnull UUID player, @Nonnull String message) {
        kickPlayerFromServerAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void playSound(@Nonnull UUID player, @Nonnull String sound, float f1, float f2) {
        playSoundAsync(player, sound, f1, f2).awaitUninterruptedly();
    }

    @Override
    public void sendTitle(@Nonnull UUID player, @Nonnull String title, @Nonnull String subTitle, int fadeIn, int stay, int fadeOut) {
        sendTitleAsync(player, title, subTitle, fadeIn, stay, fadeOut).awaitUninterruptedly();
    }

    @Override
    public void playEffect(@Nonnull UUID player, @Nonnull String entityEffect) {
        playEffectAsync(player, entityEffect).awaitUninterruptedly();
    }

    @Override
    public <T> void playEffect(@Nonnull UUID player, @Nonnull String effect, T data) {
        playEffectAsync(player, effect, data).awaitUninterruptedly();
    }

    @Override
    public void respawn(@Nonnull UUID player) {
        respawnAsync(player).awaitUninterruptedly();
    }

    @Override
    public void teleport(@Nonnull UUID player, @Nonnull String world, double x, double y, double z, float yaw, float pitch) {
        teleportAsync(player, world, x, y, z, yaw, pitch).awaitUninterruptedly();
    }

    @Override
    public void connect(@Nonnull UUID player, @Nonnull String server) {
        connectAsync(player, server).awaitUninterruptedly();
    }

    @Override
    public void connect(@Nonnull UUID player, @Nonnull ProcessInformation server) {
        connectAsync(player, server).awaitUninterruptedly();
    }

    @Override
    public void connect(@Nonnull UUID player, @Nonnull UUID target) {
        connectAsync(player, target).awaitUninterruptedly();
    }

    @Override
    public void setResourcePack(@Nonnull UUID player, @Nonnull String pack) {
        setResourcePackAsync(player, pack).awaitUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<Void> installPluginAsync(@Nonnull String process, @Nonnull InstallablePlugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation information = this.nodeNetworkManager.getNodeProcessHelper().getClusterProcess(process);
            if (information == null) {
                task.complete(null);
                return;
            }

            task.complete(installPluginAsync(information, plugin).getUninterruptedly());
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> installPluginAsync(@Nonnull ProcessInformation process, @Nonnull InstallablePlugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(process.getParent())) {
                DefaultChannelManager.INSTANCE.get(process.getName()).ifPresent(e -> e.sendPacket(new NodePluginAction(
                        NodePluginAction.Action.INSTALL, process.getName(), new DefaultInstallablePlugin(
                        plugin.getDownloadURL(),
                        plugin.getName(),
                        plugin.version(),
                        plugin.author(),
                        plugin.main()
                ))));
            } else {
                DefaultChannelManager.INSTANCE.get(process.getParent()).ifPresent(e -> e.sendPacket(new NodePluginAction(
                        NodePluginAction.Action.INSTALL, process.getName(), new DefaultInstallablePlugin(
                        plugin.getDownloadURL(),
                        plugin.getName(),
                        plugin.version(),
                        plugin.author(),
                        plugin.main()
                ))));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> unloadPluginAsync(@Nonnull String process, @Nonnull Plugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation information = this.nodeNetworkManager.getNodeProcessHelper().getClusterProcess(process);
            if (information == null) {
                task.complete(null);
                return;
            }

            task.complete(unloadPluginAsync(information, plugin).getUninterruptedly());
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> unloadPluginAsync(@Nonnull ProcessInformation process, @Nonnull Plugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(process.getParent())) {
                DefaultChannelManager.INSTANCE.get(process.getName()).ifPresent(e -> e.sendPacket(new NodePluginAction(
                        NodePluginAction.Action.UNINSTALL,
                        process.getName(),
                        new DefaultPlugin(
                                plugin.version(),
                                plugin.author(),
                                plugin.main(),
                                plugin.depends(),
                                plugin.softpends(),
                                plugin.enabled(),
                                plugin.getName()
                        ))));
            } else {
                DefaultChannelManager.INSTANCE.get(process.getParent()).ifPresent(e -> e.sendPacket(new NodePluginAction(
                        NodePluginAction.Action.UNINSTALL,
                        process.getName(),
                        new DefaultPlugin(
                                plugin.version(),
                                plugin.author(),
                                plugin.main(),
                                plugin.depends(),
                                plugin.softpends(),
                                plugin.enabled(),
                                plugin.getName()
                        ))));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@Nonnull String process, @Nonnull String name) {
        Task<Plugin> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation information = this.nodeNetworkManager.getNodeProcessHelper().getClusterProcess(process);
            if (information == null) {
                task.complete(null);
                return;
            }

            task.complete(getInstalledPluginAsync(information, name).getUninterruptedly());
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@Nonnull ProcessInformation process, @Nonnull String name) {
        Task<Plugin> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.filterToReference(process.getPlugins(), e -> e.getName().equals(name)).orNothing()));
        return task;
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull String process, @Nonnull String author) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation information = this.nodeNetworkManager.getNodeProcessHelper().getClusterProcess(process);
            if (information == null) {
                task.complete(null);
                return;
            }

            task.complete(getPluginsAsync(information, author).getUninterruptedly());
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull ProcessInformation process, @Nonnull String author) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.allOf(process.getPlugins(), e -> e.author().equals(author))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull String process) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation information = this.nodeNetworkManager.getNodeProcessHelper().getClusterProcess(process);
            if (information == null) {
                task.complete(null);
                return;
            }

            task.complete(getPluginsAsync(information).getUninterruptedly());
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull ProcessInformation processInformation) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Collections.unmodifiableList(processInformation.getPlugins())));
        return task;
    }

    @Override
    public void installPlugin(@Nonnull String process, @Nonnull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void installPlugin(@Nonnull ProcessInformation process, @Nonnull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void unloadPlugin(@Nonnull String process, @Nonnull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public void unloadPlugin(@Nonnull ProcessInformation process, @Nonnull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly();
    }

    @Override
    public Plugin getInstalledPlugin(@Nonnull String process, @Nonnull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly();
    }

    @Override
    public Plugin getInstalledPlugin(@Nonnull ProcessInformation process, @Nonnull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull String process, @Nonnull String author) {
        return getPluginsAsync(process, author).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull ProcessInformation process, @Nonnull String author) {
        return getPluginsAsync(process, author).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull String process) {
        return getPluginsAsync(process).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull ProcessInformation processInformation) {
        return getPluginsAsync(processInformation).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> startProcessAsync(@Nonnull String groupName) {
        return startProcessAsync(groupName, null);
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, String template) {
        return startProcessAsync(groupName, template, new JsonConfiguration());
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> startProcessAsync(@Nonnull String groupName, String template, @Nonnull JsonConfiguration configurable) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessGroup group = Links.filterToReference(clusterSyncManager.getProcessGroups(), e -> e.getName().equals(groupName)).orNothing();
            if (group == null) {
                task.complete(null);
                return;
            }

            task.complete(nodeNetworkManager.startProcess(
                    group,
                    Links.filterToReference(group.getTemplates(), e -> e.getName().equals(template)).orNothing(),
                    configurable
            ));
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@Nonnull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation old = nodeNetworkManager.getNodeProcessHelper().getClusterProcess(name);
            if (old == null) {
                task.complete(null);
                return;
            }

            nodeNetworkManager.stopProcess(old.getName());
            task.complete(old);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@Nonnull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation old = nodeNetworkManager.getNodeProcessHelper().getClusterProcess(uniqueID);
            if (old == null) {
                task.complete(null);
                return;
            }

            nodeNetworkManager.stopProcess(old.getProcessUniqueID());
            task.complete(old);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getProcessAsync(@Nonnull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(nodeNetworkManager.getNodeProcessHelper().getClusterProcess(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getProcessAsync(@Nonnull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(nodeNetworkManager.getNodeProcessHelper().getClusterProcess(uniqueID)));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<ProcessInformation>> getAllProcessesAsync() {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.newList(nodeNetworkManager.getNodeProcessHelper().getClusterProcesses())));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<ProcessInformation>> getProcessesAsync(@Nonnull String group) {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.newList(nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(group))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> executeProcessCommandAsync(@Nonnull String name, @Nonnull String commandLine) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getProcess(name);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeConfig.getUniqueID().equals(processInformation.getNodeUniqueID())) {
                Links.filterToReference(LocalProcessManager.getNodeProcesses(),
                        e -> e.getProcessInformation().getProcessUniqueID().equals(processInformation.getProcessUniqueID())
                ).ifPresent(e -> e.sendCommand(commandLine));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getParent())
                        .ifPresent(e -> e.sendPacket(new NodePacketOutExecuteCommand(processInformation.getName(), commandLine)));
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Integer> getGlobalOnlineCountAsync(@Nonnull Collection<String> ignoredProxies) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            int online = Links.allOf(nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(),
                    e -> !e.getTemplate().isServer() && !ignoredProxies.contains(e.getName())
            ).stream().mapToInt(ProcessInformation::getOnlineCount).sum();
            task.complete(online);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getThisProcessInformationAsync() {
        Task<ProcessInformation> task = new DefaultTask<>();
        task.complete(null);
        return task;
    }

    @Nonnull
    @Override
    public ProcessInformation startProcess(@Nonnull String groupName) {
        return startProcessAsync(groupName).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessInformation startProcess(@Nonnull String groupName, String template) {
        return startProcessAsync(groupName, template).getUninterruptedly();
    }

    @Nonnull
    @Override
    public ProcessInformation startProcess(@Nonnull String groupName, String template, @Nonnull JsonConfiguration configurable) {
        return startProcessAsync(groupName, template, configurable).getUninterruptedly();
    }

    @Override
    public ProcessInformation stopProcess(@Nonnull String name) {
        return stopProcessAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessInformation stopProcess(@Nonnull UUID uniqueID) {
        return stopProcessAsync(uniqueID).getUninterruptedly();
    }

    @Override
    public ProcessInformation getProcess(@Nonnull String name) {
        return getProcessAsync(name).getUninterruptedly();
    }

    @Override
    public ProcessInformation getProcess(@Nonnull UUID uniqueID) {
        return getProcessAsync(uniqueID).getUninterruptedly();
    }

    @Nonnull
    @Override
    public List<ProcessInformation> getAllProcesses() {
        return getAllProcessesAsync().getUninterruptedly();
    }

    @Nonnull
    @Override
    public List<ProcessInformation> getProcesses(@Nonnull String group) {
        return getProcessesAsync(group).getUninterruptedly();
    }

    @Override
    public void executeProcessCommand(@Nonnull String name, @Nonnull String commandLine) {
        executeProcessCommandAsync(name, commandLine).awaitUninterruptedly();
    }

    @Override
    public int getGlobalOnlineCount(@Nonnull Collection<String> ignoredProxies) {
        return getGlobalOnlineCountAsync(ignoredProxies).getUninterruptedly();
    }

    @Override
    public ProcessInformation getThisProcessInformation() {
        return null;
    }

    @Override
    public void update(@Nonnull ProcessInformation processInformation) {
        this.nodeNetworkManager.getNodeProcessHelper().update(processInformation);
    }

    @Override
    public void reload() throws Exception {
        final long current = System.currentTimeMillis();
        System.out.println(LanguageManager.get("runtime-try-reload"));

        this.applicationLoader.disableApplications();

        this.commandManager.unregisterAll();
        this.packetHandler.clearHandlers();

        this.clusterSyncManager.getProcessGroups().clear();
        this.clusterSyncManager.getMainGroups().clear();

        this.nodeExecutorConfig.getProcessGroups().clear();
        this.nodeExecutorConfig.getMainGroups().clear();

        this.applicationLoader.detectApplications();
        this.applicationLoader.installApplications();

        LanguageWorker.doReload();

        this.nodeConfig = this.nodeExecutorConfig.reload();

        this.clusterSyncManager.getProcessGroups().addAll(nodeExecutorConfig.getProcessGroups());
        this.clusterSyncManager.getMainGroups().addAll(nodeExecutorConfig.getMainGroups());

        this.localAutoStartupHandler.update();

        this.applicationLoader.loadApplications();

        this.sendGroups();
        this.loadCommands();
        this.loadPacketHandlers();

        this.applicationLoader.enableApplications();

        this.clusterSyncManager.doClusterReload();
        System.out.println(LanguageManager.get("runtime-reload-done", Long.toString(System.currentTimeMillis() - current)));
    }

    private void awaitConnectionsAndUpdate() {
        CommonHelper.EXECUTOR.execute(() -> {
            while (!this.clusterSyncManager.isConnectedAndSyncWithCluster()) {
                AbsoluteThread.sleep(100);
            }

            AbsoluteThread.sleep(100);
            if (this.nodeNetworkManager.getCluster().isSelfNodeHead()) {
                return;
            }

            this.clusterSyncManager.syncProcessGroups(
                    this.nodeExecutorConfig.getProcessGroups(), SyncAction.SYNC
            );
            this.clusterSyncManager.syncMainGroups(
                    this.nodeExecutorConfig.getMainGroups(), SyncAction.SYNC
            );
        });
    }

    private void loadCommands() {
        this.commandManager
                .register(new CommandDump(new NodeDumpUtil()))
                .register(new CommandReformCloud())
                .register(CommandStop.class)
                .register(new CommandReload(this))
                .register(new CommandClear(loggerBase))
                .register(new CommandHelp(commandManager));
    }

    private void loadPacketHandlers() {
        new Reflections("systems.reformcloud.reformcloud2.executor.node.network.packet.in").getSubTypesOf(NetworkHandler.class).forEach(packetHandler::registerHandler);

        // The query handler for the external api, we can re-use them
        new Reflections("systems.reformcloud.reformcloud2.executor.controller.packet.in.query").getSubTypesOf(NetworkHandler.class).forEach(e -> {
            if (e.getSimpleName().equals("ControllerQueryInRequestIngameMessages")) {
                return;
            }

            packetHandler.registerHandler(e);
        });

        new Reflections("systems.reformcloud.reformcloud2.executor.node.network.packet.query.in").getSubTypesOf(NetworkHandler.class).forEach(packetHandler::registerHandler);
    }

    private void sync(PacketSender sender) {
        Task.EXECUTOR.execute(() -> {
            while (!DefaultChannelManager.INSTANCE.get(sender.getName()).isPresent()) {
                AbsoluteThread.sleep(20);
            }

            this.clusterSyncManager.syncMainGroups(this.nodeExecutorConfig.getMainGroups(), SyncAction.SYNC);
            this.clusterSyncManager.syncProcessGroups(this.nodeExecutorConfig.getProcessGroups(), SyncAction.SYNC);
            this.clusterSyncManager.syncProcessInformation(Links.allOf(
                    this.nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(),
                    e -> this.nodeNetworkManager.getNodeProcessHelper().isLocal(e.getProcessUniqueID())
            ));
        });
    }

    private void sendGroups() {
        this.nodeExecutorConfig.getMainGroups().forEach(mainGroup -> System.out.println(LanguageManager.get("loading-main-group", mainGroup.getName())));
        this.nodeExecutorConfig.getProcessGroups().forEach(processGroup -> System.out.println(LanguageManager.get("loading-process-group", processGroup.getName())));
    }

    private ProcessInformation getPlayerOnProxy(UUID uniqueID) {
        return Links.filter(this.nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(), processInformation -> !processInformation.getTemplate().isServer() && Links.filterToReference(processInformation.getOnlinePlayers(), player -> player.getUniqueID().equals(uniqueID)).isPresent());
    }

    private ProcessInformation getPlayerOnServer(UUID uniqueID) {
        return Links.filter(this.nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(), processInformation -> processInformation.getTemplate().isServer() && Links.filterToReference(processInformation.getOnlinePlayers(), player -> player.getUniqueID().equals(uniqueID)).isPresent());
    }
}
