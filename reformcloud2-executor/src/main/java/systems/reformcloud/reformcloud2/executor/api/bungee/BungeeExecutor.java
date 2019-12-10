package systems.reformcloud.reformcloud2.executor.api.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.bungee.event.PlayerListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.bungee.event.ProcessEventHandler;
import systems.reformcloud.reformcloud2.executor.api.bungee.plugins.PluginExecutorContainer;
import systems.reformcloud.reformcloud2.executor.api.bungee.plugins.PluginUpdater;
import systems.reformcloud.reformcloud2.executor.api.bungee.reconnect.ReformCloudReconnectHandler;
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

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

public final class BungeeExecutor extends API implements PlayerAPIExecutor {

    public static final List<ProcessInformation> LOBBY_SERVERS = new ArrayList<>();

    private static BungeeExecutor instance;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private final Plugin plugin;

    private ProcessInformation thisProcessInformation;

    private IngameMessages messages = new IngameMessages();

    private static boolean waterdog;

    private static boolean waterdogPE;

    BungeeExecutor(Plugin plugin) {
        super.type = ExecutorType.API;

        this.plugin = plugin;
        instance = this;
        new ExternalEventBusHandler(packetHandler, new DefaultEventManager());
        getEventManager().registerListener(new ProcessEventHandler());
        getEventManager().registerListener(this);

        packetHandler.registerHandler(new APIPacketInAPIAction(this));
        packetHandler.registerHandler(new APIPacketInPluginAction(new PluginExecutorContainer()));

        String connectionKey = JsonConfiguration.read("reformcloud/.connection/key.json").getString("key");
        SystemHelper.deleteFile(new File("reformcloud/.connection/key.json"));
        JsonConfiguration connectionConfig = JsonConfiguration.read("reformcloud/.connection/connection.json");

        this.thisProcessInformation = connectionConfig.get("startInfo", ProcessInformation.TYPE);
        waterdog = thisProcessInformation.getTemplate().getVersion().equals(Version.WATERDOG)
                || thisProcessInformation.getTemplate().getVersion().equals(Version.WATERDOG_PE);
        waterdogPE = thisProcessInformation.getTemplate().getVersion().equals(Version.WATERDOG_PE);

        this.networkClient.connect(
                connectionConfig.getString("controller-host"),
                connectionConfig.getInteger("controller-port"),
                new DefaultAuth(
                        connectionKey,
                        thisProcessInformation.getParent(),
                        NetworkType.PROCESS,
                        thisProcessInformation.getName(),
                        new JsonConfiguration()
                ), networkChannelReader
        );

        ExecutorAPI.setInstance(this);
        ProxyServer.getInstance().setReconnectHandler(new ReformCloudReconnectHandler());
        awaitConnectionAndUpdate();
    }

