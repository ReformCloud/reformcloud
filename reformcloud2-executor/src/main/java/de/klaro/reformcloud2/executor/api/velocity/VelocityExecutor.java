package de.klaro.reformcloud2.executor.api.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
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
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.system.SystemHelper;
import de.klaro.reformcloud2.executor.api.proxprox.event.ProcessEventHandler;
import de.klaro.reformcloud2.executor.api.velocity.event.ConnectHandler;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

public final class VelocityExecutor extends API {

    private static VelocityExecutor instance;

    private final ProxyServer proxyServer;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private ProcessInformation thisProcessInformation;

    VelocityExecutor(ProxyServer proxyServer) {
        instance = this;
        this.proxyServer = proxyServer;

        new ExternalEventBusHandler(packetHandler, new DefaultEventManager());
        getEventManager().registerListener(new ProcessEventHandler());
        getEventManager().registerListener(this);
        proxyServer.getEventManager().register(proxyServer, new ConnectHandler());

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

    @Override
    public PacketHandler packetHandler() {
        return packetHandler;
    }

    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public static VelocityExecutor getInstance() {
        return instance;
    }

    public void handleProcessUpdate(ProcessInformation processInformation) {
        if (!isServerRegistered(processInformation.getName())
                && processInformation.getNetworkInfo().isConnected()
                && processInformation.getTemplate().getVersion().getId() == 1) {
            ServerInfo serverInfo = new ServerInfo(
                    processInformation.getName(),
                    new InetSocketAddress(processInformation.getNetworkInfo().getHost(), processInformation.getNetworkInfo().getPort())
            );
            proxyServer.registerServer(serverInfo);
        }
    }

    public void handleProcessRemove(ProcessInformation processInformation) {
        proxyServer.getServer(processInformation.getName()).ifPresent(new Consumer<RegisteredServer>() {
            @Override
            public void accept(RegisteredServer registeredServer) {
                proxyServer.unregisterServer(registeredServer.getServerInfo());
            }
        });
    }

    public boolean isServerRegistered(String name) {
        return proxyServer.getServer(name).isPresent();
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
