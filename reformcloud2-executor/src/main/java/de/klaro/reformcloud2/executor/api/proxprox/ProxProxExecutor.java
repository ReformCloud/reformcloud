package de.klaro.reformcloud2.executor.api.proxprox;

import de.klaro.reformcloud2.executor.api.api.API;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalEventBusHandler;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.event.EventManager;
import de.klaro.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import de.klaro.reformcloud2.executor.api.common.event.handler.Listener;
import de.klaro.reformcloud2.executor.api.common.network.auth.defaults.DefaultAuth;
import de.klaro.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import de.klaro.reformcloud2.executor.api.common.network.client.NetworkClient;
import de.klaro.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import de.klaro.reformcloud2.executor.api.common.process.NetworkInfo;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.executor.api.common.utility.system.SystemHelper;
import de.klaro.reformcloud2.executor.api.proxprox.event.ConnectHandler;
import de.klaro.reformcloud2.executor.api.proxprox.event.ProcessEventHandler;
import io.gomint.proxprox.ProxProxProxy;
import io.gomint.proxprox.api.ProxProx;
import io.gomint.proxprox.api.data.ServerDataHolder;
import io.gomint.proxprox.api.entity.Player;
import io.gomint.proxprox.api.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public final class ProxProxExecutor extends API {

    private static ProxProxExecutor instance;

    private static final Map<String, ProcessInformation> markedServers = new HashMap<>();

    private final Plugin plugin;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private ProcessInformation thisProcessInformation;

    ProxProxExecutor(Plugin plugin) {
        instance = this;
        this.plugin = plugin;

        new ExternalEventBusHandler(packetHandler, new DefaultEventManager());
        getEventManager().registerListener(new ProcessEventHandler());
        ProxProxProxy.getInstance().getPluginManager().registerListener(plugin, new ConnectHandler());

        String connectionKey = JsonConfiguration.read("reformcloud/.connection/key.json").getString("key");
        SystemHelper.deleteFile(new File("reformcloud/.connection/key.json"));
        JsonConfiguration connectionConfig = JsonConfiguration.read("reformcloud/.connection/connection.json");

        ProcessInformation startInfo = this.thisProcessInformation = connectionConfig.get("startInfo", ProcessInformation.TYPE);

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
        ExecutorAPI.setInstance(this);
    }

    NetworkClient getNetworkClient() {
        return networkClient;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
    }

    public static ProxProxExecutor getInstance() {
        return instance;
    }

    @Override
    public PacketHandler packetHandler() {
        return packetHandler;
    }

    public static void handleProcessUpdate(ProcessInformation processInformation) {
        if (!markedServers.containsKey(processInformation.getName())
                && processInformation.getNetworkInfo().isConnected()
                && processInformation.getTemplate().getVersion().getId() == 3) {
            markedServers.put(processInformation.getName(), processInformation);
        }
    }

    public static void handleProcessRemove(ProcessInformation processInformation) {
        markedServers.remove(processInformation.getName());
    }

    public static void connectPlayer(UUID uuid, String target) {
        Links.toOptional(ProxProx.getProxy().getPlayer(uuid)).ifPresent(new Consumer<Player>() {
            @Override
            public void accept(Player player) {
                if (markedServers.containsKey(target)) {
                    NetworkInfo networkInfo = toNetworkInfo(markedServers.get(target));
                    player.connect(networkInfo.getHost(), networkInfo.getPort());
                }
            }
        });
    }

    private static NetworkInfo toNetworkInfo(ProcessInformation processInformation) {
        return processInformation.getNetworkInfo();
    }

    public static ServerDataHolder toServerDataHolder(ProcessInformation processInformation) {
        NetworkInfo networkInfo = toNetworkInfo(processInformation);
        return new ServerDataHolder(networkInfo.getHost(), networkInfo.getPort());
    }

    public static boolean isServerKnown(ProcessInformation processInformation) {
        return markedServers.containsKey(processInformation.getName());
    }

    @Listener
    public void handleThisUpdate(final ProcessUpdatedEvent event) {
        if (event.getProcessInformation().getProcessUniqueID().equals(thisProcessInformation.getProcessUniqueID())) {
            thisProcessInformation = event.getProcessInformation();
        }
    }

    @Override
    public ProcessInformation getThisProcessInformation() {
        return thisProcessInformation;
    }
}
