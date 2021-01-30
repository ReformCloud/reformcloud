/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.node.player;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.network.channel.manager.ChannelManager;
import systems.reformcloud.network.packet.Packet;
import systems.reformcloud.node.NodeExecutor;
import systems.reformcloud.node.protocol.NodeToNodeDisconnectPlayer;
import systems.reformcloud.node.protocol.NodeToNodePlayEffectToPlayer;
import systems.reformcloud.node.protocol.NodeToNodePlaySoundToPlayer;
import systems.reformcloud.node.protocol.NodeToNodeSendPlayerActionbar;
import systems.reformcloud.node.protocol.NodeToNodeSendPlayerBossBar;
import systems.reformcloud.node.protocol.NodeToNodeSendPlayerMessage;
import systems.reformcloud.node.protocol.NodeToNodeSendPlayerTitle;
import systems.reformcloud.node.protocol.NodeToNodeSendPlayerToServer;
import systems.reformcloud.node.protocol.NodeToNodeSetPlayerLocation;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.protocol.shared.PacketConnectPlayerToServer;
import systems.reformcloud.protocol.shared.PacketDisconnectPlayer;
import systems.reformcloud.protocol.shared.PacketPlayEffectToPlayer;
import systems.reformcloud.protocol.shared.PacketPlaySoundToPlayer;
import systems.reformcloud.protocol.shared.PacketSendActionBar;
import systems.reformcloud.protocol.shared.PacketSendBossBar;
import systems.reformcloud.protocol.shared.PacketSendPlayerMessage;
import systems.reformcloud.protocol.shared.PacketSendPlayerTitle;
import systems.reformcloud.protocol.shared.PacketSetPlayerLocation;
import systems.reformcloud.shared.collect.Entry2;
import systems.reformcloud.task.Task;
import systems.reformcloud.wrappers.PlayerWrapper;
import systems.reformcloud.wrappers.ProcessWrapper;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class DefaultNodePlayerWrapper implements PlayerWrapper {

  private final UUID uniqueId;

  DefaultNodePlayerWrapper(UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  @NotNull
  private Optional<Entry2<UUID, UUID>> getPlayerProcess() {
    UUID proxy = null;
    UUID server = null;

    for (ProcessInformation process : ExecutorAPI.getInstance().getProcessProvider().getProcesses()) {
      if (process.getPrimaryTemplate().getVersion().getVersionType().isServer()
        && process.getPlayerByUniqueId(this.uniqueId).isPresent()
        && server == null
      ) {
        server = process.getId().getUniqueId();
      } else if (process.getPrimaryTemplate().getVersion().getVersionType().isProxy()
        && process.getPlayerByUniqueId(this.uniqueId).isPresent()
        && proxy == null
      ) {
        proxy = process.getId().getUniqueId();
      }
    }

    return proxy == null || server == null ? Optional.empty() : Optional.of(new Entry2<>(proxy, server));
  }

  @Override
  public @NotNull Task<Optional<ProcessInformation>> getConnectedProxy() {
    return Task.supply(() -> this.getPlayerProcess()
      .flatMap(duo -> ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(duo.getFirst()))
      .map(ProcessWrapper::getProcessInformation));
  }

  @Override
  public @NotNull Task<Optional<ProcessInformation>> getConnectedServer() {
    return Task.supply(() -> this.getPlayerProcess()
      .flatMap(duo -> ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(duo.getSecond()))
      .map(ProcessWrapper::getProcessInformation));
  }

  @Override
  public @NotNull Optional<UUID> getConnectedProxyUniqueId() {
    return this.getPlayerProcess().map(Entry2::getFirst);
  }

  @Override
  public @NotNull Optional<UUID> getConnectedServerUniqueId() {
    return this.getPlayerProcess().map(Entry2::getSecond);
  }

  @Override
  public void sendMessage(@NotNull Component message) {
    final ProcessInformation proxy = this.getPlayerProxy();
    if (proxy != null) {
      if (proxy.getId().getNodeUniqueId().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
        this.sendPacketToPlayerProxy(new PacketSendPlayerMessage(this.uniqueId, GsonComponentSerializer.gson().serialize(message)));
      } else {
        this.sendPacketToParent(proxy, new NodeToNodeSendPlayerMessage(this.uniqueId, GsonComponentSerializer.gson().serialize(message)));
      }
    }
  }

  @Override
  public void disconnect(@NotNull Component kickReason) {
    final ProcessInformation proxy = this.getPlayerProxy();
    if (proxy != null) {
      if (proxy.getId().getNodeUniqueId().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
        this.sendPacketToPlayerProxy(new PacketDisconnectPlayer(this.uniqueId, GsonComponentSerializer.gson().serialize(kickReason)));
      } else {
        this.sendPacketToParent(proxy, new NodeToNodeDisconnectPlayer(this.uniqueId, GsonComponentSerializer.gson().serialize(kickReason)));
      }
    }
  }

  @Override
  public void playSound(@NotNull String sound, float volume, float pitch) {
    ProcessInformation server = this.getPlayerServer();
    if (server == null) {
      return;
    }

    if (server.getId().getUniqueId().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
      this.sendPacketToPlayerProxy(new PacketPlaySoundToPlayer(this.uniqueId, sound, volume, pitch));
    } else {
      this.sendPacketToParent(server, new NodeToNodePlaySoundToPlayer(this.uniqueId, sound, volume, pitch));
    }
  }

  @Override
  public void sendTitle(@NotNull Title title) {
    final ProcessInformation proxy = this.getPlayerProxy();
    if (proxy != null) {
      final Title.Times times = title.times();

      if (proxy.getId().getUniqueId().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
        this.sendPacketToPlayerProxy(new PacketSendPlayerTitle(
          this.uniqueId,
          GsonComponentSerializer.gson().serialize(title.title()),
          GsonComponentSerializer.gson().serialize(title.subtitle()),
          times == null ? 0 : times.fadeIn().toMillis(),
          times == null ? 0 : times.stay().toMillis(),
          times == null ? 0 : times.fadeOut().toMillis()
        ));
      } else {
        this.sendPacketToParent(proxy, new NodeToNodeSendPlayerTitle(
          this.uniqueId,
          GsonComponentSerializer.gson().serialize(title.title()),
          GsonComponentSerializer.gson().serialize(title.subtitle()),
          times == null ? 0 : times.fadeIn().toMillis(),
          times == null ? 0 : times.stay().toMillis(),
          times == null ? 0 : times.fadeOut().toMillis()
        ));
      }
    }
  }

  @Override
  public void sendActionBar(@NotNull Component actionBar) {
    final ProcessInformation proxy = this.getPlayerProxy();
    if (proxy != null) {
      if (proxy.getId().getNodeUniqueId().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
        this.sendPacketToPlayerProxy(new PacketSendActionBar(this.uniqueId, GsonComponentSerializer.gson().serialize(actionBar)));
      } else {
        this.sendPacketToParent(proxy, new NodeToNodeSendPlayerActionbar(this.uniqueId, GsonComponentSerializer.gson().serialize(actionBar)));
      }
    }
  }

  @Override
  public void sendBossBar(@NotNull BossBar bossBar) {
    this.sendBossBar(bossBar, false);
  }

  @Override
  public void hideBossBar(@NotNull BossBar bossBar) {
    this.sendBossBar(bossBar, true);
  }

  private void sendBossBar(@NotNull BossBar bossBar, boolean hide) {
    final ProcessInformation proxy = this.getPlayerProxy();
    if (proxy != null) {
      if (proxy.getId().getNodeUniqueId().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
        this.sendPacketToPlayerProxy(new PacketSendBossBar(
          this.uniqueId,
          GsonComponentSerializer.gson().serialize(bossBar.name()),
          bossBar.progress(),
          bossBar.color().ordinal(),
          bossBar.overlay().ordinal(),
          bossBar.flags().stream().map(Enum::ordinal).collect(Collectors.toSet()),
          hide
        ));
      } else {
        this.sendPacketToParent(proxy, new NodeToNodeSendPlayerBossBar(
          this.uniqueId,
          GsonComponentSerializer.gson().serialize(bossBar.name()),
          bossBar.progress(),
          bossBar.color().ordinal(),
          bossBar.overlay().ordinal(),
          bossBar.flags().stream().map(Enum::ordinal).collect(Collectors.toSet()),
          hide
        ));
      }
    }
  }

  @Override
  public void playEffect(@NotNull String effect) {
    ProcessInformation server = this.getPlayerServer();
    if (server == null) {
      return;
    }

    if (server.getId().getNodeUniqueId().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
      this.sendPacketToPlayerProxy(new PacketPlayEffectToPlayer(this.uniqueId, effect));
    } else {
      this.sendPacketToParent(server, new NodeToNodePlayEffectToPlayer(this.uniqueId, effect));
    }
  }

  @Override
  public void setLocation(@NotNull String world, double x, double y, double z, float yaw, float pitch) {
    ProcessInformation server = this.getPlayerServer();
    if (server == null) {
      return;
    }

    if (server.getId().getNodeUniqueId().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
      this.sendPacketToPlayerProxy(new PacketSetPlayerLocation(this.uniqueId, world, x, y, z, yaw, pitch));
    } else {
      this.sendPacketToParent(server, new NodeToNodeSetPlayerLocation(this.uniqueId, world, x, y, z, yaw, pitch));
    }
  }

  @Override
  public void connect(@NotNull String server) {
    ProcessInformation proxy = this.getPlayerProxy();
    if (proxy == null) {
      return;
    }

    if (proxy.getId().getNodeUniqueId().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
      this.sendPacketToPlayerProxy(new PacketConnectPlayerToServer(this.uniqueId, server));
    } else {
      this.sendPacketToParent(proxy, new NodeToNodeSendPlayerToServer(this.uniqueId, server));
    }
  }

  @Override
  public void connect(@NotNull UUID otherPlayer) {
    for (ProcessInformation process : ExecutorAPI.getInstance().getProcessProvider().getProcesses()) {
      if (process.getPrimaryTemplate().getVersion().getVersionType().isServer()
        && process.getPlayerByUniqueId(otherPlayer).isPresent()
      ) {
        this.connect(process.getName());
        break;
      }
    }
  }

  private void sendPacketToPlayerProxy(@NotNull Packet packet) {
    ProcessInformation proxy = this.getPlayerProxy();
    if (proxy == null) {
      return;
    }

    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class)
      .getChannel(proxy.getName())
      .ifPresent(channel -> channel.sendPacket(packet));
  }

  private void sendPacketToParent(@NotNull ProcessInformation processInformation, @NotNull Packet packet) {
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class)
      .getChannel(processInformation.getId().getNodeName())
      .ifPresent(channel -> channel.sendPacket(packet));
  }

  private @Nullable ProcessInformation getPlayerProxy() {
    for (ProcessInformation process : ExecutorAPI.getInstance().getProcessProvider().getProcesses()) {
      if (!process.getPrimaryTemplate().getVersion().getVersionType().isServer()
        && process.getPlayerByUniqueId(this.uniqueId).isPresent()
      ) {
        return process;
      }
    }

    return null;
  }

  private @Nullable ProcessInformation getPlayerServer() {
    for (ProcessInformation process : ExecutorAPI.getInstance().getProcessProvider().getProcesses()) {
      if (process.getPrimaryTemplate().getVersion().getVersionType().isServer()
        && process.getPlayerByUniqueId(this.uniqueId).isPresent()
      ) {
        return process;
      }
    }

    return null;
  }
}
