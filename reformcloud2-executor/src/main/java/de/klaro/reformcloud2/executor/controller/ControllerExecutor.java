package de.klaro.reformcloud2.executor.controller;

import de.klaro.reformcloud2.executor.api.common.application.ApplicationLoader;
import de.klaro.reformcloud2.executor.api.common.application.basic.DefaultApplicationLoader;
import de.klaro.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import de.klaro.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import de.klaro.reformcloud2.executor.api.common.commands.basic.commands.CommandHelp;
import de.klaro.reformcloud2.executor.api.common.commands.basic.commands.CommandReload;
import de.klaro.reformcloud2.executor.api.common.commands.basic.commands.CommandStop;
import de.klaro.reformcloud2.executor.api.common.commands.basic.manager.DefaultCommandManager;
import de.klaro.reformcloud2.executor.api.common.commands.manager.CommandManager;
import de.klaro.reformcloud2.executor.api.common.commands.source.CommandSource;
import de.klaro.reformcloud2.executor.api.common.groups.MainGroup;
import de.klaro.reformcloud2.executor.api.common.groups.ProcessGroup;
import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.logger.LoggerBase;
import de.klaro.reformcloud2.executor.api.common.logger.coloured.ColouredLoggerHandler;
import de.klaro.reformcloud2.executor.api.common.logger.other.DefaultLoggerHandler;
import de.klaro.reformcloud2.executor.api.common.network.auth.Auth;
import de.klaro.reformcloud2.executor.api.common.network.auth.NetworkType;
import de.klaro.reformcloud2.executor.api.common.network.auth.defaults.DefaultAuth;
import de.klaro.reformcloud2.executor.api.common.network.auth.defaults.DefaultServerAuthHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import de.klaro.reformcloud2.executor.api.common.network.server.DefaultNetworkServer;
import de.klaro.reformcloud2.executor.api.common.network.server.NetworkServer;
import de.klaro.reformcloud2.executor.api.common.patch.Patcher;
import de.klaro.reformcloud2.executor.api.common.patch.basic.DefaultPatcher;
import de.klaro.reformcloud2.executor.api.common.utility.function.Double;
import de.klaro.reformcloud2.executor.api.common.utility.function.DoubleFunction;
import de.klaro.reformcloud2.executor.api.controller.Controller;
import de.klaro.reformcloud2.executor.api.controller.process.ProcessManager;
import de.klaro.reformcloud2.executor.controller.config.ControllerConfig;
import de.klaro.reformcloud2.executor.controller.config.ControllerExecutorConfig;
import de.klaro.reformcloud2.executor.controller.packet.in.PacketInClientAuthSuccess;
import de.klaro.reformcloud2.executor.controller.process.DefaultProcessManager;
import de.klaro.reformcloud2.executor.controller.process.startup.AutoStartupHandler;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class ControllerExecutor extends Controller {

    private static ControllerExecutor instance;

    private static volatile boolean running = false;

    private LoggerBase loggerBase;

    private AutoStartupHandler autoStartupHandler;

    private ControllerExecutorConfig controllerExecutorConfig = new ControllerExecutorConfig();

    private ControllerConfig controllerConfig;

    private final CommandManager commandManager = new DefaultCommandManager();

    private final CommandSource console = new ConsoleCommandSource(commandManager);

    private final ApplicationLoader applicationLoader = new DefaultApplicationLoader();

    private final NetworkServer networkServer = new DefaultNetworkServer();

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final ProcessManager processManager = new DefaultProcessManager();

    private final Patcher patcher = new DefaultPatcher();

    @Override
    protected void bootstrap() {
        long current = System.currentTimeMillis();
        instance = this;

        applicationLoader.detectApplications();
        applicationLoader.installApplications();

        try {
            if (Boolean.getBoolean("reformcloud2.disable.colours")) {
                this.loggerBase = new DefaultLoggerHandler();
            } else {
                this.loggerBase = new ColouredLoggerHandler();
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        this.controllerConfig = controllerExecutorConfig.getControllerConfig();
        this.controllerConfig.getNetworkListener().forEach(new Consumer<Map<String, Integer>>() {
            @Override
            public void accept(Map<String, Integer> stringIntegerMap) {
                stringIntegerMap.forEach(new BiConsumer<String, Integer>() {
                    @Override
                    public void accept(String s, Integer integer) {
                        ControllerExecutor.this.networkServer.bind(s, integer, new DefaultServerAuthHandler(
                                Controller.getInstance().networkChannelReader(),
                                new DoubleFunction<Packet, String, Boolean>() {
                                    @Override
                                    public Double<String, Boolean> apply(Packet packet) {
                                        DefaultAuth auth = packet.content().get("auth", Auth.TYPE);
                                        if (!auth.key().equals(controllerExecutorConfig.getConnectionKey())) {
                                            System.out.println(LanguageManager.get("network-channel-auth-failed", auth.getName()));
                                            return new Double<>(auth.getName(), false);
                                        }

                                        if (auth.type().equals(NetworkType.CLIENT)) {
                                            System.out.println(LanguageManager.get("client-connected", auth.getName()));
                                        } else {
                                            System.out.println(LanguageManager.get("process-connected", auth.getName(), auth.parent()));
                                        }

                                        System.out.println(LanguageManager.get("network-channel-auth-success", auth.getName(), auth.parent()));
                                        return new Double<>(auth.getName(), true);
                                    }
                                }
                        ));
                    }
                });
            }
        });

        applicationLoader.loadApplications();

        this.autoStartupHandler = new AutoStartupHandler();
        sendGroups();
        loadCommands();
        loadPacketHandlers();

        applicationLoader.enableApplications();

        running = true;
        System.out.println(LanguageManager.get("startup-done", Long.toString(System.currentTimeMillis() - current)));
        runConsole();
    }

    @Override
    public void reload() {
        final long current = System.currentTimeMillis();
        System.out.println(LanguageManager.get("runtime-try-reload"));

        this.applicationLoader.disableApplications();

        this.commandManager.unregisterAll();
        this.packetHandler.clearHandlers();
        this.packetHandler.getQueryHandler().clearQueries(); //Unsafe? May produce exception if query is sent but not handled after reload

        this.controllerExecutorConfig.getProcessGroups().clear();
        this.controllerExecutorConfig.getMainGroups().clear();

        this.applicationLoader.detectApplications();
        this.applicationLoader.installApplications();

        this.controllerExecutorConfig = null;
        this.controllerExecutorConfig = new ControllerExecutorConfig();

        this.autoStartupHandler.update(); //Update the automatic startup handler to re-sort the groups per priority

        this.controllerConfig = controllerExecutorConfig.getControllerConfig();

        this.applicationLoader.loadApplications();

        this.sendGroups();
        this.loadCommands();
        this.loadPacketHandlers();

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

        networkServer.closeAll(); //Close network first that all channels now that the controller is disconnecting

        autoStartupHandler.interrupt();

        controllerExecutorConfig.getProcessGroups().clear();
        controllerExecutorConfig.getMainGroups().clear();

        applicationLoader.disableApplications();
        loggerBase.close();
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

    @Override
    public PacketHandler getPacketHandler() {
        return packetHandler;
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

    public Patcher getPatcher() {
        return patcher;
    }

    private void runConsole() {
        String line;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                loggerBase.getConsoleReader().setPrompt("");
                loggerBase.getConsoleReader().resetPromptLine("", "", 0);

                while ((line = loggerBase.readLine()) != null && !line.trim().isEmpty() && running) {
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

    private void sendGroups() {
        this.controllerExecutorConfig.getMainGroups().forEach(new Consumer<MainGroup>() {
            @Override
            public void accept(MainGroup mainGroup) {
                System.out.println(LanguageManager.get("loading-main-group", mainGroup.getName()));
            }
        });
        this.controllerExecutorConfig.getProcessGroups().forEach(new Consumer<ProcessGroup>() {
            @Override
            public void accept(ProcessGroup processGroup) {
                System.out.println(LanguageManager.get("loading-process-group", processGroup.getName(), processGroup.getParentGroup()));
            }
        });
    }

    private void loadCommands() {
        this.commandManager
                .register(CommandStop.class)
                .register(new CommandReload(this))
                .register(new CommandHelp(commandManager));
    }

    private void loadPacketHandlers() {
        this.packetHandler.registerNetworkHandlers(
                new PacketInClientAuthSuccess()
        );
    }
}
