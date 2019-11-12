package systems.reformcloud.reformcloud2.executor.api.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.NetworkType;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.defaults.DefaultAuth;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.executor.PlayerAPIExecutor;
import systems.reformcloud.reformcloud2.executor.api.packets.in.APIPacketInAPIAction;
import systems.reformcloud.reformcloud2.executor.api.sponge.event.PlayerListenerHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.UUID;

public class SpongeExecutor extends API implements PlayerAPIExecutor {

    private static SpongeExecutor instance;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private final SpongeLauncher plugin;

    private ProcessInformation thisProcessInformation;

    SpongeExecutor(SpongeLauncher launcher) {
        super.type = ExecutorType.API;

        this.plugin = launcher;
        instance = this;

        packetHandler.registerHandler(new APIPacketInAPIAction(this));

        new ExternalEventBusHandler(packetHandler, new DefaultEventManager());
        getEventManager().registerListener(this);
        Sponge.getEventManager().registerListeners(launcher, new PlayerListenerHandler());

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

    @Override
    public ProcessInformation getThisProcessInformation() {
        return thisProcessInformation;
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

    @Nonnull
    public NetworkClient getNetworkClient() {
        return networkClient;
    }

    @Nonnull
    public SpongeLauncher getPlugin() {
        return plugin;
    }

    @Nonnull
    public static SpongeExecutor getInstance() {
        return instance;
    }

    public void setThisProcessInformation(@Nonnull ProcessInformation thisProcessInformation) {
        this.thisProcessInformation = thisProcessInformation;
    }

    @Listener
    public void handleThisUpdate(final ProcessUpdatedEvent event) {
        if (event.getProcessInformation().getProcessUniqueID().equals(thisProcessInformation.getProcessUniqueID())) {
            thisProcessInformation = event.getProcessInformation();
        }
    }

    private void awaitConnectionAndUpdate() {
        Task.EXECUTOR.execute(() -> {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            while (packetSender == null) {
                packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                AbsoluteThread.sleep(100);
            }

            thisProcessInformation.updateMaxPlayers(Sponge.getServer().getMaxPlayers());
            thisProcessInformation.updateRuntimeInformation();
            ExecutorAPI.getInstance().update(thisProcessInformation);
        });
    }

    @Override
    public void executeSendMessage(UUID player, String message) {
        Sponge.getServer().getPlayer(player).ifPresent(e -> e.sendMessage(Text.of(message)));
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        Sponge.getServer().getPlayer(player).ifPresent(e -> e.kick(Text.of(message)));
    }

    @Override
    public void executePlaySound(UUID player, String sound, float f1, float f2) {
        Sponge.getServer().getPlayer(player).ifPresent(e -> {
            try {
                SoundType soundType = SoundType.of(sound);
                e.playSound(soundType, e.getLocation().getPosition(), f1, f2);
            } catch (final Throwable ignored) {
            }
        });
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        Sponge.getServer().getPlayer(player).ifPresent(e ->
            e.sendTitle(Title.CLEAR.toBuilder()
                    .title(Text.of(title))
                    .subtitle(Text.of(subTitle))
                    .fadeIn(fadeIn)
                    .stay(stay)
                    .fadeOut(fadeOut)
                    .build()
            )
        );
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
    }

    @Override
    public <T> void executePlayEffect(UUID player, String effect, @Nullable T data) {
    }

    @Override
    public void executeRespawn(UUID player) {
        Sponge.getServer().getPlayer(player).ifPresent(Player::respawnPlayer);
    }

    @Override
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        Sponge.getServer().getPlayer(player).ifPresent(e -> Sponge.getServer().getWorld(world).ifPresent(w ->
            e.setLocationSafely(new Location<World>(w, x, y, z))
        ));
    }

    @Override
    public void executeConnect(UUID player, String server) {

    }

    @Override
    public void executeConnect(UUID player, ProcessInformation server) {

    }

    @Override
    public void executeConnect(UUID player, UUID target) {

    }

    @Override
    public void executeSetResourcePack(UUID player, String pack) {
    }
}
