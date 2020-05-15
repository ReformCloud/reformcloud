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
package systems.reformcloud.reformcloud2.executor.controller;

import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.PacketAPIProcessCopyByName;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.PacketAPIProcessCopyByUniqueID;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.*;
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
import systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.rethinkdb.RethinkDBDatabase;
import systems.reformcloud.reformcloud2.executor.api.common.database.config.DatabaseConfig;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import systems.reformcloud.reformcloud2.executor.api.common.groups.task.OnlinePercentCheckerTask;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.ColouredLoggerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.logger.other.DefaultLoggerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client.PacketOutClientChallengeRequest;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client.PacketOutClientChallengeResponse;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared.ServerChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared.SharedChallengeProvider;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.DefaultNetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.NetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.basic.DefaultWebServerAuth;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.http.server.DefaultWebServer;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.http.server.WebServer;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults.DefaultRequestListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.user.WebUser;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.api.controller.Controller;
import systems.reformcloud.reformcloud2.executor.api.controller.process.ProcessManager;
import systems.reformcloud.reformcloud2.executor.client.network.packet.ControllerPacketToggleScreen;
import systems.reformcloud.reformcloud2.executor.controller.api.GeneralAPI;
import systems.reformcloud.reformcloud2.executor.controller.api.console.ConsoleAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.database.DatabaseAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.group.GroupAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.message.ChannelMessageAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.player.PlayerAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.api.process.ProcessAPIImplementation;
import systems.reformcloud.reformcloud2.executor.controller.commands.CommandClients;
import systems.reformcloud.reformcloud2.executor.controller.config.ControllerConfig;
import systems.reformcloud.reformcloud2.executor.controller.config.ControllerExecutorConfig;
import systems.reformcloud.reformcloud2.executor.controller.network.channel.ControllerNetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.controller.network.channel.ControllerNetworkSuccessHandler;
import systems.reformcloud.reformcloud2.executor.controller.network.packet.handler.PacketInAPILogoutPlayer;
import systems.reformcloud.reformcloud2.executor.controller.network.packet.handler.PacketInAPIPlayerCommandExecute;
import systems.reformcloud.reformcloud2.executor.controller.network.packet.handler.PacketInAPIPlayerLoggedIn;
import systems.reformcloud.reformcloud2.executor.controller.network.packet.handler.PacketInAPIServerSwitchPlayer;
import systems.reformcloud.reformcloud2.executor.controller.process.ClientManager;
import systems.reformcloud.reformcloud2.executor.controller.process.DefaultProcessManager;
import systems.reformcloud.reformcloud2.executor.controller.process.startup.AutoStartupHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public final class ControllerExecutor extends Controller {

    private static ControllerExecutor instance;

    private static volatile boolean running = false;
    private final CommandManager commandManager = new DefaultCommandManager();
    private final CommandSource console = new ConsoleCommandSource(commandManager);
    private final ApplicationLoader applicationLoader = new DefaultApplicationLoader();
    private final NetworkServer networkServer = new DefaultNetworkServer();
    private final WebServer webServer = new DefaultWebServer();
    private final PacketHandler packetHandler = new DefaultPacketHandler();
    private final ProcessManager processManager = new DefaultProcessManager();
    private final DatabaseConfig databaseConfig = new DatabaseConfig();
    private final EventManager eventManager = new DefaultEventManager();
    private LoggerBase loggerBase;
    private AutoStartupHandler autoStartupHandler;
    private ControllerExecutorConfig controllerExecutorConfig;
    private ControllerConfig controllerConfig;
    private Database<?> database;
    private RequestListenerHandler requestListenerHandler;
    private SyncAPI syncAPI;
    private AsyncAPI asyncAPI;

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

    @NotNull
    public static ControllerExecutor getInstance() {
        if (instance == null) {
            return (ControllerExecutor) Controller.getInstance();
        }

        return instance;
    }

    @Override
    protected void bootstrap() {
        long current = System.currentTimeMillis();
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

            case RETHINK_DB: {
                this.database = new RethinkDBDatabase();
                this.databaseConfig.connect(this.database);
                break;
            }
        }

        GeneralAPI generalAPI = new GeneralAPI(
                new ConsoleAPIImplementation(this.commandManager),
                new DatabaseAPIImplementation(this.database),
                new GroupAPIImplementation(),
                new PlayerAPIImplementation(this.processManager),
                new ProcessAPIImplementation(this.processManager),
                new ChannelMessageAPIImplementation()
        );
        this.syncAPI = generalAPI;
        this.asyncAPI = generalAPI;

        this.requestListenerHandler = new DefaultRequestListenerHandler(new DefaultWebServerAuth(this.getSyncAPI().getDatabaseSyncAPI()));

        applicationLoader.detectApplications();
        applicationLoader.installApplications();

        this.controllerConfig = controllerExecutorConfig.getControllerConfig();
        this.controllerConfig.getNetworkListener().forEach(e -> e.forEach((host, port) -> ControllerExecutor.this.networkServer.bind(
                host,
                port,
                () -> new ControllerNetworkChannelReader(),
                new ServerChallengeAuthHandler(new SharedChallengeProvider(this.controllerExecutorConfig.getConnectionKey()), new ControllerNetworkSuccessHandler())
        )));

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

    @Override
    public NetworkServer getNetworkServer() {
        return networkServer;
    }

    @NotNull
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

    @NotNull
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
                    ReferencedOptional<PacketSender> optional = DefaultChannelManager.INSTANCE.get(target.getProcessDetail().getParentName());
                    optional.ifPresent(packetSender -> packetSender.sendPacket(new ControllerPacketToggleScreen(target.getProcessDetail().getProcessUniqueID())));
                    return optional.isPresent();
                }))
                .register(new CommandClients())
                .register(new CommandPlayers())
                .register(new CommandGroup())
                .register(new CommandDump(new DefaultDumpUtil()))
                .register(new CommandLaunch())
                .register(new CommandStop())
                .register(new CommandCreate())
                .register(new CommandReload(this))
                .register(new CommandClear(loggerBase))
                .register(new CommandHelp(commandManager));
    }

    private void loadPacketHandlers() {
        new Reflections("systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api")
                .getSubTypesOf(Packet.class)
                .forEach(e -> {
                    if (e.getSimpleName().equals("PacketAPIProcessCopy") || e.getSimpleName().equals("QueryResultPacket")) {
                        return;
                    }

                    this.packetHandler.registerHandler(e);
                });

        // Copy api
        this.packetHandler.registerHandler(PacketAPIProcessCopyByName.class);
        this.packetHandler.registerHandler(PacketAPIProcessCopyByUniqueID.class);

        new Reflections("systems.reformcloud.reformcloud2.executor.controller.network.packet")
                .getSubTypesOf(Packet.class)
                .forEach(packetHandler::registerHandler);

        // API -> Controller handler
        this.packetHandler.registerHandler(PacketInAPILogoutPlayer.class);
        this.packetHandler.registerHandler(PacketInAPIPlayerCommandExecute.class);
        this.packetHandler.registerHandler(PacketInAPIPlayerLoggedIn.class);
        this.packetHandler.registerHandler(PacketInAPIServerSwitchPlayer.class);

        // Auth
        this.packetHandler.registerHandler(PacketOutClientChallengeRequest.class);
        this.packetHandler.registerHandler(PacketOutClientChallengeResponse.class);
    }

    public void handleChannelDisconnect(PacketSender packetSender) {
        ClientRuntimeInformation clientRuntimeInformation = ClientManager.INSTANCE.getClientRuntimeInformation()
                .stream()
                .filter(e -> e.getName().equals(packetSender.getName()))
                .findFirst()
                .orElse(null);
        if (clientRuntimeInformation != null) {
            ClientManager.INSTANCE.disconnectClient(clientRuntimeInformation.getName());
        } else {
            this.processManager.onChannelClose(packetSender.getName());
        }
    }
}
