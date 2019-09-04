package de.klaro.reformcloud2.executor.client;

import de.klaro.reformcloud2.executor.api.ExecutorType;
import de.klaro.reformcloud2.executor.api.client.Client;
import de.klaro.reformcloud2.executor.api.client.process.ProcessManager;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import de.klaro.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import de.klaro.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import de.klaro.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import de.klaro.reformcloud2.executor.api.common.commands.basic.manager.DefaultCommandManager;
import de.klaro.reformcloud2.executor.api.common.commands.manager.CommandManager;
import de.klaro.reformcloud2.executor.api.common.commands.source.CommandSource;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.logger.LoggerBase;
import de.klaro.reformcloud2.executor.api.common.logger.coloured.ColouredLoggerHandler;
import de.klaro.reformcloud2.executor.api.common.logger.other.DefaultLoggerHandler;
import de.klaro.reformcloud2.executor.api.common.network.auth.defaults.DefaultAuth;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import de.klaro.reformcloud2.executor.api.common.network.client.NetworkClient;
import de.klaro.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import de.klaro.reformcloud2.executor.api.common.utility.StringUtil;
import de.klaro.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import de.klaro.reformcloud2.executor.client.config.ClientConfig;
import de.klaro.reformcloud2.executor.client.config.ClientExecutorConfig;
import de.klaro.reformcloud2.executor.client.process.ProcessQueue;
import de.klaro.reformcloud2.executor.client.process.basic.DefaultProcessManager;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.function.Consumer;

public final class ClientExecutor extends Client {

    private static ClientExecutor instance;

    private static volatile boolean running = false;

    private LoggerBase loggerBase;

    private ClientConfig clientConfig;

    private DefaultClientRuntimeInformation clientRuntimeInformation;

    private ClientExecutorConfig clientExecutorConfig;

    private final CommandManager commandManager = new DefaultCommandManager();

    private final CommandSource console = new ConsoleCommandSource(commandManager);

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final ProcessManager processManager = new DefaultProcessManager();

    private final ProcessQueue processQueue = new ProcessQueue();

    ClientExecutor() {
        ExecutorAPI.setInstance(this);
        super.type = ExecutorType.CLIENT;

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    shutdown();
                } catch (final Exception ex) {
                    ex.printStackTrace();
                }
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

        this.networkClient.connect(
                clientExecutorConfig.getClientConnectionConfig().getHost(),
                clientExecutorConfig.getClientConnectionConfig().getPort(),
                new DefaultAuth(
                        clientExecutorConfig.getConnectionKey(),
                        null,
                        true,
                        clientConfig.getName(),
                        new JsonConfiguration().add("info", clientRuntimeInformation)
                ), networkChannelReader
        );

        running = true;
        System.out.println(LanguageManager.get("startup-done", Long.toString(System.currentTimeMillis() - current)));
        runConsole();
    }

    private void registerNetworkHandlers() {
        new Reflections("de.klaro.reformcloud2.executor.client.packet.in").getSubTypesOf(NetworkHandler.class).forEach(new Consumer<Class<? extends NetworkHandler>>() {
            @Override
            public void accept(Class<? extends NetworkHandler> aClass) {
                packetHandler.registerHandler(aClass);
            }
        });
    }

    @Override
    public void reload() throws Exception {

    }

    @Override
    public void shutdown() throws Exception {
        this.networkClient.disconnect();
        processQueue.interrupt();
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
                    commandManager.dispatchCommand(console, AllowedCommandSources.ALL, line, new Consumer<String>() {
                        @Override
                        public void accept(String s) {
                            System.out.println(s);
                        }
                    });
                }
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
