package systems.reformcloud.reformcloud2.executor.node.api.player;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.node.network.NodeNetworkManager;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.api.NodeAPIAction;

public class PlayerAPIImplementation implements PlayerAsyncAPI, PlayerSyncAPI {

  public PlayerAPIImplementation(NodeNetworkManager nodeNetworkManager) {
    this.nodeNetworkManager = nodeNetworkManager;
  }

  private final NodeNetworkManager nodeNetworkManager;

  @Nonnull
  @Override
  public Task<Void> sendMessageAsync(@Nonnull UUID player,
                                     @Nonnull String message) {
    Task<Void> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ProcessInformation processInformation = this.getPlayerOnProxy(player);
      if (processInformation == null) {
        task.complete(null);
        return;
      }

      if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(
              processInformation.getParent())) {
        DefaultChannelManager.INSTANCE.get(processInformation.getName())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.SEND_MESSAGE,
                           Arrays.asList(player, message))));
      } else {
        DefaultChannelManager.INSTANCE.get(processInformation.getParent())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.SEND_MESSAGE,
                           Arrays.asList(player, message))));
      }

      task.complete(null);
    });
    return task;
  }

  @Nonnull
  @Override
  public Task<Void> kickPlayerAsync(@Nonnull UUID player,
                                    @Nonnull String message) {
    Task<Void> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ProcessInformation processInformation = this.getPlayerOnProxy(player);
      if (processInformation == null) {
        task.complete(null);
        return;
      }

      if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(
              processInformation.getParent())) {
        DefaultChannelManager.INSTANCE.get(processInformation.getName())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.KICK_PLAYER,
                           Arrays.asList(player, message))));
      } else {
        DefaultChannelManager.INSTANCE.get(processInformation.getParent())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.KICK_PLAYER,
                           Arrays.asList(player, message))));
      }

      task.complete(null);
    });
    return task;
  }

  @Nonnull
  @Override
  public Task<Void> kickPlayerFromServerAsync(@Nonnull UUID player,
                                              @Nonnull String message) {
    Task<Void> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ProcessInformation processInformation = this.getPlayerOnServer(player);
      if (processInformation == null) {
        task.complete(null);
        return;
      }

      if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(
              processInformation.getParent())) {
        DefaultChannelManager.INSTANCE.get(processInformation.getName())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.KICK_PLAYER,
                           Arrays.asList(player, message))));
      } else {
        DefaultChannelManager.INSTANCE.get(processInformation.getParent())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.KICK_PLAYER,
                           Arrays.asList(player, message))));
      }

      task.complete(null);
    });
    return task;
  }

  @Nonnull
  @Override
  public Task<Void> playSoundAsync(@Nonnull UUID player, @Nonnull String sound,
                                   float f1, float f2) {
    Task<Void> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ProcessInformation processInformation = this.getPlayerOnServer(player);
      if (processInformation == null) {
        task.complete(null);
        return;
      }

      if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(
              processInformation.getParent())) {
        DefaultChannelManager.INSTANCE.get(processInformation.getName())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.PLAY_SOUND,
                           Arrays.asList(player, sound, f1, f2))));
      } else {
        DefaultChannelManager.INSTANCE.get(processInformation.getParent())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.PLAY_SOUND,
                           Arrays.asList(player, sound, f1, f2))));
      }

      task.complete(null);
    });
    return task;
  }

  @Nonnull
  @Override
  public Task<Void> sendTitleAsync(@Nonnull UUID player, @Nonnull String title,
                                   @Nonnull String subTitle, int fadeIn,
                                   int stay, int fadeOut) {
    Task<Void> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ProcessInformation processInformation = this.getPlayerOnProxy(player);
      if (processInformation == null) {
        task.complete(null);
        return;
      }

      if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(
              processInformation.getParent())) {
        DefaultChannelManager.INSTANCE.get(processInformation.getName())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.SEND_TITLE,
                           Arrays.asList(player, title, subTitle, fadeIn, stay,
                                         fadeOut))));
      } else {
        DefaultChannelManager.INSTANCE.get(processInformation.getParent())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.SEND_TITLE,
                           Arrays.asList(player, title, subTitle, fadeIn, stay,
                                         fadeOut))));
      }

      task.complete(null);
    });
    return task;
  }

  @Nonnull
  @Override
  public Task<Void> playEffectAsync(@Nonnull UUID player,
                                    @Nonnull String entityEffect) {
    Task<Void> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ProcessInformation processInformation = this.getPlayerOnServer(player);
      if (processInformation == null) {
        task.complete(null);
        return;
      }

      if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(
              processInformation.getParent())) {
        DefaultChannelManager.INSTANCE.get(processInformation.getName())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.PLAY_ENTITY_EFFECT,
                           Arrays.asList(player, entityEffect))));
      } else {
        DefaultChannelManager.INSTANCE.get(processInformation.getParent())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.PLAY_ENTITY_EFFECT,
                           Arrays.asList(player, entityEffect))));
      }

      task.complete(null);
    });
    return task;
  }

  @Nonnull
  @Override
  public <T> Task<Void> playEffectAsync(@Nonnull UUID player,
                                        @Nonnull String effect, T data) {
    Task<Void> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ProcessInformation processInformation = this.getPlayerOnServer(player);
      if (processInformation == null) {
        task.complete(null);
        return;
      }

      if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(
              processInformation.getParent())) {
        DefaultChannelManager.INSTANCE.get(processInformation.getName())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.PLAY_EFFECT,
                           Arrays.asList(player, effect, data))));
      } else {
        DefaultChannelManager.INSTANCE.get(processInformation.getParent())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.PLAY_EFFECT,
                           Arrays.asList(player, effect, data))));
      }

      task.complete(null);
    });
    return task;
  }

  @Nonnull
  @Override
  public Task<Void> respawnAsync(@Nonnull UUID player) {
    Task<Void> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ProcessInformation processInformation = this.getPlayerOnServer(player);
      if (processInformation == null) {
        task.complete(null);
        return;
      }

      if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(
              processInformation.getParent())) {
        DefaultChannelManager.INSTANCE.get(processInformation.getName())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.RESPAWN,
                           Collections.singletonList(player))));
      } else {
        DefaultChannelManager.INSTANCE.get(processInformation.getParent())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.RESPAWN,
                           Collections.singletonList(player))));
      }

      task.complete(null);
    });
    return task;
  }

  @Nonnull
  @Override
  public Task<Void> teleportAsync(@Nonnull UUID player, @Nonnull String world,
                                  double x, double y, double z, float yaw,
                                  float pitch) {
    Task<Void> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ProcessInformation processInformation = this.getPlayerOnServer(player);
      if (processInformation == null) {
        task.complete(null);
        return;
      }

      if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(
              processInformation.getParent())) {
        DefaultChannelManager.INSTANCE.get(processInformation.getName())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.LOCATION_TELEPORT,
                           Arrays.asList(player, world, x, y, z, yaw, pitch))));
      } else {
        DefaultChannelManager.INSTANCE.get(processInformation.getParent())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.LOCATION_TELEPORT,
                           Arrays.asList(player, world, x, y, z, yaw, pitch))));
      }

      task.complete(null);
    });
    return task;
  }

  @Nonnull
  @Override
  public Task<Void> connectAsync(@Nonnull UUID player, @Nonnull String server) {
    Task<Void> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ProcessInformation processInformation = this.getPlayerOnProxy(player);
      if (processInformation == null) {
        task.complete(null);
        return;
      }

      if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(
              processInformation.getParent())) {
        DefaultChannelManager.INSTANCE.get(processInformation.getName())
            .ifPresent(e
                       -> e.sendPacket(
                           new NodeAPIAction(NodeAPIAction.APIAction.CONNECT,
                                             Arrays.asList(player, server))));
      } else {
        DefaultChannelManager.INSTANCE.get(processInformation.getParent())
            .ifPresent(e
                       -> e.sendPacket(
                           new NodeAPIAction(NodeAPIAction.APIAction.CONNECT,
                                             Arrays.asList(player, server))));
      }

      task.complete(null);
    });
    return task;
  }

  @Nonnull
  @Override
  public Task<Void> connectAsync(@Nonnull UUID player,
                                 @Nonnull ProcessInformation server) {
    return connectAsync(player, server.getName());
  }

  @Nonnull
  @Override
  public Task<Void> connectAsync(@Nonnull UUID player, @Nonnull UUID target) {
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

  @Nonnull
  @Override
  public Task<Void> setResourcePackAsync(@Nonnull UUID player,
                                         @Nonnull String pack) {
    Task<Void> task = new DefaultTask<>();
    Task.EXECUTOR.execute(() -> {
      ProcessInformation processInformation = this.getPlayerOnServer(player);
      if (processInformation == null) {
        task.complete(null);
        return;
      }

      if (this.nodeNetworkManager.getCluster().getSelfNode().getName().equals(
              processInformation.getParent())) {
        DefaultChannelManager.INSTANCE.get(processInformation.getName())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.SET_RESOURCE_PACK,
                           Arrays.asList(player, pack))));
      } else {
        DefaultChannelManager.INSTANCE.get(processInformation.getParent())
            .ifPresent(e
                       -> e.sendPacket(new NodeAPIAction(
                           NodeAPIAction.APIAction.SET_RESOURCE_PACK,
                           Arrays.asList(player, pack))));
      }

      task.complete(null);
    });
    return task;
  }

  @Override
  public void sendMessage(@Nonnull UUID player, @Nonnull String message) {
    sendMessageAsync(player, message).awaitUninterruptedly();
  }

  @Override
  public void kickPlayer(@Nonnull UUID player, @Nonnull String message) {
    kickPlayerAsync(player, message).awaitUninterruptedly();
  }

  @Override
  public void kickPlayerFromServer(@Nonnull UUID player,
                                   @Nonnull String message) {
    kickPlayerFromServerAsync(player, message).awaitUninterruptedly();
  }

  @Override
  public void playSound(@Nonnull UUID player, @Nonnull String sound, float f1,
                        float f2) {
    playSoundAsync(player, sound, f1, f2).awaitUninterruptedly();
  }

  @Override
  public void sendTitle(@Nonnull UUID player, @Nonnull String title,
                        @Nonnull String subTitle, int fadeIn, int stay,
                        int fadeOut) {
    sendTitleAsync(player, title, subTitle, fadeIn, stay, fadeOut)
        .awaitUninterruptedly();
  }

  @Override
  public void playEffect(@Nonnull UUID player, @Nonnull String entityEffect) {
    playEffectAsync(player, entityEffect).awaitUninterruptedly();
  }

  @Override
  public <T> void playEffect(@Nonnull UUID player, @Nonnull String effect,
                             T data) {
    playEffectAsync(player, effect, data).awaitUninterruptedly();
  }

  @Override
  public void respawn(@Nonnull UUID player) {
    respawnAsync(player).awaitUninterruptedly();
  }

  @Override
  public void teleport(@Nonnull UUID player, @Nonnull String world, double x,
                       double y, double z, float yaw, float pitch) {
    teleportAsync(player, world, x, y, z, yaw, pitch).awaitUninterruptedly();
  }

  @Override
  public void connect(@Nonnull UUID player, @Nonnull String server) {
    connectAsync(player, server).awaitUninterruptedly();
  }

  @Override
  public void connect(@Nonnull UUID player,
                      @Nonnull ProcessInformation server) {
    connectAsync(player, server).awaitUninterruptedly();
  }

  @Override
  public void connect(@Nonnull UUID player, @Nonnull UUID target) {
    connectAsync(player, target).awaitUninterruptedly();
  }

  @Override
  public void setResourcePack(@Nonnull UUID player, @Nonnull String pack) {
    setResourcePackAsync(player, pack).awaitUninterruptedly();
  }

  private ProcessInformation getPlayerOnProxy(UUID uniqueID) {
    return Links.filter(
        this.nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(),
        processInformation
        -> !processInformation.getTemplate().isServer() &&
               Links
                   .filterToReference(
                       processInformation.getOnlinePlayers(),
                       player -> player.getUniqueID().equals(uniqueID))
                   .isPresent());
  }

  private ProcessInformation getPlayerOnServer(UUID uniqueID) {
    return Links.filter(
        this.nodeNetworkManager.getNodeProcessHelper().getClusterProcesses(),
        processInformation
        -> processInformation.getTemplate().isServer() &&
               Links
                   .filterToReference(
                       processInformation.getOnlinePlayers(),
                       player -> player.getUniqueID().equals(uniqueID))
                   .isPresent());
  }
}
