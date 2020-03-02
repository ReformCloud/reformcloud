package systems.reformcloud.reformcloud2.executor.client;

import org.reflections.Reflections;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.client.Client;
import systems.reformcloud.reformcloud2.executor.api.client.process.ProcessManager;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
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
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackendManager;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.ColouredLoggerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.logger.other.DefaultLoggerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.NetworkType;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.defaults.DefaultAuth;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.ProxiedChannelMessageHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.client.config.ClientConfig;
import systems.reformcloud.reformcloud2.executor.client.config.ClientExecutorConfig;
import systems.reformcloud.reformcloud2.executor.client.dump.ClientDumpUtil;
import systems.reformcloud.reformcloud2.executor.client.packet.out.ClientPacketOutNotifyRuntimeUpdate;
import systems.reformcloud.reformcloud2.executor.client.process.ProcessQueue;
import systems.reformcloud.reformcloud2.executor.client.process.basic.DefaultProcessManager;
import systems.reformcloud.reformcloud2.executor.client.screen.ScreenManager;
import systems.reformcloud.reformcloud2.executor.client.watchdog.WatchdogThread;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class ClientExecutor extends Client {

    private static ClientExecutor instance;

    private static volatile boolean running = false;

    private LoggerBase loggerBase;

    private ClientConfig clientConfig;

    private DefaultClientRuntimeInformation clientRuntimeInformation;

    private ClientExecutorConfig clientExecutorConfig;

    private WatchdogThread watchdogThread;

    private ScreenManager screenManager;

    private final CommandManager commandManager = new DefaultCommandManager();

    private final CommandSource console = new ConsoleCommandSource(commandManager);

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final ProcessManager processManager = new DefaultProcessManager();

    private final ProcessQueue processQueue = new ProcessQueue();

    private final ApplicationLoader applicationLoader = new DefaultApplicationLoader();

    private static final AtomicBoolean GLOBAL_CONNECTION_STATUS = new AtomicBoolean(false);

    ClientExecutor() {
        ExecutorAPI.setInstance(this);
        super.type = ExecutorType.CLIENT;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                shutdown();
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }));

        bootstrap();
    }

    @Override
    protected void bootstrap() {
        long current = System.currentTimeMillis();
        instance = this;

        SystemHelper.deleteDirectory(Paths.get("reformcloud/temp"));

        try {
            if (Boolean.getBoolean("reformcloud2.disable.colours")) {
                this.loggerBase = new DefaultLoggerHandler(this.commandManager);
            } else {
                this.loggerBase = new ColouredLoggerHandler(this.commandManager);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        this.clientExecutorConfig = new ClientExecutorConfig();
        this.clientConfig = clientExecutorConfig.getClientConfig();
        this.clientRuntimeInformation = new DefaultClientRuntimeInformation(
                clientConfig.getStartHost(),
                clientConfig.getMaxMemory(),
                clientConfig.getMaxProcesses(),
                clientConfig.getName()
        );

        applicationLoader.detectApplications();
        applicationLoader.installApplications();

        TemplateBackendManager.registerDefaults();

        registerNetworkHandlers();
        registerDefaultCommands();
        new ExternalEventBusHandler(
                packetHandler, new DefaultEventManager()
        );

        applicationLoader.loadApplications();

        this.watchdogThread = new WatchdogThread();
        this.screenManager = new ScreenManager();

        doConnect();

        applicationLoader.enableApplications();

        running = true;
        System.out.println(LanguageManager.get("startup-done", Long.toString(System.currentTimeMillis() - current)));
        runConsole();
    }

    private void registerNetworkHandlers() {
        new Reflections("systems.reformcloud.reformcloud2.executor.client.packet.in").getSubTypesOf(DefaultJsonNetworkHandler.class).forEach(packetHandler::registerHandler);
        this.packetHandler.registerHandler(new ProxiedChannelMessageHandler());
    }

    private void registerDefaultCommands() {
        commandManager
                .register(new CommandDump(new ClientDumpUtil()))
                .register(CommandStop.class)
                .register(new CommandReload(this))
                .register(new CommandClear(loggerBase))
                .register(new CommandHelp(commandManager));
    }

    @Override
    public void reload() {
        this.applicationLoader.disableApplications();

        this.clientExecutorConfig = new ClientExecutorConfig();
        this.clientConfig = clientExecutorConfig.getClientConfig();
        this.clientRuntimeInformation = new DefaultClientRuntimeInformation(
                clientConfig.getStartHost(),
                clientConfig.getMaxMemory(),
                clientConfig.getMaxProcesses(),
                clientConfig.getName()
        );
        this.packetHandler.clearHandlers();
        this.packetHandler.getQueryHandler().clearQueries();
        this.commandManager.unregisterAll();

        registerDefaultCommands();
        registerNetworkHandlers();

        this.applicationLoader.detectApplications();
        this.applicationLoader.installApplications();
        this.applicationLoader.loadApplications();
        this.applicationLoader.enableApplications();

        notifyUpdate();
    }

    @Override
    public void shutdown() {
        this.watchdogThread.interrupt();
        processQueue.interrupt();
        this.screenManager.interrupt();

        this.packetHandler.clearHandlers();
        this.packetHandler.getQueryHandler().clearQueries();
        this.networkClient.disconnect();

        this.processManager.stopAll();

        SystemHelper.deleteDirectory(Paths.get("reformcloud/temp"));

        this.applicationLoader.disableApplications();
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public NetworkClient getNetworkClient() {
        return networkClient;
    }

    @Override
    protected PacketHandler packetHandler() {
        return packetHandler;
    }

    @Nonnull
    @Override
    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public LoggerBase getLoggerBase() {
        return loggerBase;
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

    public static ClientExecutor getInstance() {
        return instance;
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public ClientExecutorConfig getClientExecutorConfig() {
        return clientExecutorConfig;
    }

    @Nonnull
    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
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

    private void notifyUpdate() {
        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> {
            DefaultClientRuntimeInformation information = new DefaultClientRuntimeInformation(
                    clientConfig.getStartHost(),
                    clientConfig.getMaxMemory(),
                    clientConfig.getMaxProcesses(),
                    clientConfig.getName()
            );

            packetSender.sendPacket(new ClientPacketOutNotifyRuntimeUpdate(information));
        });
    }

    private void doConnect() {
        if (GLOBAL_CONNECTION_STATUS.get()) {
            // All connections are already open and ready ( prevents abuse )
            return;
        }

        AtomicInteger atomicInteger = new AtomicInteger(1);
        boolean isConnected = false;
        while (atomicInteger.get() <= 10) {
            System.out.println(LanguageManager.get(
                    "network-client-try-connect",
                    clientExecutorConfig.getClientConnectionConfig().getHost(),
                    Integer.toString(clientExecutorConfig.getClientConnectionConfig().getPort()),
                    atomicInteger.getAndIncrement()
            ));
            isConnected = tryConnect();
            if (isConnected) {
                break;
            }

            AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 100);
        }

        if (isConnected) {
            System.out.println(LanguageManager.get(
                    "network-client-connect-success",
                    clientExecutorConfig.getClientConnectionConfig().getHost(),
                    Integer.toString(clientExecutorConfig.getClientConnectionConfig().getPort()),
                    atomicInteger.get()
            ));
            GLOBAL_CONNECTION_STATUS.set(true);
        } else {
            System.out.println(LanguageManager.get(
                    "network-client-connection-refused",
                    clientExecutorConfig.getClientConnectionConfig().getHost(),
                    Integer.toString(clientExecutorConfig.getClientConnectionConfig().getPort())
            ));
            AbsoluteThread.sleep(TimeUnit.SECONDS, 1);
            System.exit(-1);
        }
    }

    private boolean tryConnect() {
        return this.networkClient.connect(
                clientExecutorConfig.getClientConnectionConfig().getHost(),
                clientExecutorConfig.getClientConnectionConfig().getPort(),
                new DefaultAuth(
                        clientExecutorConfig.getConnectionKey(),
                        null,
                        NetworkType.CLIENT,
                        clientConfig.getName(),
                        new JsonConfiguration().add("info", clientRuntimeInformation)
                ), createChannelReader(() -> {
                    processManager.stopAll();
                    AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 100);

                    if (GLOBAL_CONNECTION_STATUS.get()) {
                        GLOBAL_CONNECTION_STATUS.set(false);
                        System.out.println(LanguageManager.get("network-client-connection-lost"));
                        doConnect();
                    }
                })
        );
    }

    @Override
    public ProcessInformation getThisProcessInformation() {
        return null;
    }
}
