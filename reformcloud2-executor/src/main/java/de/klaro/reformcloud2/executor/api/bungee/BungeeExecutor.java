package de.klaro.reformcloud2.executor.api.bungee;

import de.klaro.reformcloud2.executor.api.api.API;
import de.klaro.reformcloud2.executor.api.bungee.event.ConnectHandler;
import de.klaro.reformcloud2.executor.api.bungee.event.ProcessEventHandler;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalEventBusHandler;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.event.EventManager;
import de.klaro.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import de.klaro.reformcloud2.executor.api.common.event.handler.Listener;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Version;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

public final class BungeeExecutor extends API {

    private static BungeeExecutor instance;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private final Plugin plugin;

    private ProcessInformation thisProcessInformation;

    private static boolean WATERDOG;

    BungeeExecutor(Plugin plugin) {
        this.plugin = plugin;
        instance = this;
        new ExternalEventBusHandler(packetHandler, new DefaultEventManager());
        getEventManager().registerListener(new ProcessEventHandler());
        getEventManager().registerListener(this);

        String connectionKey = JsonConfiguration.read("reformcloud/.connection/key.json").getString("key");
        SystemHelper.deleteFile(new File("reformcloud/.connection/key.json"));
        JsonConfiguration connectionConfig = JsonConfiguration.read("reformcloud/.connection/connection.json");

        this.thisProcessInformation = connectionConfig.get("startInfo", ProcessInformation.TYPE);
        WATERDOG = thisProcessInformation.getTemplate().getVersion().equals(Version.WATERDOG)
                || thisProcessInformation.getTemplate().getVersion().equals(Version.WATERDOG_PE);

        this.networkClient.connect(
                connectionConfig.getString("controller-host"),
                connectionConfig.getInteger("controller-port"),
                new DefaultAuth(
                        connectionKey,
                        thisProcessInformation.getParent(),
                        false,
                        thisProcessInformation.getName(),
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
    public PacketHandler packetHandler() {
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
                ProxyServer.getInstance().getReconnectHandler().close();
                ProxyServer.getInstance().getPluginManager().registerListener(plugin, new ConnectHandler());
            }
        });
    }

    public static void registerServer(ProcessInformation processInformation) {
        if (!ProxyServer.getInstance().getServers().containsKey(processInformation.getName())
                && processInformation.getNetworkInfo().isConnected()
                && processInformation.getTemplate().isServer()) {
            ProxyServer.getInstance().getServers().put(
                    processInformation.getName(),
                    constructServerInfo(processInformation)
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

    private static ServerInfo constructServerInfo(ProcessInformation processInformation) {
        if (WATERDOG) {
            try {
                Method method = ProxyServer.class.getMethod("constructServerInfo",
                        String.class, InetSocketAddress.class, String.class, boolean.class, boolean.class, String.class);
                method.setAccessible(true);
                return (ServerInfo) method.invoke(
                        ProxyServer.getInstance(),
                        processInformation.getName(),
                        new InetSocketAddress(processInformation.getNetworkInfo().getHost(), processInformation.getNetworkInfo().getPort()),
                        "ReformCloud2",
                        false,
                        processInformation.getTemplate().getVersion().getId() == 3,
                        "default"
                );
            } catch (final InvocationTargetException | IllegalAccessException | NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }

        return ProxyServer.getInstance().constructServerInfo(
                processInformation.getName(),
                new InetSocketAddress(
                        processInformation.getNetworkInfo().getHost(),
                        processInformation.getNetworkInfo().getPort()
                ), "ReformCloud2", false
        );
    }

    @Override
    public ProcessInformation getThisProcessInformation() {
        return thisProcessInformation;
    }

    @Listener
    public void handleThisUpdate(final ProcessUpdatedEvent event) {
        if (event.getProcessInformation().getProcessUniqueID().equals(thisProcessInformation.getProcessUniqueID())) {
            thisProcessInformation = event.getProcessInformation();
        }
    }
}
