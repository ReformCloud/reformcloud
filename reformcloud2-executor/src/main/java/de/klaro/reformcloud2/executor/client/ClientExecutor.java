package de.klaro.reformcloud2.executor.client;

import de.klaro.reformcloud2.executor.api.ExecutorType;
import de.klaro.reformcloud2.executor.api.client.Client;
import de.klaro.reformcloud2.executor.api.client.process.ProcessManager;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalEventBusHandler;
import de.klaro.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import de.klaro.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import de.klaro.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import de.klaro.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import de.klaro.reformcloud2.executor.api.common.commands.basic.commands.CommandClear;
import de.klaro.reformcloud2.executor.api.common.commands.basic.commands.CommandHelp;
import de.klaro.reformcloud2.executor.api.common.commands.basic.commands.CommandReload;
import de.klaro.reformcloud2.executor.api.common.commands.basic.commands.CommandStop;
import de.klaro.reformcloud2.executor.api.common.commands.basic.manager.DefaultCommandManager;
import de.klaro.reformcloud2.executor.api.common.commands.manager.CommandManager;
import de.klaro.reformcloud2.executor.api.common.commands.source.CommandSource;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.event.EventManager;
import de.klaro.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.logger.LoggerBase;
import de.klaro.reformcloud2.executor.api.common.logger.coloured.ColouredLoggerHandler;
import de.klaro.reformcloud2.executor.api.common.logger.other.DefaultLoggerHandler;
import de.klaro.reformcloud2.executor.api.common.network.auth.defaults.DefaultAuth;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import de.klaro.reformcloud2.executor.api.common.network.client.NetworkClient;
import de.klaro.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.StringUtil;
import de.klaro.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import de.klaro.reformcloud2.executor.api.common.utility.system.SystemHelper;
import de.klaro.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import de.klaro.reformcloud2.executor.client.config.ClientConfig;
import de.klaro.reformcloud2.executor.client.config.ClientExecutorConfig;
import de.klaro.reformcloud2.executor.client.packet.out.ClientPacketOutNotifyRuntimeUpdate;
import de.klaro.reformcloud2.executor.client.process.ProcessQueue;
import de.klaro.reformcloud2.executor.client.process.basic.DefaultProcessManager;
import de.klaro.reformcloud2.executor.client.screen.ScreenManager;
import de.klaro.reformcloud2.executor.client.watchdog.WatchdogThread;
import org.reflections.Reflections;

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

    private final CommandManager commandManager = new DefaultCommandManager();

    private final CommandSource console = new ConsoleCommandSource(commandManager);

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final ProcessManager processManager = new DefaultProcessManager();

    private final ProcessQueue processQueue = new ProcessQueue();

    private final ScreenManager screenManager = new ScreenManager();

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

        try {
            if (Boolean.getBoolean("reformcloud2.disable.colours")) {
                this.loggerBase = new DefaultLoggerHandler();
            } else {
                this.loggerBase = new ColouredLoggerHandler();
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        DownloadHelper.downloadAndDisconnect(StringUtil.RUNNER_DOWNLOAD_URL, "reformcloud/files/runner.jar");

        this.clientExecutorConfig = new ClientExecutorConfig();
        this.clientConfig = clientExecutorConfig.getClientConfig();
        this.clientRuntimeInformation = new DefaultClientRuntimeInformation(
                clientConfig.getStartHost(),
                clientConfig.getMaxMemory(),
                clientConfig.getMaxProcesses(),
                clientConfig.getName()
        );

        registerNetworkHandlers();
        registerDefaultCommands();
        new ExternalEventBusHandler(
                packetHandler, new DefaultEventManager()
        );

        this.watchdogThread = new WatchdogThread();

        doConnect();
        running = true;
        System.out.println(LanguageManager.get("startup-done", Long.toString(System.currentTimeMillis() - current)));
        runConsole();
    }

    private void registerNetworkHandlers() {
        new Reflections("de.klaro.reformcloud2.executor.client.packet.in").getSubTypesOf(NetworkHandler.class).forEach(packetHandler::registerHandler);
    }

    private void registerDefaultCommands() {
        commandManager
                .register(CommandStop.class)
                .register(new CommandReload(this))
                .register(new CommandClear(loggerBase))
                .register(new CommandHelp(commandManager));
    }

    @Override
    public void reload() {
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

        SystemHelper.deleteDirectory(Paths.get("reformcloud/temp"));
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

    public LoggerBase getLoggerBase() {
        return loggerBase;
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

    public static boolean isRunning() {
        return running;
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

    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
    }

    public ClientRuntimeInformation getClientRuntimeInformation() {
        return clientRuntimeInformation;
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
            // Returns if the cloud means that the connection is already open
            return;
        }

        AtomicInteger atomicInteger = new AtomicInteger(0);
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

            AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 500);
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
            AbsoluteThread.sleep(TimeUnit.SECONDS, 5);
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
                        true,
                        clientConfig.getName(),
                        new JsonConfiguration().add("info", clientRuntimeInformation)
                ), createChannelReader(() -> {
                    processManager.stopAll();
                    AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 500);

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
