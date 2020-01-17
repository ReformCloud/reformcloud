package systems.reformcloud.reformcloud2.executor.api.velocity;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.text.TextComponent;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.NetworkType;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.defaults.DefaultAuth;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.executor.PlayerAPIExecutor;
import systems.reformcloud.reformcloud2.executor.api.packets.in.APIPacketInAPIAction;
import systems.reformcloud.reformcloud2.executor.api.packets.in.APIPacketInPluginAction;
import systems.reformcloud.reformcloud2.executor.api.packets.out.APIBungeePacketOutRequestIngameMessages;
import systems.reformcloud.reformcloud2.executor.api.velocity.event.PlayerListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.velocity.event.ProcessEventHandler;
import systems.reformcloud.reformcloud2.executor.api.velocity.plugins.PluginExecutorContainer;
import systems.reformcloud.reformcloud2.executor.api.velocity.plugins.PluginUpdater;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

public final class VelocityExecutor extends API implements PlayerAPIExecutor {

    private static final List<ProcessInformation> LOBBY_SERVERS = new ArrayList<>();

    private static VelocityExecutor instance;

    private final ProxyServer proxyServer;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private IngameMessages messages = new IngameMessages();

    private ProcessInformation thisProcessInformation;

    VelocityExecutor(VelocityLauncher launcher, ProxyServer proxyServer) {
        super.type = ExecutorType.API;

        instance = this;
        this.proxyServer = proxyServer;

        new ExternalEventBusHandler(packetHandler, new DefaultEventManager());
        getEventManager().registerListener(new ProcessEventHandler());
        getEventManager().registerListener(this);
        proxyServer.getEventManager().register(launcher, new PlayerListenerHandler());

        packetHandler.registerHandler(new APIPacketInAPIAction(this));
        packetHandler.registerHandler(new APIPacketInPluginAction(new PluginExecutorContainer()));

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
                        NetworkType.PROCESS,
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

    @Nonnull
    @Override
    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    @Nonnull
    public static VelocityExecutor getInstance() {
        return instance;
    }

    public void handleProcessUpdate(ProcessInformation processInformation) {
        if (!isServerRegistered(processInformation.getName())
                && processInformation.getNetworkInfo().isConnected()
                && processInformation.getTemplate().getVersion().getId() == 1) {
            ServerInfo serverInfo = new ServerInfo(
                    processInformation.getName(),
                    processInformation.getNetworkInfo().toInet()
            );
            proxyServer.registerServer(serverInfo);
            if (processInformation.isLobby()) {
                LOBBY_SERVERS.add(processInformation);
            }
        }
    }

    public void handleProcessRemove(ProcessInformation processInformation) {
        proxyServer.getServer(processInformation.getName()).ifPresent(registeredServer -> proxyServer.unregisterServer(registeredServer.getServerInfo()));

        if (processInformation.isLobby()) {
            LOBBY_SERVERS.remove(processInformation);
        }
    }

    private boolean isServerRegistered(String name) {
        return proxyServer.getServer(name).isPresent();
    }

    private void awaitConnectionAndUpdate() {
        Task.EXECUTOR.execute(() -> {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            while (packetSender == null) {
                packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                AbsoluteThread.sleep(100);
            }

            getAllProcesses().forEach(this::handleProcessUpdate);

            new PluginUpdater();

            thisProcessInformation.updateMaxPlayers(proxyServer.getConfiguration().getShowMaxPlayers());
            thisProcessInformation.updateRuntimeInformation();
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(thisProcessInformation);

            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(controller -> packetHandler.getQueryHandler().sendQueryAsync(controller, new APIBungeePacketOutRequestIngameMessages()).onComplete(packet -> {
                IngameMessages ingameMessages = packet.content().get("messages", IngameMessages.TYPE);
                setMessages(ingameMessages);
            }));
        });
    }

    public static ProcessInformation getBestLobbyForPlayer(ProcessInformation current, Player player, Function<String, Boolean> permissionCheck) {
        final List<ProcessInformation> lobbies = new ArrayList<>(LOBBY_SERVERS);

        if (player != null && player.getCurrentServer().isPresent()) {
            Links.allOf(lobbies, e -> e.getName().equals(player.getCurrentServer().get().getServerInfo().getName())).forEach(lobbies::remove);
        }

        // Filter all non java servers if this is a java proxy else all mcpe servers
        Links.others(lobbies, e -> {
            Version version = e.getTemplate().getVersion();
            if (version.equals(Version.NUKKIT_X) && current.getTemplate().getVersion().equals(Version.WATERDOG_PE)) {
                return true;
            }

            return version.getId() == 1 && current.getTemplate().getVersion().getId() == 2;
        }).forEach(lobbies::remove);

        // Filter out all lobbies with join permission which the player does not have
        Links.others(lobbies, e -> {
            final PlayerAccessConfiguration configuration = e.getProcessGroup().getPlayerAccessConfiguration();
            if (!configuration.isJoinOnlyPerPermission()) {
                return true;
            }

            return permissionCheck.apply(configuration.getJoinPermission());
        }).forEach(lobbies::remove);

        // Filter out all lobbies which are in maintenance and not joinable for the player
        Links.others(lobbies, e -> {
            final PlayerAccessConfiguration configuration = e.getProcessGroup().getPlayerAccessConfiguration();
            if (!configuration.isMaintenance()) {
                return true;
            }

            return permissionCheck.apply(configuration.getMaintenanceJoinPermission());
        }).forEach(lobbies::remove);

        // Filter out all full server which the player cannot access
        Links.others(lobbies, e -> {
            final PlayerAccessConfiguration configuration = e.getProcessGroup().getPlayerAccessConfiguration();
            if (!configuration.isUseCloudPlayerLimit()) {
                return true;
            }

            if (e.getOnlineCount() < configuration.getMaxPlayers()) {
                return true;
            }

            return permissionCheck.apply("reformcloud.join.full");
        }).forEach(lobbies::remove);

        if (lobbies.isEmpty()) {
            return null;
        }

        if (lobbies.size() == 1) {
            return lobbies.get(0);
        }

        return lobbies.get(new Random().nextInt(lobbies.size()));
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

    public IngameMessages getMessages() {
        return messages;
    }

    private void setMessages(IngameMessages messages) {
        this.messages = messages;
    }

    public void setThisProcessInformation(ProcessInformation thisProcessInformation) {
        this.thisProcessInformation = thisProcessInformation;
    }

    /* ======================== Player API ======================== */

    @Override
    public void executeSendMessage(UUID player, String message) {
        proxyServer.getPlayer(player).ifPresent(player1 -> player1.sendMessage(TextComponent.of(message)));
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        proxyServer.getPlayer(player).ifPresent(player1 -> player1.disconnect(TextComponent.of(message)));
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
        proxyServer.getPlayer(player).ifPresent(player1 -> {
            if (isServerRegistered(server)) {
                player1.createConnectionRequest(
                        proxyServer.getServer(server).get()
                ).fireAndForget();
            }
        });
    }

    @Override
    public void executeConnect(UUID player, ProcessInformation server) {
        executeConnect(player, server.getName());
    }

    @Override
    public void executeConnect(UUID player, UUID target) {
        proxyServer.getPlayer(player).ifPresent(player1 -> proxyServer.getPlayer(target).ifPresent(targetPlayer -> {
            if (targetPlayer.getCurrentServer().isPresent()
                    && isServerRegistered(targetPlayer.getCurrentServer().get().getServerInfo().getName())) {
                player1.createConnectionRequest(
                        targetPlayer.getCurrentServer().get().getServer()
                ).fireAndForget();
            }
        }));
    }

    @Override
    public void executeSetResourcePack(UUID player, String pack) {
        proxyServer.getPlayer(player).ifPresent(player1 -> player1.sendResourcePack(pack));
    }
}