    @Nonnull
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
        ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> listenerInfo.getServerPriority().clear());
        ProxyServer.getInstance().getConfig().getServers().clear();
    }

    private void awaitConnectionAndUpdate() {
        Task.EXECUTOR.execute(() -> {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            while (packetSender == null) {
                packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                AbsoluteThread.sleep(100);
            }

            AbsoluteThread.sleep(100);

            getAllProcesses().forEach(BungeeExecutor::registerServer);
            ProxyServer.getInstance().getPluginManager().registerListener(plugin, new PlayerListenerHandler());
            new PluginUpdater();

            thisProcessInformation.updateMaxPlayers(ProxyServer.getInstance().getConfig().getPlayerLimit());
            thisProcessInformation.updateRuntimeInformation();
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(thisProcessInformation);

            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(controller -> packetHandler.getQueryHandler().sendQueryAsync(controller, new APIBungeePacketOutRequestIngameMessages()).onComplete(packet -> {
                IngameMessages ingameMessages = packet.content().get("messages", IngameMessages.TYPE);
                setMessages(ingameMessages);
            }));
        });
    }

    public static void registerServer(ProcessInformation processInformation) {
        if (!ProxyServer.getInstance().getServers().containsKey(processInformation.getName())
                && processInformation.getNetworkInfo().isConnected()
                && processInformation.getTemplate().isServer()) {
            ServerInfo serverInfo = constructServerInfo(processInformation);
            if (serverInfo == null) {
                return;
            }

            ProxyServer.getInstance().getServers().put(
                    processInformation.getName(),
                    serverInfo
            );

            if (processInformation.isLobby()) {
                LOBBY_SERVERS.add(processInformation);
                ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> listenerInfo.getServerPriority().add(processInformation.getName()));
            }
        }
    }

    private static ServerInfo constructServerInfo(ProcessInformation processInformation) {
        if (waterdog) {
            if (waterdogPE && processInformation.getTemplate().getVersion().getId() != 3) {
                return null;
            } else if (!waterdogPE && processInformation.getTemplate().getVersion().getId() == 3) {
                return null;
            }

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

    public static ProcessInformation getBestLobbyForPlayer(ProcessInformation current, ProxiedPlayer proxiedPlayer, Function<String, Boolean> permissionCheck) {
        final List<ProcessInformation> lobbies = new ArrayList<>(LOBBY_SERVERS);

        if (proxiedPlayer != null && proxiedPlayer.getServer() != null) {
            Links.allOf(lobbies, e -> e.getName().equals(proxiedPlayer.getServer().getInfo().getName())).forEach(lobbies::remove);
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

    @Override
    public ProcessInformation getThisProcessInformation() {
        return thisProcessInformation;
    }

    public void setThisProcessInformation(ProcessInformation thisProcessInformation) {
        this.thisProcessInformation = thisProcessInformation;
    }

    public IngameMessages getMessages() {
        return messages;
    }

    private void setMessages(IngameMessages messages) {
        this.messages = messages;
    }

    @Listener
    public void handleThisUpdate(final ProcessUpdatedEvent event) {
        if (event.getProcessInformation().getProcessUniqueID().equals(thisProcessInformation.getProcessUniqueID())) {
            thisProcessInformation = event.getProcessInformation();
        }
    }

    /* ======================== Player API ======================== */

    @Override
    public void executeSendMessage(UUID player, String message) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer != null) {
            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(message));
        }
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer != null) {
            proxiedPlayer.disconnect(TextComponent.fromLegacyText(message));
        }
    }

    @Override
    public void executePlaySound(UUID player, String sound, float f1, float f2) {
        throw new UnsupportedOperationException("Not supported on proxy server");
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer != null) {
            ProxyServer.getInstance().createTitle()
                    .title(TextComponent.fromLegacyText(title))
                    .subTitle(TextComponent.fromLegacyText(subTitle))
                    .fadeIn(fadeIn)
                    .stay(stay)
                    .fadeOut(fadeOut)
                    .send(proxiedPlayer);
        }
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
        throw new UnsupportedOperationException("Not supported on proxy server");
    }

    @Override
    public <T> void executePlayEffect(UUID player, String effect, T data) {
        throw new UnsupportedOperationException("Not supported on proxy server");
    }

    @Override
    public void executeRespawn(UUID player) {
        throw new UnsupportedOperationException("Not supported on proxy server");
    }

    @Override
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        throw new UnsupportedOperationException("Not supported on proxy server");
    }

    @Override
    public void executeConnect(UUID player, String server) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
        if (proxiedPlayer != null && serverInfo != null) {
            proxiedPlayer.connect(serverInfo);
        }
    }

    @Override
    public void executeConnect(UUID player, ProcessInformation server) {
        executeConnect(player, server.getName());
    }

    @Override
    public void executeConnect(UUID player, UUID target) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);
        if (proxiedPlayer != null && targetPlayer != null) {
            proxiedPlayer.connect(targetPlayer.getServer().getInfo());
        }
    }

    @Override
    public void executeSetResourcePack(UUID player, String pack) {
        throw new UnsupportedOperationException("Not supported on proxy server");
    }
}
