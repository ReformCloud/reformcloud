package de.klaro.reformcloud2.executor.api.velocity;

import com.velocitypowered.api.proxy.Player;
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
import de.klaro.reformcloud2.executor.api.executor.PlayerAPIExecutor;
import de.klaro.reformcloud2.executor.api.packets.in.APIPacketInAPIAction;
import de.klaro.reformcloud2.executor.api.velocity.event.ExtraListenerHandler;
import de.klaro.reformcloud2.executor.api.velocity.event.PlayerListenerHandler;
import de.klaro.reformcloud2.executor.api.velocity.event.ProcessEventHandler;
import de.klaro.reformcloud2.executor.api.velocity.plugins.PluginUpdater;
import net.kyori.text.TextComponent;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

public final class VelocityExecutor extends API implements PlayerAPIExecutor {

    private static VelocityExecutor instance;

    private final ProxyServer proxyServer;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private ProcessInformation thisProcessInformation;

    VelocityExecutor(VelocityLauncher launcher, ProxyServer proxyServer) {
        instance = this;
        this.proxyServer = proxyServer;

        new ExternalEventBusHandler(packetHandler, new DefaultEventManager());
        getEventManager().registerListener(new ProcessEventHandler());
        getEventManager().registerListener(this);
        proxyServer.getEventManager().register(launcher, new PlayerListenerHandler());
        proxyServer.getEventManager().register(launcher, new ExtraListenerHandler());

        packetHandler.registerHandler(new APIPacketInAPIAction(this));

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
        awaitConnectionAndUpdate();
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

    private void awaitConnectionAndUpdate() {
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                while (packetSender == null) {
                    packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                    AbsoluteThread.sleep(100);
                }

                getAllProcesses().forEach(new Consumer<ProcessInformation>() {
                    @Override
                    public void accept(ProcessInformation processInformation) {
                        handleProcessUpdate(processInformation);
                    }
                });

                new PluginUpdater();

                thisProcessInformation.updateMaxPlayers(proxyServer.getConfiguration().getShowMaxPlayers());
                thisProcessInformation.updateRuntimeInformation();
                ExecutorAPI.getInstance().update(thisProcessInformation);
            }
        });
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

    public void setThisProcessInformation(ProcessInformation thisProcessInformation) {
        this.thisProcessInformation = thisProcessInformation;
    }

    /* ======================== Player API ======================== */

    @Override
    public void executeSendMessage(UUID player, String message) {
        proxyServer.getPlayer(player).ifPresent(new Consumer<Player>() {
            @Override
            public void accept(Player player) {
                player.sendMessage(TextComponent.of(message));
            }
        });
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        proxyServer.getPlayer(player).ifPresent(new Consumer<Player>() {
            @Override
            public void accept(Player player) {
                player.disconnect(TextComponent.of(message));
            }
        });
    }

    @Override
    public void executePlaySound(UUID player, String sound, float f1, float f2) {
        throw new UnsupportedOperationException("Not supported on velocity");
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        throw new UnsupportedOperationException("Not supported on velocity");
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
        throw new UnsupportedOperationException("Not supported on velocity");
    }

    @Override
    public <T> void executePlayEffect(UUID player, String effect, T data) {
        throw new UnsupportedOperationException("Not supported on velocity");
    }

    @Override
    public void executeRespawn(UUID player) {
        throw new UnsupportedOperationException("Not supported on velocity");
    }

    @Override
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        throw new UnsupportedOperationException("Not supported on velocity");
    }

    @Override
    public void executeConnect(UUID player, String server) {
        proxyServer.getPlayer(player).ifPresent(new Consumer<Player>() {
            @Override
            public void accept(Player player) {
                if (isServerRegistered(server)) {
                    player.createConnectionRequest(
                            proxyServer.getServer(server).get()
                    ).fireAndForget();
                }
            }
        });
    }

    @Override
    public void executeConnect(UUID player, ProcessInformation server) {
        executeConnect(player, server.getName());
    }

    @Override
    public void executeConnect(UUID player, UUID target) {
        proxyServer.getPlayer(player).ifPresent(new Consumer<Player>() {
            @Override
            public void accept(Player player) {
                proxyServer.getPlayer(target).ifPresent(new Consumer<Player>() {
                    @Override
                    public void accept(Player targetPlayer) {
                        if (targetPlayer.getCurrentServer().isPresent()
                                && isServerRegistered(targetPlayer.getCurrentServer().get().getServerInfo().getName())) {
                            player.createConnectionRequest(
                                    targetPlayer.getCurrentServer().get().getServer()
                            ).fireAndForget();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void executeSetResourcePack(UUID player, String pack) {
        proxyServer.getPlayer(player).ifPresent(new Consumer<Player>() {
            @Override
            public void accept(Player player) {
                player.sendResourcePack(pack);
            }
        });
    }
}
