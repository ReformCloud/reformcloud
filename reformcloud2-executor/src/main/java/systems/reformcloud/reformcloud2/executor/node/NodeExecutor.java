package systems.reformcloud.reformcloud2.executor.node;

import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.*;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.dump.CommandDump;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.shared.CommandClear;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.shared.CommandHelp;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.shared.CommandReload;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.shared.CommandStop;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.manager.DefaultCommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.file.FileDatabase;
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.h2.H2Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mongo.MongoDatabase;
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mysql.MySQLDatabase;
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.rethinkdb.RethinkDBDatabase;
import systems.reformcloud.reformcloud2.executor.api.common.database.config.DatabaseConfig;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import systems.reformcloud.reformcloud2.executor.api.common.groups.task.OnlinePercentCheckerTask;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.ColouredLoggerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.logger.other.DefaultLoggerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared.ClientChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared.SharedChallengeProvider;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.NamedMessagePacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.TypeMessagePacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.DefaultNetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.NetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.basic.DefaultWebServerAuth;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.http.server.DefaultWebServer;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.http.server.WebServer;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults.DefaultRequestListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.user.WebUser;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.node.Node;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterSyncManager;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.SyncAction;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.node.api.GeneralAPI;
import systems.reformcloud.reformcloud2.executor.node.api.console.ConsoleAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.database.DatabaseAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.group.GroupAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.message.ChannelMessageAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.player.PlayerAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.process.ProcessAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.cluster.DefaultClusterManager;
import systems.reformcloud.reformcloud2.executor.node.cluster.DefaultNodeInternalCluster;
import systems.reformcloud.reformcloud2.executor.node.cluster.sync.DefaultClusterSyncManager;
import systems.reformcloud.reformcloud2.executor.node.commands.CommandCluster;
import systems.reformcloud.reformcloud2.executor.node.config.NodeConfig;
import systems.reformcloud.reformcloud2.executor.node.config.NodeExecutorConfig;
import systems.reformcloud.reformcloud2.executor.node.dump.NodeDumpUtil;
import systems.reformcloud.reformcloud2.executor.node.network.DefaultNodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.node.network.auth.NodeChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.node.network.channel.NodeNetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.node.network.channel.NodeNetworkSuccessHandler;
import systems.reformcloud.reformcloud2.executor.node.network.client.NodeNetworkClient;
import systems.reformcloud.reformcloud2.executor.node.network.packet.handler.PacketInAPILogoutPlayer;
import systems.reformcloud.reformcloud2.executor.node.network.packet.handler.PacketInAPIPlayerCommandExecute;
import systems.reformcloud.reformcloud2.executor.node.network.packet.handler.PacketInAPIPlayerLoggedIn;
import systems.reformcloud.reformcloud2.executor.node.network.packet.handler.PacketInAPIServerSwitchPlayer;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.screen.NodePacketOutToggleScreen;
import systems.reformcloud.reformcloud2.executor.node.network.packet.query.APIPacketOutRequestIngameMessagesHandler;
import systems.reformcloud.reformcloud2.executor.node.network.packet.query.PacketNodeQueryStartProcess;
import systems.reformcloud.reformcloud2.executor.node.network.packet.query.PacketNodeQueryStartProcessResult;
import systems.reformcloud.reformcloud2.executor.node.process.LocalAutoStartupHandler;
import systems.reformcloud.reformcloud2.executor.node.process.LocalNodeProcessManager;
import systems.reformcloud.reformcloud2.executor.node.process.listeners.RunningProcessPreparedListener;
import systems.reformcloud.reformcloud2.executor.node.process.listeners.RunningProcessStartedListener;
import systems.reformcloud.reformcloud2.executor.node.process.listeners.RunningProcessStoppedListener;
import systems.reformcloud.reformcloud2.executor.node.process.log.LogLineReader;
import systems.reformcloud.reformcloud2.executor.node.process.log.NodeProcessScreen;
import systems.reformcloud.reformcloud2.executor.node.process.log.NodeProcessScreenHandler;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;
import systems.reformcloud.reformcloud2.executor.node.process.startup.LocalProcessQueue;
import systems.reformcloud.reformcloud2.executor.node.process.watchdog.WatchdogThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class NodeExecutor extends Node {

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

    private final EventManager eventManager = new DefaultEventManager();

    private LocalAutoStartupHandler localAutoStartupHandler;

    private SyncAPI syncAPI;

    private AsyncAPI asyncAPI;

    private LocalProcessQueue localProcessQueue;

    private LogLineReader logLineReader;

    private WatchdogThread watchdogThread;

    private NetworkClient networkClient;

    private NodeConfig nodeConfig;

    private Database<?> database;

    private NodeNetworkManager nodeNetworkManager;

    private ClusterSyncManager clusterSyncManager;

    private LoggerBase loggerBase;

    private RequestListenerHandler requestListenerHandler;

    NodeExecutor() {
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
            if (Boolean.getBoolean("reformcloud.disable.colours")) {
                this.loggerBase = new DefaultLoggerHandler(this.commandManager);
            } else {
                this.loggerBase = new ColouredLoggerHandler(this.commandManager);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        SystemHelper.deleteDirectory(Paths.get("reformcloud/temp"));

        this.nodeExecutorConfig.init();
        this.nodeConfig = this.nodeExecutorConfig.getNodeConfig();

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

            case RETHINK_DB: {
                this.database = new RethinkDBDatabase();
                this.databaseConfig.connect(this.database);
                break;
            }
        }

        this.nodeNetworkManager = new DefaultNodeNetworkManager(
                new LocalNodeProcessManager(),
                new DefaultNodeInternalCluster(new DefaultClusterManager(), nodeExecutorConfig.getSelf(), packetHandler)
        );
        this.nodeNetworkManager.getCluster().getClusterManager().init();

        final NodeNetworkClient nodeNetworkClient = new NodeNetworkClient();
        this.networkClient = nodeNetworkClient;
        this.clusterSyncManager = new DefaultClusterSyncManager(nodeNetworkClient);

        this.clusterSyncManager.getProcessGroups().addAll(nodeExecutorConfig.getProcessGroups());
        this.clusterSyncManager.getMainGroups().addAll(nodeExecutorConfig.getMainGroups());

        GeneralAPI generalAPI = new GeneralAPI(
                new ConsoleAPIImplementation(this.commandManager),
                new DatabaseAPIImplementation(this.database),
                new GroupAPIImplementation(this.clusterSyncManager),
                new PlayerAPIImplementation(this.nodeNetworkManager),
                new ProcessAPIImplementation(this.nodeNetworkManager),
                new ChannelMessageAPIImplementation()
        );
        this.syncAPI = generalAPI;
        this.asyncAPI = generalAPI;

        this.requestListenerHandler = new DefaultRequestListenerHandler(new DefaultWebServerAuth(this.getSyncAPI().getDatabaseSyncAPI()));

        this.applicationLoader.detectApplications();
        this.applicationLoader.installApplications();

        TemplateBackendManager.registerDefaults();

        ExecutorAPI.getInstance().getEventManager().registerListener(new RunningProcessPreparedListener());
        ExecutorAPI.getInstance().getEventManager().registerListener(new RunningProcessStartedListener());
        ExecutorAPI.getInstance().getEventManager().registerListener(new RunningProcessStoppedListener());

        this.logLineReader = new LogLineReader();
        this.watchdogThread = new WatchdogThread();
        this.localAutoStartupHandler = new LocalAutoStartupHandler();

        this.loadPacketHandlers();

        this.nodeConfig.getHttpNetworkListeners().forEach(e -> this.webServer.add(e.getHost(), e.getPort(), this.requestListenerHandler));
        this.nodeConfig.getNetworkListeners().forEach(e -> this.networkServer.bind(
                e.getHost(),
                e.getPort(),
                () -> new NodeNetworkChannelReader(this.packetHandler),
                new NodeChallengeAuthHandler(new SharedChallengeProvider(this.nodeExecutorConfig.getConnectionKey()), new NodeNetworkSuccessHandler())
        ));

        this.applicationLoader.loadApplications();
        this.getSyncAPI().getDatabaseSyncAPI().createDatabase("internal_users");

        if (this.nodeExecutorConfig.isFirstStartup()) {
            final String token = StringUtil.generateString(2);
            WebUser webUser = new WebUser("admin", token, Collections.singletonList("*"));
            this.getSyncAPI().getDatabaseSyncAPI().insert("internal_users", webUser.getName(), "", new JsonConfiguration().add("user", webUser));

            System.out.println(LanguageManager.get("setup-created-default-user", webUser.getName(), token));
        }

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
                            () -> new NodeNetworkChannelReader(NodeExecutor.getInstance().getPacketHandler()),
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

        this.localProcessQueue = new LocalProcessQueue();
        this.localAutoStartupHandler.update();
        this.localAutoStartupHandler.doStart();
        this.applicationLoader.enableApplications();

        this.loadCommands();
        this.sendGroups();

        OnlinePercentCheckerTask.start();

        running = true;
        System.out.println(LanguageManager.get("startup-done", Long.toString(System.currentTimeMillis() - current)));

        this.awaitConnectionsAndUpdate();
        this.startSendUpdate();
        this.runConsole();
    }

    @NotNull
    public static NodeExecutor getInstance() {
        return instance;
    }

    @NotNull
    @Override
    public SyncAPI getSyncAPI() {
        return syncAPI;
    }

    @NotNull
    @Override
    public AsyncAPI getAsyncAPI() {
        return asyncAPI;
    }

    public RequestListenerHandler getRequestListenerHandler() {
        return requestListenerHandler;
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

    public Database<?> getDatabase() {
        return database;
    }

    @NotNull
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public boolean isReady() {
        return this.clusterSyncManager.isConnectedAndSyncWithCluster();
    }

    public Duo<String, Integer> getConnectHost() {
        if (this.nodeConfig.getNetworkListeners().size() == 1) {
            NodeConfig.NetworkAddress address = this.nodeConfig.getNetworkListeners().get(0);
            return new Duo<>(address.getHost(), address.getPort());
        }

        NodeConfig.NetworkAddress address = this.nodeConfig.getNetworkListeners().get(new Random().nextInt(
                this.nodeConfig.getNetworkListeners().size()
        ));
        return new Duo<>(address.getHost(), address.getPort());
    }

    @Override
    public void shutdown() throws Exception {
        if (running) {
            running = false;
        } else {
            return;
        }

        System.out.println(LanguageManager.get("runtime-try-shutdown"));

        OnlinePercentCheckerTask.stop();

        this.clusterSyncManager.disconnectFromCluster();

        this.networkServer.closeAll();
        this.networkClient.disconnect();
        this.webServer.close();

        this.watchdogThread.interrupt();
        this.logLineReader.interrupt();
        this.localProcessQueue.interrupt();
        this.localAutoStartupHandler.interrupt();
        this.nodeNetworkManager.close();

        LocalProcessManager.close();

        this.database.disconnect();

        this.applicationLoader.disableApplications();

        CommonHelper.SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
        CommonHelper.EXECUTOR.shutdownNow();
        Task.EXECUTOR.shutdownNow();

        SystemHelper.deleteDirectory(Paths.get("reformcloud/temp"));

        this.loggerBase.close();
    }

    private void runConsole() {
        String line;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                line = loggerBase.readLine();
                while (!line.trim().isEmpty() && running) {
                    commandManager.dispatchCommand(this.console, AllowedCommandSources.ALL, line, System.out::println);

                    line = loggerBase.readLine();
                }
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    @NotNull
    @Override
    public NetworkServer getNetworkServer() {
        return networkServer;
    }

    @NotNull
    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @NotNull
    @Override
    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    @Override
    public void reload() {
        reload(true);
    }

    public void reload(boolean informCluster) {
        final long current = System.currentTimeMillis();
        System.out.println(LanguageManager.get("runtime-try-reload"));

        OnlinePercentCheckerTask.stop();

        this.applicationLoader.disableApplications();

        this.commandManager.unregisterAll();
        this.packetHandler.clearHandlers();

        this.clusterSyncManager.getProcessGroups().clear();
        this.clusterSyncManager.getMainGroups().clear();

        this.applicationLoader.detectApplications();
        this.applicationLoader.installApplications();

        LanguageWorker.doReload();

        this.nodeConfig = this.nodeExecutorConfig.reload();
        this.clusterSyncManager.syncSelfInformation();

        this.clusterSyncManager.getProcessGroups().addAll(nodeExecutorConfig.getProcessGroups());
        this.clusterSyncManager.getMainGroups().addAll(nodeExecutorConfig.getMainGroups());

        this.localAutoStartupHandler.update();

        this.applicationLoader.loadApplications();

        this.sendGroups();
        this.loadCommands();
        this.loadPacketHandlers();

        this.applicationLoader.enableApplications();

        if (informCluster) {
            this.clusterSyncManager.doClusterReload();
        }

        OnlinePercentCheckerTask.start();
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
                .register(new CommandProcess(target -> {
                    if (target.getProcessDetail().getParentUniqueID().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
                        ReferencedOptional<NodeProcessScreen> screen = NodeProcessScreenHandler.getScreen(target.getProcessDetail().getProcessUniqueID());
                        return screen.isPresent() && screen.get().toggleFor(NodeExecutor.getInstance().getNodeConfig().getName());
                    } else {
                        ReferencedOptional<PacketSender> optional = DefaultChannelManager.INSTANCE.get(target.getProcessDetail().getParentName());
                        optional.ifPresent(packetSender -> packetSender.sendPacket(new NodePacketOutToggleScreen(target.getProcessDetail().getProcessUniqueID())));
                        return optional.isPresent();
                    }
                }))
                .register(new CommandCluster())
                .register(new CommandPlayers())
                .register(new CommandGroup())
                .register(new CommandDump(new NodeDumpUtil()))
                .register(new CommandCreate())
                .register(new CommandLaunch())
                .register(new CommandStop())
                .register(new CommandReload(this))
                .register(new CommandClear(loggerBase))
                .register(new CommandHelp(commandManager));
    }

    private void loadPacketHandlers() {
        new Reflections("systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api")
                .getSubTypesOf(Packet.class)
                .forEach(packetHandler::registerHandler);

        new Reflections("systems.reformcloud.reformcloud2.executor.node.network.packet.out")
                .getSubTypesOf(Packet.class)
                .forEach(packetHandler::registerHandler);

        // Queries
        this.packetHandler.registerHandler(APIPacketOutRequestIngameMessagesHandler.class);
        this.packetHandler.registerHandler(PacketNodeQueryStartProcess.class);
        this.packetHandler.registerHandler(PacketNodeQueryStartProcessResult.class);

        // Channel Messages
        this.packetHandler.registerHandler(NamedMessagePacket.class);
        this.packetHandler.registerHandler(TypeMessagePacket.class);

        // API -> Node handler
        this.packetHandler.registerHandler(PacketInAPILogoutPlayer.class);
        this.packetHandler.registerHandler(PacketInAPIPlayerCommandExecute.class);
        this.packetHandler.registerHandler(PacketInAPIPlayerLoggedIn.class);
        this.packetHandler.registerHandler(PacketInAPIServerSwitchPlayer.class);
    }

    public void sync(String name) {
        Task.EXECUTOR.execute(() -> {
            while (DefaultChannelManager.INSTANCE.get(name).isEmpty()) {
                AbsoluteThread.sleep(20);
            }

            this.clusterSyncManager.syncMainGroups(this.nodeExecutorConfig.getMainGroups(), SyncAction.SYNC);
            this.clusterSyncManager.syncProcessGroups(this.nodeExecutorConfig.getProcessGroups(), SyncAction.SYNC);
            this.clusterSyncManager.syncProcessInformation(Streams.allOf(
                    this.nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(),
                    e -> this.nodeNetworkManager.getNodeProcessHelper().isLocal(e.getProcessDetail().getProcessUniqueID())
            ));
        });
    }

    private void sendGroups() {
        this.nodeExecutorConfig.getMainGroups().forEach(mainGroup -> System.out.println(LanguageManager.get("loading-main-group", mainGroup.getName())));
        this.nodeExecutorConfig.getProcessGroups().forEach(processGroup -> System.out.println(LanguageManager.get("loading-process-group", processGroup.getName())));
    }

    public void handleChannelDisconnect(@NotNull PacketSender packetSender) {
        NodeInformation information = nodeNetworkManager.getCluster().getNode(packetSender.getName());
        if (information == null) {
            nodeNetworkManager.getNodeProcessHelper().handleProcessDisconnect(packetSender.getName());
        } else {
            nodeNetworkManager.getCluster().getClusterManager().handleNodeDisconnect(
                    nodeNetworkManager.getCluster(),
                    packetSender.getName()
            );
        }
    }

    private void startSendUpdate() {
        CommonHelper.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(this.clusterSyncManager::syncSelfInformation, 10, 30, TimeUnit.SECONDS);
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }
}
