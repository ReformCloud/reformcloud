package systems.reformcloud.reformcloud2.executor.node.api.player;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.api.NodeAPIAction;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class PlayerAPIImplementation implements PlayerAsyncAPI, PlayerSyncAPI {

    public PlayerAPIImplementation(NodeNetworkManager nodeNetworkManager) {
        this.nodeNetworkManager = nodeNetworkManager;
    }

    private final NodeNetworkManager nodeNetworkManager;

    @NotNull
    @Override
    public Task<Void> sendMessageAsync(@NotNull UUID player, @NotNull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnProxy(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.SEND_MESSAGE, Arrays.asList(player, message)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.SEND_MESSAGE, Arrays.asList(player, message)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> kickPlayerAsync(@NotNull UUID player, @NotNull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnProxy(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.KICK_PLAYER, Arrays.asList(player, message)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.KICK_PLAYER, Arrays.asList(player, message)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> kickPlayerFromServerAsync(@NotNull UUID player, @NotNull String message) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.KICK_PLAYER, Arrays.asList(player, message)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.KICK_PLAYER, Arrays.asList(player, message)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> playSoundAsync(@NotNull UUID player, @NotNull String sound, float f1, float f2) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.PLAY_SOUND, Arrays.asList(player, sound, f1, f2)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.PLAY_SOUND, Arrays.asList(player, sound, f1, f2)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> sendTitleAsync(@NotNull UUID player, @NotNull String title, @NotNull String subTitle, int fadeIn, int stay, int fadeOut) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnProxy(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.SEND_TITLE, Arrays.asList(player, title, subTitle, fadeIn, stay, fadeOut)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.SEND_TITLE, Arrays.asList(player, title, subTitle, fadeIn, stay, fadeOut)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> playEffectAsync(@NotNull UUID player, @NotNull String entityEffect) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.PLAY_ENTITY_EFFECT, Arrays.asList(player, entityEffect)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.PLAY_ENTITY_EFFECT, Arrays.asList(player, entityEffect)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public <T> Task<Void> playEffectAsync(@NotNull UUID player, @NotNull String effect, T data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.PLAY_EFFECT, Arrays.asList(player, effect, data)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.PLAY_EFFECT, Arrays.asList(player, effect, data)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> respawnAsync(@NotNull UUID player) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.RESPAWN, Collections.singletonList(player)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.RESPAWN, Collections.singletonList(player)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> teleportAsync(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.LOCATION_TELEPORT, Arrays.asList(player, world, x, y, z, yaw, pitch)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.LOCATION_TELEPORT, Arrays.asList(player, world, x, y, z, yaw, pitch)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> connectAsync(@NotNull UUID player, @NotNull String server) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnProxy(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.CONNECT, Arrays.asList(player, server)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.CONNECT, Arrays.asList(player, server)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> connectAsync(@NotNull UUID player, @NotNull ProcessInformation server) {
        return connectAsync(player, server.getProcessDetail().getName());
    }

    @NotNull
    @Override
    public Task<Void> connectAsync(@NotNull UUID player, @NotNull UUID target) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation targetServer = getPlayerOnServer(target);
            if (targetServer != null) {
                connectAsync(player, targetServer).awaitUninterruptedly();
            }

            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> setResourcePackAsync(@NotNull UUID player, @NotNull String pack) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            ProcessInformation processInformation = this.getPlayerOnServer(player);
            if (processInformation == null) {
                task.complete(null);
                return;
            }

            if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(processInformation.getProcessDetail().getParentName())) {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.SET_RESOURCE_PACK, Arrays.asList(player, pack)
                )));
            } else {
                DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(e -> e.sendPacket(new NodeAPIAction(
                        NodeAPIAction.APIAction.SET_RESOURCE_PACK, Arrays.asList(player, pack)
                )));
            }

            task.complete(null);
        });
        return task;
    }

    @Override
    public void sendMessage(@NotNull UUID player, @NotNull String message) {
        sendMessageAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void kickPlayer(@NotNull UUID player, @NotNull String message) {
        kickPlayerAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void kickPlayerFromServer(@NotNull UUID player, @NotNull String message) {
        kickPlayerFromServerAsync(player, message).awaitUninterruptedly();
    }

    @Override
    public void playSound(@NotNull UUID player, @NotNull String sound, float f1, float f2) {
        playSoundAsync(player, sound, f1, f2).awaitUninterruptedly();
    }

    @Override
    public void sendTitle(@NotNull UUID player, @NotNull String title, @NotNull String subTitle, int fadeIn, int stay, int fadeOut) {
        sendTitleAsync(player, title, subTitle, fadeIn, stay, fadeOut).awaitUninterruptedly();
    }

    @Override
    public void playEffect(@NotNull UUID player, @NotNull String entityEffect) {
        playEffectAsync(player, entityEffect).awaitUninterruptedly();
    }

    @Override
    public <T> void playEffect(@NotNull UUID player, @NotNull String effect, T data) {
        playEffectAsync(player, effect, data).awaitUninterruptedly();
    }

    @Override
    public void respawn(@NotNull UUID player) {
        respawnAsync(player).awaitUninterruptedly();
    }

    @Override
    public void teleport(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
        teleportAsync(player, world, x, y, z, yaw, pitch).awaitUninterruptedly();
    }

    @Override
    public void connect(@NotNull UUID player, @NotNull String server) {
        connectAsync(player, server).awaitUninterruptedly();
    }

    @Override
    public void connect(@NotNull UUID player, @NotNull ProcessInformation server) {
        connectAsync(player, server).awaitUninterruptedly();
    }

    @Override
    public void connect(@NotNull UUID player, @NotNull UUID target) {
        connectAsync(player, target).awaitUninterruptedly();
    }

    @Override
    public void setResourcePack(@NotNull UUID player, @NotNull String pack) {
        setResourcePackAsync(player, pack).awaitUninterruptedly();
    }

    private ProcessInformation getPlayerOnProxy(UUID uniqueID) {
        return Streams.filter(this.nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(), processInformation -> !processInformation.getProcessDetail().getTemplate().isServer() && Streams.filterToReference(processInformation.getProcessPlayerManager().getOnlinePlayers(), player -> player.getUniqueID().equals(uniqueID)).isPresent());
    }

    private ProcessInformation getPlayerOnServer(UUID uniqueID) {
        return Streams.filter(this.nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(), processInformation -> processInformation.getProcessDetail().getTemplate().isServer() && Streams.filterToReference(processInformation.getProcessPlayerManager().getOnlinePlayers(), player -> player.getUniqueID().equals(uniqueID)).isPresent());
    }
}
