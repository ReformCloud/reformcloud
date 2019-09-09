package de.klaro.reformcloud2.executor.api.spigot;

import com.google.common.base.Enums;
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
import de.klaro.reformcloud2.executor.api.common.process.ProcessState;
import de.klaro.reformcloud2.executor.api.common.utility.system.SystemHelper;
import de.klaro.reformcloud2.executor.api.common.utility.task.Task;
import de.klaro.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import de.klaro.reformcloud2.executor.api.executor.PlayerAPIExecutor;
import de.klaro.reformcloud2.executor.api.packets.in.APIPacketInAPIAction;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.UUID;

public final class SpigotExecutor extends API implements PlayerAPIExecutor {

    private static SpigotExecutor instance;

    private final JavaPlugin plugin;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private ProcessInformation thisProcessInformation;

    SpigotExecutor(JavaPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        new ExternalEventBusHandler(packetHandler, new DefaultEventManager());
        getEventManager().registerListener(this);

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

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public static SpigotExecutor getInstance() {
        return instance;
    }

    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
    }

    NetworkClient getNetworkClient() {
        return networkClient;
    }

    @Override
    public PacketHandler packetHandler() {
        return packetHandler;
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

    private void awaitConnectionAndUpdate() {
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                while (packetSender == null) {
                    packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                    AbsoluteThread.sleep(100);
                }

                thisProcessInformation.updateMaxPlayers(Bukkit.getMaxPlayers());
                thisProcessInformation.updateRuntimeInformation();
                ExecutorAPI.getInstance().update(thisProcessInformation);
                startSimulatePing();
            }
        });
    }

    private void startSimulatePing() {
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    ServerListPingEvent serverListPingEvent = new ServerListPingEvent(
                            new InetSocketAddress("127.0.0.1", 50000).getAddress(),
                            thisProcessInformation.getMotd(),
                            Bukkit.getOnlinePlayers().size(),
                            thisProcessInformation.getMaxPlayers()
                    );
                    Bukkit.getPluginManager().callEvent(serverListPingEvent);

                    boolean hasChanges = false;
                    if (!serverListPingEvent.getMotd().equals(thisProcessInformation.getMotd())) {
                        thisProcessInformation.setMotd(serverListPingEvent.getMotd());
                        hasChanges = true;
                    }

                    if (serverListPingEvent.getMaxPlayers() != thisProcessInformation.getMaxPlayers()) {
                        thisProcessInformation.updateMaxPlayers(serverListPingEvent.getMaxPlayers());
                        hasChanges = true;
                    }

                    if (!thisProcessInformation.getProcessState().equals(ProcessState.INVISIBLE)
                            && (serverListPingEvent.getMotd().toLowerCase().contains("hide")
                            || serverListPingEvent.getMotd().toLowerCase().contains("invisible"))) {
                        thisProcessInformation.setProcessState(ProcessState.INVISIBLE);
                        hasChanges = true;
                    }

                    if (hasChanges) {
                        thisProcessInformation.updateRuntimeInformation();
                        ExecutorAPI.getInstance().update(thisProcessInformation);
                    }
                } catch (final Throwable ignored) {
                }
            }
        }, 0, 20);
    }

    public void setThisProcessInformation(ProcessInformation thisProcessInformation) {
        this.thisProcessInformation = thisProcessInformation;
    }

    /* ======================== Player API ======================== */

    @Override
    public void executeSendMessage(UUID player, String message) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 != null) {
            player1.sendMessage(message);
        }
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 != null) {
            player1.kickPlayer(message);
        }
    }

    @Override
    public void executePlaySound(UUID player, String sound, float f1, float f2) {
        Player player1 = Bukkit.getPlayer(player);
        Sound sound1 = Enums.getIfPresent(Sound.class, sound).orNull();
        if (player1 != null && sound1 != null) {
            player1.playSound(player1.getLocation(), sound1, f1, f2);
        }
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 != null) {
            player1.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
        }
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
        Player player1 = Bukkit.getPlayer(player);
        EntityEffect entityEffect1 = Enums.getIfPresent(EntityEffect.class, entityEffect).orNull();
        if (player1 != null && entityEffect1 != null) {
            player1.playEffect(entityEffect1);
        }
    }

    @Override
    public <T> void executePlayEffect(UUID player, String effect, T data) {
        Player player1 = Bukkit.getPlayer(player);
        Effect effect1 = Enums.getIfPresent(Effect.class, effect).orNull();
        if (player1 != null && effect1 != null) {
            player1.playEffect(player1.getLocation(), effect1, data);
        }
    }

    @Override
    public void executeRespawn(UUID player) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 != null) {
            player1.spigot().respawn();
        }
    }

    @Override
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 != null) {
            player1.teleport(new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
        }
    }

    @Override
    public void executeConnect(UUID player, String server) {
        throw new UnsupportedOperationException("Not supported on spigot");
    }

    @Override
    public void executeConnect(UUID player, ProcessInformation server) {
        throw new UnsupportedOperationException("Not supported on spigot");
    }

    @Override
    public void executeConnect(UUID player, UUID target) {
        throw new UnsupportedOperationException("Not supported on spigot");
    }

    @Override
    public void executeSetResourcePack(UUID player, String pack) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 != null) {
            player1.setResourcePack(pack);
        }
    }
}
