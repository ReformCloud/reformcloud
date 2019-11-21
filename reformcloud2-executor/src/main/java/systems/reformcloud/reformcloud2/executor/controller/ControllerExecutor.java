package systems.reformcloud.reformcloud2.executor.controller;

import org.reflections.Reflections;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.DefaultCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.CommandClear;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.CommandHelp;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.CommandReload;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.CommandStop;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.dump.CommandDump;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.dump.basic.DefaultDumpUtil;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.manager.DefaultCommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.file.FileDatabase;
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.h2.H2Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mongo.MongoDatabase;
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mysql.MySQLDatabase;
import systems.reformcloud.reformcloud2.executor.api.common.database.config.DatabaseConfig;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.task.OnlinePercentCheckerTask;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
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
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.DefaultNetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.NetworkServer;
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
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.controller.Controller;
import systems.reformcloud.reformcloud2.executor.api.controller.process.ProcessManager;
import systems.reformcloud.reformcloud2.executor.controller.commands.CommandReformCloud;
import systems.reformcloud.reformcloud2.executor.controller.config.ControllerConfig;
import systems.reformcloud.reformcloud2.executor.controller.config.ControllerExecutorConfig;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.api.ControllerAPIAction;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.api.ControllerExecuteCommand;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.api.ControllerPluginAction;
import systems.reformcloud.reformcloud2.executor.controller.process.ClientManager;
import systems.reformcloud.reformcloud2.executor.controller.process.DefaultProcessManager;
import systems.reformcloud.reformcloud2.executor.controller.process.startup.AutoStartupHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public final class ControllerExecutor extends Controller {

    private static ControllerExecutor instance;

    private static volatile boolean running = false;

    private LoggerBase loggerBase;

    private AutoStartupHandler autoStartupHandler;

    private ControllerExecutorConfig controllerExecutorConfig;

    private ControllerConfig controllerConfig;

    private Database database;

    private RequestListenerHandler requestListenerHandler;

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
                this.loggerBase = new DefaultLoggerHandler();
            } else {
                this.loggerBase = new ColouredLoggerHandler();
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        this.controllerExecutorConfig = new ControllerExecutorConfig();
        this.requestListenerHandler = new DefaultRequestListenerHandler(new DefaultWebServerAuth(this));

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
                    if (!auth.key().equals(controllerExecutorConfig.getConnectionKey())) {
                        System.out.println(LanguageManager.get("network-channel-auth-failed", auth.getName()));
                        return new Double<>(auth.getName(), false);
                    }

                    if (auth.type().equals(NetworkType.CLIENT)) {
                        System.out.println(LanguageManager.get("client-connected", auth.getName()));
                        DefaultClientRuntimeInformation runtimeInformation = auth.extra().get("info", ClientRuntimeInformation.TYPE);
                        ClientManager.INSTANCE.connectClient(runtimeInformation);
                    } else {
                        ProcessInformation information = processManager.getProcess(auth.getName());
                        information.getNetworkInfo().setConnected(true);
                        information.setProcessState(ProcessState.READY);
                        processManager.update(information);

                        System.out.println(LanguageManager.get("process-connected", auth.getName(), auth.parent()));
                    }

                    System.out.println(LanguageManager.get("network-channel-auth-success", auth.getName(), auth.parent()));
                    return new Double<>(auth.getName(), true);
                }
        ))));

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

        applicationLoader.loadApplications();

        this.autoStartupHandler = new AutoStartupHandler();
        sendGroups();
        loadCommands();
        loadPacketHandlers();

        createDatabase("internal_users");
        if (controllerExecutorConfig.isFirstStartup()) {
            final String token = StringUtil.generateString(2);
            WebUser webUser = new WebUser("admin", token, Collections.singletonList("*"));
            insert("internal_users", webUser.getName(), "", new JsonConfiguration().add("user", webUser));

            System.out.println(LanguageManager.get("setup-created-default-user", webUser.getName(), token));
        }

        this.controllerExecutorConfig.getControllerConfig().getHttpNetworkListener().forEach(map -> map.forEach((host, port) -> {
                this.webServer.add(host, port, this.requestListenerHandler);
            })
        );

        applicationLoader.enableApplications();

        if (Files.exists(Paths.get("reformcloud/.client"))) {
            try {
                DownloadHelper.downloadAndDisconnect(StringUtil.RUNNER_DOWNLOAD_URL, "reformcloud/.client/runner.jar");
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

    public static ControllerExecutor getInstance() {
        if (instance == null) {
            return (ControllerExecutor) Controller.getInstance();
        }

        return instance;
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

    private void sendGroups() {
        this.controllerExecutorConfig.getMainGroups().forEach(mainGroup -> System.out.println(LanguageManager.get("loading-main-group", mainGroup.getName())));
        this.controllerExecutorConfig.getProcessGroups().forEach(processGroup -> System.out.println(LanguageManager.get("loading-process-group", processGroup.getName())));
    }

    private void loadCommands() {
        this.commandManager
                .register(new CommandDump(new DefaultDumpUtil()))
                .register(CommandStop.class)
                .register(new CommandReformCloud())
                .register(new CommandReload(this))
                .register(new CommandClear(loggerBase))
                .register(new CommandHelp(commandManager));
    }

    private void loadPacketHandlers() {
        new Reflections("systems.reformcloud.reformcloud2.executor.controller.packet.in").getSubTypesOf(NetworkHandler.class).forEach(packetHandler::registerHandler);
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
    public Task<Void> sendColouredLineAsync(@Nonnull String line) throws IllegalAccessException {
        if (!(loggerBase instanceof ColouredLoggerHandler)) {
            throw new IllegalAccessException();
        }

        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            try {
                sendColouredLine(line);
                task.complete(null);
            } catch (IllegalAccessException ignored) {
                //already catches above
            }
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> sendRawLineAsync(@Nonnull String line) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            loggerBase.logRaw(line);
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<String> dispatchCommandAndGetResultAsync(@Nonnull String commandLine) {
        Task<String> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> commandManager.dispatchCommand(new DefaultCommandSource(task::complete, commandManager), AllowedCommandSources.ALL, commandLine, task::complete));
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
        if (!(loggerBase instanceof ColouredLoggerHandler)) {
            throw new IllegalAccessException();
        }

        loggerBase.log(line);
    }

    @Override
    public void sendRawLine(@Nonnull String line) {
        loggerBase.logRaw(line);
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
        return getCommand(name) != null;
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
            MainGroup mainGroup = new MainGroup(name, subgroups);
            task.complete(controllerExecutorConfig.createMainGroup(mainGroup));
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
        Task.EXECUTOR.execute(() -> task.complete(controllerExecutorConfig.createProcessGroup(processGroup)));
        return task;
    }

    @Nonnull
    @Override
    public Task<MainGroup> updateMainGroupAsync(@Nonnull MainGroup mainGroup) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            controllerExecutorConfig.updateMainGroup(mainGroup);
            task.complete(mainGroup);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> updateProcessGroupAsync(@Nonnull ProcessGroup processGroup) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            controllerExecutorConfig.updateProcessGroup(processGroup);
            task.complete(processGroup);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<MainGroup> getMainGroupAsync(@Nonnull String name) {
        Task<MainGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.filter(controllerExecutorConfig.getMainGroups(), mainGroup -> mainGroup.getName().equals(name))));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessGroup> getProcessGroupAsync(@Nonnull String name) {
        Task<ProcessGroup> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.filter(controllerExecutorConfig.getProcessGroups(), processGroup -> processGroup.getName().equals(name))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> deleteMainGroupAsync(@Nonnull String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Links.filterToReference(controllerExecutorConfig.getMainGroups(), mainGroup -> mainGroup.getName().equals(name)).ifPresent(mainGroup -> controllerExecutorConfig.deleteMainGroup(mainGroup));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> deleteProcessGroupAsync(@Nonnull String name) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Links.filterToReference(controllerExecutorConfig.getProcessGroups(), processGroup -> processGroup.getName().equals(name)).ifPresent(processGroup -> controllerExecutorConfig.deleteProcessGroup(processGroup));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<List<MainGroup>> getMainGroupsAsync() {
        Task<List<MainGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Collections.unmodifiableList(controllerExecutorConfig.getMainGroups())));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<ProcessGroup>> getProcessGroupsAsync() {
        Task<List<ProcessGroup>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Collections.unmodifiableList(controllerExecutorConfig.getProcessGroups())));
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
            ProcessInformation processInformation = ControllerExecutor.this.getPlayerOnProxy(player);
            if (processInformation != null) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerAPIAction(
                        ControllerAPIAction.APIAction.SEND_MESSAGE,
                        Arrays.asList(player, message)
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
            ProcessInformation processInformation = ControllerExecutor.this.getPlayerOnProxy(player);
            if (processInformation != null) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerAPIAction(
                        ControllerAPIAction.APIAction.KICK_PLAYER,
                        Arrays.asList(player, message)
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
            ProcessInformation processInformation = ControllerExecutor.this.getPlayerOnServer(player);
            if (processInformation != null) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerAPIAction(
                        ControllerAPIAction.APIAction.KICK_PLAYER,
                        Arrays.asList(player, message)
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
            ProcessInformation processInformation = ControllerExecutor.this.getPlayerOnServer(player);
            if (processInformation != null) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerAPIAction(
                        ControllerAPIAction.APIAction.PLAY_SOUND,
                        Arrays.asList(player, sound, f1, f2)
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
            ProcessInformation processInformation = ControllerExecutor.this.getPlayerOnProxy(player);
            if (processInformation != null) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerAPIAction(
                        ControllerAPIAction.APIAction.SEND_TITLE,
                        Arrays.asList(player, title, subTitle, fadeIn, stay, fadeOut)
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
            ProcessInformation processInformation = ControllerExecutor.this.getPlayerOnServer(player);
            if (processInformation != null) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerAPIAction(
                        ControllerAPIAction.APIAction.PLAY_ENTITY_EFFECT,
                        Arrays.asList(player, entityEffect)
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
            ProcessInformation processInformation = ControllerExecutor.this.getPlayerOnProxy(player);
            if (processInformation != null) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerAPIAction(
                        ControllerAPIAction.APIAction.PLAY_EFFECT,
                        Arrays.asList(player, effect, data)
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
            ProcessInformation processInformation = ControllerExecutor.this.getPlayerOnServer(player);
            if (processInformation != null) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerAPIAction(
                        ControllerAPIAction.APIAction.RESPAWN,
                        Collections.singletonList(player)
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
            ProcessInformation processInformation = ControllerExecutor.this.getPlayerOnServer(player);
            if (processInformation != null) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerAPIAction(
                        ControllerAPIAction.APIAction.LOCATION_TELEPORT,
                        Arrays.asList(player, world, x, y, z, yaw, pitch)
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
            ProcessInformation processInformation = ControllerExecutor.this.getPlayerOnProxy(player);
            if (processInformation != null) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerAPIAction(
                        ControllerAPIAction.APIAction.CONNECT,
                        Arrays.asList(player, server)
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
        ProcessInformation targetServer = getPlayerOnServer(target);
        return connectAsync(player, targetServer);
    }

    @Nonnull
    @Override
    public Task<Void> setResourcePackAsync(@Nonnull UUID player, @Nonnull String pack) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = ControllerExecutor.this.getPlayerOnServer(player);
            if (processInformation != null) {
                DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerAPIAction(
                        ControllerAPIAction.APIAction.SET_RESOURCE_PACK,
                        Arrays.asList(player, pack)
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
            DefaultChannelManager.INSTANCE.get(process).ifPresent(packetSender -> packetSender.sendPacket(new ControllerPluginAction(
                    ControllerPluginAction.Action.INSTALL,
                    new DefaultInstallablePlugin(
                            plugin.getDownloadURL(),
                            plugin.getName(),
                            plugin.version(),
                            plugin.author(),
                            plugin.main()
                    )
            )));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> installPluginAsync(@Nonnull ProcessInformation process, @Nonnull InstallablePlugin plugin) {
        return installPluginAsync(process.getName(), plugin);
    }

    @Nonnull
    @Override
    public Task<Void> unloadPluginAsync(@Nonnull String process, @Nonnull Plugin plugin) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            DefaultChannelManager.INSTANCE.get(process).ifPresent(packetSender -> packetSender.sendPacket(new ControllerPluginAction(
                    ControllerPluginAction.Action.UNINSTALL,
                    new DefaultPlugin(
                            plugin.version(),
                            plugin.author(),
                            plugin.main(),
                            plugin.depends(),
                            plugin.softpends(),
                            plugin.enabled(),
                            plugin.getName()
                    )
            )));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> unloadPluginAsync(@Nonnull ProcessInformation process, @Nonnull Plugin plugin) {
        return unloadPluginAsync(process.getName(), plugin);
    }

    @Nonnull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@Nonnull String process, @Nonnull String name) {
        Task<Plugin> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = getProcess(process);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            task.complete(Links.filter(processInformation.getPlugins(), defaultPlugin -> defaultPlugin.getName().equals(name)));
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Plugin> getInstalledPluginAsync(@Nonnull ProcessInformation process, @Nonnull String name) {
        return getInstalledPluginAsync(process.getName(), name);
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull String process, @Nonnull String author) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = getProcess(process);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            task.complete(Links.allOf(processInformation.getPlugins(), defaultPlugin -> defaultPlugin.author().equals(author)));
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull ProcessInformation process, @Nonnull String author) {
        return getPluginsAsync(process.getName(), author);
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull String process) {
        Task<Collection<DefaultPlugin>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = getProcess(process);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            task.complete(processInformation.getPlugins());
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Collection<DefaultPlugin>> getPluginsAsync(@Nonnull ProcessInformation processInformation) {
        return getPluginsAsync(processInformation.getName());
    }

    @Override
    public void installPlugin(@Nonnull String process, @Nonnull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public void installPlugin(@Nonnull ProcessInformation process, @Nonnull InstallablePlugin plugin) {
        installPluginAsync(process, plugin).awaitUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public void unloadPlugin(@Nonnull String process, @Nonnull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public void unloadPlugin(@Nonnull ProcessInformation process, @Nonnull Plugin plugin) {
        unloadPluginAsync(process, plugin).awaitUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public Plugin getInstalledPlugin(@Nonnull String process, @Nonnull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Override
    public Plugin getInstalledPlugin(@Nonnull ProcessInformation process, @Nonnull String name) {
        return getInstalledPluginAsync(process, name).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull String process, @Nonnull String author) {
        return getPluginsAsync(process, author).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull ProcessInformation process, @Nonnull String author) {
        return getPluginsAsync(process, author).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull String process) {
        return getPluginsAsync(process).getUninterruptedly(TimeUnit.SECONDS, 5);
    }

    @Nonnull
    @Override
    public Collection<DefaultPlugin> getPlugins(@Nonnull ProcessInformation processInformation) {
        return getPluginsAsync(processInformation).getUninterruptedly(TimeUnit.SECONDS, 5);
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
        Task.EXECUTOR.execute(() -> task.complete(processManager.startProcess(groupName, template, configurable)));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@Nonnull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.stopProcess(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> stopProcessAsync(@Nonnull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.stopProcess(uniqueID)));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getProcessAsync(@Nonnull String name) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getProcess(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getProcessAsync(@Nonnull UUID uniqueID) {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getProcess(uniqueID)));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<ProcessInformation>> getAllProcessesAsync() {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getAllProcesses()));
        return task;
    }

    @Nonnull
    @Override
    public Task<List<ProcessInformation>> getProcessesAsync(@Nonnull String group) {
        Task<List<ProcessInformation>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getProcesses(group)));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> executeProcessCommandAsync(@Nonnull String name, @Nonnull String commandLine) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation information = getProcess(name);
            DefaultChannelManager.INSTANCE.get(information.getParent()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerExecuteCommand(name, commandLine)));
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Integer> getGlobalOnlineCountAsync(@Nonnull Collection<String> ignoredProxies) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(processManager.getAllProcesses().stream().filter(processInformation -> !processInformation.getTemplate().isServer() && !ignoredProxies.contains(processInformation.getName())).mapToInt(ProcessInformation::getOnlineCount).sum()));
        return task;
    }

    @Nonnull
    @Override
    public Task<ProcessInformation> getThisProcessInformationAsync() {
        Task<ProcessInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(getThisProcessInformation()));
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
        this.processManager.update(processInformation);
    }

    private ProcessInformation getPlayerOnProxy(UUID uniqueID) {
        return Links.filter(processManager.getAllProcesses(), processInformation -> !processInformation.getTemplate().isServer() && Links.filterToReference(processInformation.getOnlinePlayers(), player -> player.getUniqueID().equals(uniqueID)).isPresent());
    }

    private ProcessInformation getPlayerOnServer(UUID uniqueID) {
        return Links.filter(processManager.getAllProcesses(), processInformation -> processInformation.getTemplate().isServer() && Links.filterToReference(processInformation.getOnlinePlayers(), player -> player.getUniqueID().equals(uniqueID)).isPresent());
    }

    @Nonnull
    @Override
    public Task<Boolean> isClientConnectedAsync(String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(Links.filterToReference(ClientManager.INSTANCE.getClientRuntimeInformation(), clientRuntimeInformation -> clientRuntimeInformation.getName().equals(name)).isPresent()));
        return task;
    }

    @Nonnull
    @Override
    public Task<String> getClientStartHostAsync(String name) {
        Task<String> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ClientRuntimeInformation information = Links.filter(ClientManager.INSTANCE.getClientRuntimeInformation(), clientRuntimeInformation -> clientRuntimeInformation.getName().equals(name));
            if (information == null) {
                task.complete(null);
            } else {
                task.complete(information.startHost());
            }
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Integer> getMaxMemoryAsync(String name) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ClientRuntimeInformation information = Links.filter(ClientManager.INSTANCE.getClientRuntimeInformation(), clientRuntimeInformation -> clientRuntimeInformation.getName().equals(name));
            if (information == null) {
                task.complete(null);
            } else {
                task.complete(information.maxMemory());
            }
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Integer> getMaxProcessesAsync(String name) {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ClientRuntimeInformation information = Links.filter(ClientManager.INSTANCE.getClientRuntimeInformation(), clientRuntimeInformation -> clientRuntimeInformation.getName().equals(name));
            if (information == null) {
                task.complete(null);
            } else {
                task.complete(information.maxProcessCount());
            }
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<ClientRuntimeInformation> getClientInformationAsync(String name) {
        Task<ClientRuntimeInformation> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ClientRuntimeInformation information = Links.filter(ClientManager.INSTANCE.getClientRuntimeInformation(), clientRuntimeInformation -> clientRuntimeInformation.getName().equals(name));
            task.complete(information);
        });
        return task;
    }

    @Override
    public boolean isClientConnected(@Nonnull String name) {
        return isClientConnectedAsync(name).getUninterruptedly();
    }

    @Override
    public String getClientStartHost(@Nonnull String name) {
        return getClientStartHostAsync(name).getUninterruptedly();
    }

    @Override
    public int getMaxMemory(@Nonnull String name) {
        return getMaxMemoryAsync(name).getUninterruptedly();
    }

    @Override
    public int getMaxProcesses(@Nonnull String name) {
        return getMaxProcessesAsync(name).getUninterruptedly();
    }

    @Override
    public ClientRuntimeInformation getClientInformation(@Nonnull String name) {
        return getClientInformationAsync(name).getUninterruptedly();
    }

    @Nonnull
    @Override
    public Task<JsonConfiguration> findAsync(@Nonnull String table, @Nonnull String key, String identifier) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            DatabaseReader databaseReader = ControllerExecutor.this.database.createForTable(table);
            JsonConfiguration result = databaseReader.find(key).getUninterruptedly();
            if (result != null) {
                task.complete(result);
            } else if (identifier != null) {
                task.complete(databaseReader.findIfAbsent(identifier).getUninterruptedly());
            } else {
                task.complete(null);
            }
        });
        return task;
    }

    @Nonnull
    @Override
    public <T> Task<T> findAsync(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull Function<JsonConfiguration, T> function) {
        Task<T> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            JsonConfiguration jsonConfiguration = findAsync(table, key, identifier).getUninterruptedly();
            if (jsonConfiguration != null) {
                task.complete(function.apply(jsonConfiguration));
            } else {
                task.complete(null);
            }
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> insertAsync(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull JsonConfiguration data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            database.createForTable(table).insert(key, identifier, data).awaitUninterruptedly();
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> updateAsync(@Nonnull String table, @Nonnull String key, @Nonnull JsonConfiguration newData) {
        return database.createForTable(table).update(key, newData);
    }

    @Nonnull
    @Override
    public Task<Boolean> updateIfAbsentAsync(@Nonnull String table, @Nonnull String identifier, @Nonnull JsonConfiguration newData) {
        return database.createForTable(table).updateIfAbsent(identifier, newData);
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
}
