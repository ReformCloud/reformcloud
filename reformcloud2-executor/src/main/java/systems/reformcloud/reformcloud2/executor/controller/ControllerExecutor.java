package systems.reformcloud.reformcloud2.executor.controller;

import org.reflections.Reflections;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.CommandCreate;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.CommandLaunch;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.CommandProcess;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.dump.CommandDump;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.dump.basic.DefaultDumpUtil;
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
import systems.reformcloud.reformcloud2.executor.api.common.database.config.DatabaseConfig;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import systems.reformcloud.reformcloud2.executor.api.common.groups.task.OnlinePercentCheckerTask;
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
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.DefaultNetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.NetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.basic.DefaultWebServerAuth;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.http.server.DefaultWebServer;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.http.server.WebServer;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults.DefaultRequestListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.user.WebUser;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.api.controller.Controller;
import systems.reformcloud.reformcloud2.executor.api.controller.process.ProcessManager;
import systems.reformcloud.reformcloud2.executor.controller.api.GeneralAPI;
import systems.reformcloud.reformcloud2.executor.controller.api.applications.ApplicationAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.console.ConsoleAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.database.DatabaseAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.group.GroupAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.message.ChannelMessageAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.player.PlayerAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.plugins.PluginAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.process.ProcessAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.config.ControllerConfig;
import systems.reformcloud.reformcloud2.executor.controller.config.ControllerExecutorConfig;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.ControllerPacketOutCopyProcess;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.ControllerPacketOutToggleScreen;
import systems.reformcloud.reformcloud2.executor.controller.process.ClientManager;
import systems.reformcloud.reformcloud2.executor.controller.process.DefaultProcessManager;
import systems.reformcloud.reformcloud2.executor.controller.process.startup.AutoStartupHandler;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public final class ControllerExecutor extends Controller {

    private static ControllerExecutor instance;

    private static volatile boolean running = false;

    private LoggerBase loggerBase;

    private AutoStartupHandler autoStartupHandler;

    private ControllerExecutorConfig controllerExecutorConfig;

    private ControllerConfig controllerConfig;

    private Database<?> database;

    private RequestListenerHandler requestListenerHandler;

    private SyncAPI syncAPI;

    private AsyncAPI asyncAPI;

    private final CommandManager commandManager = new DefaultCommandManager();

    private final CommandSource console = new ConsoleCommandSource(commandManager);

    private final ApplicationLoader applicationLoader = new DefaultApplicationLoader();

    private final NetworkServer networkServer = new DefaultNetworkServer();

    private final WebServer webServer = new DefaultWebServer();

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final ProcessManager processManager = new DefaultProcessManager();

    private final DatabaseConfig databaseConfig = new DatabaseConfig();

    private final EventManager eventManager = new DefaultEventManager();

    ControllerExecutor() {
        ExecutorAPI.setInstance(this);
        super.type = ExecutorType.CONTROLLER;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                shutdown();
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }, "Shutdown-Hook"));

        bootstrap();
    }

    @Override
    protected void bootstrap() {
        long current = System.currentTimeMillis();
        instance = this;

        try {
            if (Boolean.getBoolean("reformcloud2.disable.colours")) {
                this.loggerBase = new DefaultLoggerHandler(this.commandManager);
            } else {
                this.loggerBase = new ColouredLoggerHandler(this.commandManager);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        this.controllerExecutorConfig = new ControllerExecutorConfig();
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

        GeneralAPI generalAPI = new GeneralAPI(
                new ApplicationAPIImplementation(this.applicationLoader),
                new ConsoleAPIImplementation(this.commandManager),
                new DatabaseAPIImplementation(this.database),
                new GroupAPIImplementation(),
                new PlayerAPIImplementation(this.processManager),
                new PluginAPIImplementation(),
                new ProcessAPIImplementation(this.processManager),
                new ChannelMessageAPIImplementation()
        );
        this.syncAPI = generalAPI;
        this.asyncAPI = generalAPI;

        this.requestListenerHandler = new DefaultRequestListenerHandler(new DefaultWebServerAuth(this.getSyncAPI().getDatabaseSyncAPI()));

        applicationLoader.detectApplications();
        applicationLoader.installApplications();

        this.controllerConfig = controllerExecutorConfig.getControllerConfig();
        this.controllerConfig.getNetworkListener().forEach(stringIntegerMap -> stringIntegerMap.forEach((s, integer) -> ControllerExecutor.this.networkServer.bind(s, integer, new DefaultServerAuthHandler(
                packetHandler,
                packetSender -> {
                    ClientManager.INSTANCE.disconnectClient(packetSender.getName());
                    processManager.onChannelClose(packetSender.getName());
                },
                packet -> {
                    DefaultAuth auth = packet.content().get("auth", Auth.TYPE);
                    if (auth == null) {
                        return new Duo<>("", false);
                    }

                    if (!auth.key().equals(controllerExecutorConfig.getConnectionKey())) {
                        System.out.println(LanguageManager.get("network-channel-auth-failed", auth.getName()));
                        return new Duo<>(auth.getName(), false);
                    }

                    if (auth.type().equals(NetworkType.CLIENT)) {
                        System.out.println(LanguageManager.get("client-connected", auth.getName()));
                        DefaultClientRuntimeInformation runtimeInformation = auth.extra().get("info", ClientRuntimeInformation.TYPE);
                        ClientManager.INSTANCE.connectClient(runtimeInformation);
                    } else {
                        ProcessInformation information = processManager.getProcess(auth.getName());
                        if (information == null) {
                            return new Duo<>(auth.getName(), false);
                        }

                        information.getNetworkInfo().setConnected(true);
                        information.setProcessState(ProcessState.READY);
                        processManager.update(information);

                        System.out.println(LanguageManager.get("process-connected", auth.getName(), auth.parent()));
                    }

                    System.out.println(LanguageManager.get("network-channel-auth-success", auth.getName(), auth.parent()));
                    return new Duo<>(auth.getName(), true);
                }
        ))));

        applicationLoader.loadApplications();

        this.autoStartupHandler = new AutoStartupHandler();
        sendGroups();
        loadCommands();
        loadPacketHandlers();

        this.getSyncAPI().getDatabaseSyncAPI().createDatabase("internal_users");
        if (controllerExecutorConfig.isFirstStartup()) {
            final String token = StringUtil.generateString(2);
            WebUser webUser = new WebUser("admin", token, Collections.singletonList("*"));
            this.getSyncAPI().getDatabaseSyncAPI().insert("internal_users", webUser.getName(), "", new JsonConfiguration().add("user", webUser));

            System.out.println(LanguageManager.get("setup-created-default-user", webUser.getName(), token));
        }

        this.controllerExecutorConfig.getControllerConfig().getHttpNetworkListener().forEach(map -> map.forEach((host, port) -> {
                this.webServer.add(host, port, this.requestListenerHandler);
            })
        );

        applicationLoader.enableApplications();

        if (Files.exists(Paths.get("reformcloud/.client"))) {
            try {
                Process process = new ProcessBuilder()
                        .command(Arrays.asList("java", "-jar", "runner.jar").toArray(new String[0]))
                        .directory(new File("reformcloud/.client"))
                        .start();
                ClientManager.INSTANCE.setProcess(process);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }

        OnlinePercentCheckerTask.start();

        running = true;
        System.out.println(LanguageManager.get("startup-done", Long.toString(System.currentTimeMillis() - current)));
        runConsole();
    }

    @Override
    public void reload() {
        final long current = System.currentTimeMillis();
        System.out.println(LanguageManager.get("runtime-try-reload"));

        OnlinePercentCheckerTask.stop(); // To update the config of the auto start, too

        this.applicationLoader.disableApplications();

        this.commandManager.unregisterAll();
        this.packetHandler.clearHandlers();
        this.packetHandler.getQueryHandler().clearQueries(); //Unsafe? May produce exception if query is sent but not handled after reload

        this.controllerExecutorConfig.getProcessGroups().clear();
        this.controllerExecutorConfig.getMainGroups().clear();

        this.applicationLoader.detectApplications();
        this.applicationLoader.installApplications();

        LanguageWorker.doReload(); //Reloads the language files

        this.controllerExecutorConfig = new ControllerExecutorConfig();

        this.autoStartupHandler.update(); //Update the automatic startup handler to re-sort the groups per priority

        this.controllerConfig = controllerExecutorConfig.getControllerConfig();

        this.applicationLoader.loadApplications();

        this.sendGroups();
        this.loadCommands();
        this.loadPacketHandlers();

        OnlinePercentCheckerTask.start();

        this.applicationLoader.enableApplications();
        System.out.println(LanguageManager.get("runtime-reload-done", Long.toString(System.currentTimeMillis() - current)));
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

        this.networkServer.closeAll(); //Close network first that all channels now that the controller is disconnecting
        this.webServer.close();
        ClientManager.INSTANCE.onShutdown();

        this.loggerBase.close();
        this.autoStartupHandler.interrupt();
        this.applicationLoader.disableApplications();
    }

    public ControllerExecutorConfig getControllerExecutorConfig() {
        return controllerExecutorConfig;
    }

    @Nonnull
    public static ControllerExecutor getInstance() {
        if (instance == null) {
            return (ControllerExecutor) Controller.getInstance();
        }

        return instance;
    }

    @Nonnull
    public ApplicationLoader getApplicationLoader() {
        return applicationLoader;
    }

    @Nonnull
    @Override
    public SyncAPI getSyncAPI() {
        return syncAPI;
    }

    @Nonnull
    @Override
    public AsyncAPI getAsyncAPI() {
        return asyncAPI;
    }

    @Override
    public NetworkServer getNetworkServer() {
        return networkServer;
    }

    @Nonnull
    @Override
    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    public RequestListenerHandler getRequestListenerHandler() {
        return requestListenerHandler;
    }

    public LoggerBase getLoggerBase() {
        return loggerBase;
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    public ControllerConfig getControllerConfig() {
        return controllerConfig;
    }

    public AutoStartupHandler getAutoStartupHandler() {
        return autoStartupHandler;
    }

    public Database<?> getDatabase() {
        return database;
    }

    @Nonnull
    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    private void runConsole() {
        String line;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                line = loggerBase.readLine();
                while (!line.trim().isEmpty() && running) {
                    commandManager.dispatchCommand(console, AllowedCommandSources.ALL, line, System.out::println);

                    line = loggerBase.readLine();
                }
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void sendGroups() {
        this.controllerExecutorConfig.getMainGroups().forEach(mainGroup -> System.out.println(LanguageManager.get("loading-main-group", mainGroup.getName())));
        this.controllerExecutorConfig.getProcessGroups().forEach(processGroup -> System.out.println(LanguageManager.get("loading-process-group", processGroup.getName())));
    }

    private void loadCommands() {
        this.commandManager
                .register(new CommandProcess(target -> {
                    ReferencedOptional<PacketSender> optional = DefaultChannelManager.INSTANCE.get(target.getParent());
                    optional.ifPresent(packetSender -> packetSender.sendPacket(new ControllerPacketOutToggleScreen(target.getProcessUniqueID())));
                    return optional.isPresent();
                }, e -> DefaultChannelManager.INSTANCE.get(e.getParent()).ifPresent(packetSender -> packetSender.sendPacket(
                        new ControllerPacketOutCopyProcess(e.getProcessUniqueID())
                ))))
                .register(new CommandDump(new DefaultDumpUtil()))
                .register(new CommandLaunch())
                .register(new CommandStop())
                .register(new CommandCreate())
                .register(new CommandReload(this))
                .register(new CommandClear(loggerBase))
                .register(new CommandHelp(commandManager));
    }

    private void loadPacketHandlers() {
        new Reflections("systems.reformcloud.reformcloud2.executor.controller.packet.in").getSubTypesOf(DefaultJsonNetworkHandler.class).forEach(packetHandler::registerHandler);
    }
}
