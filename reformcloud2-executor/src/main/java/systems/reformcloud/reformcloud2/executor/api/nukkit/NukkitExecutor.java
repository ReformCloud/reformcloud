package systems.reformcloud.reformcloud2.executor.api.nukkit;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
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
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared.ClientChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.ProxiedChannelMessageHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.executor.PlayerAPIExecutor;
import systems.reformcloud.reformcloud2.executor.api.network.channel.APINetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.packets.in.APIPacketInAPIAction;
import systems.reformcloud.reformcloud2.executor.api.network.packets.in.APIPacketInPluginAction;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIBungeePacketOutRequestIngameMessages;
import systems.reformcloud.reformcloud2.executor.api.nukkit.plugins.PluginsExecutorContainer;

import java.io.File;
import java.util.UUID;

public final class NukkitExecutor extends API implements PlayerAPIExecutor {

    private static NukkitExecutor instance;

    private final Plugin plugin;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private IngameMessages messages = new IngameMessages();

    private ProcessInformation thisProcessInformation;

    NukkitExecutor(Plugin plugin) {
        super.type = ExecutorType.API;

        instance = this;
        this.plugin = plugin;

        new ExternalEventBusHandler(packetHandler, new DefaultEventManager());
        getEventManager().registerListener(this);

        packetHandler.registerHandler(new APIPacketInAPIAction(this));
        packetHandler.registerHandler(new APIPacketInPluginAction(new PluginsExecutorContainer()));
        packetHandler.registerHandler(new ProxiedChannelMessageHandler());

        String connectionKey = JsonConfiguration.read("reformcloud/.connection/key.json").getString("key");
        SystemHelper.deleteFile(new File("reformcloud/.connection/key.json"));
        JsonConfiguration connectionConfig = JsonConfiguration.read("reformcloud/.connection/connection.json");

        this.thisProcessInformation = connectionConfig.get("startInfo", ProcessInformation.TYPE);
        if (thisProcessInformation == null) {
            System.exit(0);
            return;
        }

        this.networkClient.connect(
                connectionConfig.getString("controller-host"),
                connectionConfig.getInteger("controller-port"),
                () -> new APINetworkChannelReader(this.packetHandler),
                new ClientChallengeAuthHandler(
                        connectionKey,
                        thisProcessInformation.getName(),
                        () -> new JsonConfiguration(),
                        context -> {
                        } // unused here
                )
        );
        ExecutorAPI.setInstance(this);
        awaitConnectionAndUpdate();
    }

    NetworkClient getNetworkClient() {
        return networkClient;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @NotNull
    public IngameMessages getMessages() {
        return messages;
    }

    @NotNull
    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
    }

    @Override
    public PacketHandler packetHandler() {
        return packetHandler;
    }

    @NotNull
    @Override
    public ProcessInformation getCurrentProcessInformation() {
        return this.thisProcessInformation;
    }

    @NotNull
    public static NukkitExecutor getInstance() {
        return instance;
    }

    @Override
    @Deprecated
    public ProcessInformation getThisProcessInformation() {
        return getCurrentProcessInformation();
    }

    public void setThisProcessInformation(ProcessInformation thisProcessInformation) {
        this.thisProcessInformation = thisProcessInformation;
    }

    private void awaitConnectionAndUpdate() {
        Task.EXECUTOR.execute(() -> {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            while (packetSender == null) {
                packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                AbsoluteThread.sleep(100);
            }

            thisProcessInformation.updateMaxPlayers(Server.getInstance().getMaxPlayers());
            thisProcessInformation.updateRuntimeInformation();
            thisProcessInformation.getNetworkInfo().setConnected(true);
            thisProcessInformation.getProcessDetail().setProcessState(ProcessState.READY);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(thisProcessInformation);

            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(controller -> packetHandler.getQueryHandler().sendQueryAsync(controller, new APIBungeePacketOutRequestIngameMessages()).onComplete(packet -> {
                IngameMessages messages = packet.content().get("messages", IngameMessages.TYPE);
                if (messages != null) {
                    NukkitExecutor.this.messages = messages;
                }
            }));
        });
    }

    @Listener
    public void handle(final ProcessUpdatedEvent event) {
        if (event.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(thisProcessInformation.getProcessDetail().getProcessUniqueID())) {
            thisProcessInformation = event.getProcessInformation();
        }
    }

    /* ======================== Player API ======================== */

    @Override
    public void executeSendMessage(UUID player, String message) {
        Server.getInstance().getPlayer(player).ifPresent(player1 -> player1.sendMessage(message));
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        Server.getInstance().getPlayer(player).ifPresent(player1 -> player1.kick(message));
    }

    @Override
    public void executePlaySound(UUID player, String sound, float f1, float f2) {
        throw new UnsupportedOperationException("Not supported on nukkit");
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        Server.getInstance().getPlayer(player).ifPresent(player1 -> player1.sendTitle(title, subTitle, fadeIn, stay, fadeOut));
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
        throw new UnsupportedOperationException("Not supported on nukkit");
    }

    @Override
    public <T> void executePlayEffect(UUID player, String effect, T data) {
        throw new UnsupportedOperationException("Not supported on nukkit");
    }

    @Override
    public void executeRespawn(UUID player) {
        Server.getInstance().getPlayer(player).ifPresent(Player::kill);
    }

    @Override
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        Server.getInstance().getPlayer(player).ifPresent(player1 -> player1.teleport(new Location(x, y, z, yaw, pitch)));
    }

    @Override
    public void executeConnect(UUID player, String server) {
        throw new UnsupportedOperationException("Not supported on nukkit");
    }

    @Override
    public void executeConnect(UUID player, ProcessInformation server) {
        throw new UnsupportedOperationException("Not supported on nukkit");
    }

    @Override
    public void executeConnect(UUID player, UUID target) {
        throw new UnsupportedOperationException("Not supported on nukkit");
    }

    @Override
    public void executeSetResourcePack(UUID player, String pack) {
        throw new UnsupportedOperationException("Not supported on nukkit");
    }
}
