package de.klaro.reformcloud2.executor.api.bungee;

import de.klaro.reformcloud2.executor.api.api.API;
import de.klaro.reformcloud2.executor.api.bungee.event.ProcessEventHandler;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalEventBusHandler;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.event.EventManager;
import de.klaro.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import de.klaro.reformcloud2.executor.api.common.network.auth.defaults.DefaultAuth;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import de.klaro.reformcloud2.executor.api.common.network.client.NetworkClient;
import de.klaro.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.system.SystemHelper;
import de.klaro.reformcloud2.executor.api.common.utility.task.Task;
import de.klaro.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

public final class BungeeExecutor extends API {

    private static BungeeExecutor instance;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient;

    private final Plugin plugin;

    BungeeExecutor(Plugin plugin) {
        this.plugin = plugin;
        instance = this;
        new ExternalEventBusHandler(packetHandler, new DefaultEventManager());
        getEventManager().registerListener(new ProcessEventHandler());

        String connectionKey = JsonConfiguration.read("reformcloud/.connection/key.json").getString("key");
        SystemHelper.deleteFile(new File("reformcloud/.connection/key.json"));
        JsonConfiguration connectionConfig = JsonConfiguration.read("reformcloud/.connection/connection.json");

        ProcessInformation startInfo = connectionConfig.get("startInfo", ProcessInformation.TYPE);

        this.networkClient = new DefaultNetworkClient();
        this.networkClient.connect(
                connectionConfig.getString("controller-host"),
                connectionConfig.getInteger("controller-port"),
                new DefaultAuth(
                        connectionKey,
                        startInfo.getParent(),
                        false,
                        startInfo.getName(),
                        new JsonConfiguration()
                ), networkChannelReader
        );
        awaitConnectionAndUpdate();
        ExecutorAPI.setInstance(this);
    }

    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
    }

    public Plugin getPlugin() {
        return plugin;
    }

    NetworkClient getNetworkClient() {
        return networkClient;
    }

    @Override
    protected PacketHandler packetHandler() {
        return packetHandler;
    }

    public static BungeeExecutor getInstance() {
        return instance;
    }

    // ===============
    static void clearHandlers() {
        ProxyServer.getInstance().getConfig().getListeners().forEach(new Consumer<ListenerInfo>() {
            @Override
            public void accept(ListenerInfo listenerInfo) {
                listenerInfo.getServerPriority().clear();
            }
        });
        ProxyServer.getInstance().getConfig().getServers().clear();
    }

    private void awaitConnectionAndUpdate() {
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                while (packetSender == null) {
                    packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                    AbsoluteThread.sleep(100);
                }

                AbsoluteThread.sleep(100);

                getAllProcesses().forEach(new Consumer<ProcessInformation>() {
                    @Override
                    public void accept(ProcessInformation processInformation) {
                        registerServer(processInformation);
                    }
                });
            }
        });
    }

    public static void registerServer(ProcessInformation processInformation) {
        if (!ProxyServer.getInstance().getServers().containsKey(processInformation.getName())
                && processInformation.getNetworkInfo().isConnected()) {
            ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(
                    processInformation.getName(),
                    new InetSocketAddress(
                            processInformation.getNetworkInfo().getHost(),
                            processInformation.getNetworkInfo().getPort()
                    ), "ReformCloud2", false
            );
            ProxyServer.getInstance().getServers().put(
                    processInformation.getName(),
                    serverInfo
            );

            if (processInformation.isLobby()) {
                ProxyServer.getInstance().getConfig().getListeners().forEach(new Consumer<ListenerInfo>() {
                    @Override
                    public void accept(ListenerInfo listenerInfo) {
                        listenerInfo.getServerPriority().add(processInformation.getName());
                    }
                });
            }
        }
    }
}
