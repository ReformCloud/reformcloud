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
package systems.reformcloud.reformcloud2.executor.client;

import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.client.Client;
import systems.reformcloud.reformcloud2.executor.api.client.process.ProcessManager;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.dump.CommandDump;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.shared.CommandClear;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.shared.CommandHelp;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.shared.CommandReload;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.shared.CommandStop;
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
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared.ClientChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.client.config.ClientConfig;
import systems.reformcloud.reformcloud2.executor.client.config.ClientExecutorConfig;
import systems.reformcloud.reformcloud2.executor.client.dump.ClientDumpUtil;
import systems.reformcloud.reformcloud2.executor.client.network.channel.ClientNetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.client.process.ProcessQueue;
import systems.reformcloud.reformcloud2.executor.client.process.basic.DefaultProcessManager;
import systems.reformcloud.reformcloud2.executor.client.process.listeners.RunningProcessPreparedListener;
import systems.reformcloud.reformcloud2.executor.client.process.listeners.RunningProcessScreenListener;
import systems.reformcloud.reformcloud2.executor.client.process.listeners.RunningProcessStoppedListener;
import systems.reformcloud.reformcloud2.executor.controller.network.packet.ClientPacketNotifyRuntimeUpdate;

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
        super.loadPacketHandlers();

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
            if (Boolean.getBoolean("reformcloud.disable.colours")) {
                this.loggerBase = new DefaultLoggerHandler(this.commandManager);
            } else {
                this.loggerBase = new ColouredLoggerHandler(this.commandManager);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        new ExternalEventBusHandler(
                packetHandler, new DefaultEventManager()
        );

        ExecutorAPI.getInstance().getEventManager().registerListener(new RunningProcessPreparedListener());
        ExecutorAPI.getInstance().getEventManager().registerListener(new RunningProcessStoppedListener());
        ExecutorAPI.getInstance().getEventManager().registerListener(new RunningProcessScreenListener());

        this.clientExecutorConfig = new ClientExecutorConfig();
        this.clientConfig = clientExecutorConfig.getClientConfig();
        this.clientRuntimeInformation = new DefaultClientRuntimeInformation(
                clientConfig.getStartHost(),
                clientConfig.getMaxMemory(),
                clientConfig.getMaxProcesses(),
                clientConfig.getName(),
                clientConfig.getUniqueID()
        );

        applicationLoader.detectApplications();
        applicationLoader.installApplications();

        TemplateBackendManager.registerDefaults();

        registerNetworkHandlers();
        registerDefaultCommands();

        applicationLoader.loadApplications();

        doConnect();

        applicationLoader.enableApplications();

        running = true;
        System.out.println(LanguageManager.get("startup-done", Long.toString(System.currentTimeMillis() - current)));
        runConsole();
    }

    private void registerNetworkHandlers() {
        new Reflections("systems.reformcloud.reformcloud2.executor.client.network.packet")
                .getSubTypesOf(Packet.class)
                .forEach(packetHandler::registerHandler);
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
                clientConfig.getName(),
                clientConfig.getUniqueID()
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
        this.processQueue.interrupt();

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

    @NotNull
    @Override
    public PacketHandler getPacketHandler() {
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

    @NotNull
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
                    clientConfig.getName(),
                    clientConfig.getUniqueID()
            );

            packetSender.sendPacket(new ClientPacketNotifyRuntimeUpdate(information));
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
                () -> new ClientNetworkChannelReader(),
                new ClientChallengeAuthHandler(
                        this.clientExecutorConfig.getConnectionKey(),
                        this.clientConfig.getName(),
                        () -> new JsonConfiguration().add("info", this.clientRuntimeInformation),
                        context -> {
                        } // unused here
                )
        );
    }

    public void handleDisconnect() {
        this.processManager.stopAll();
        AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 100);

        if (GLOBAL_CONNECTION_STATUS.get()) {
            GLOBAL_CONNECTION_STATUS.set(false);
            System.out.println(LanguageManager.get("network-client-connection-lost"));
            doConnect();
        }
    }
}
