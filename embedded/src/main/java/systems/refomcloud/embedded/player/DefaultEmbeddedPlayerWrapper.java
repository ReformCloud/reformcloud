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
package systems.refomcloud.embedded.player;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.Embedded;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.protocol.node.ApiToNodeConnectPlayerToPlayer;
import systems.reformcloud.protocol.node.ApiToNodeGetCurrentPlayerProcessUniqueIds;
import systems.reformcloud.protocol.node.ApiToNodeGetCurrentPlayerProcessUniqueIdsResult;
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

public class DefaultEmbeddedPlayerWrapper implements PlayerWrapper {

  private final UUID playerUniqueId;

  DefaultEmbeddedPlayerWrapper(UUID playerUniqueId) {
    this.playerUniqueId = playerUniqueId;
  }

  @NotNull
  private Optional<Entry2<UUID, UUID>> getPlayerProcess() {
    return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetCurrentPlayerProcessUniqueIds(this.playerUniqueId))
      .map(result -> {
        if (result instanceof ApiToNodeGetCurrentPlayerProcessUniqueIdsResult) {
          return Optional.ofNullable(((ApiToNodeGetCurrentPlayerProcessUniqueIdsResult) result).getResult());
        }

        return Optional.<Entry2<UUID, UUID>>empty();
      }).orElseGet(Optional::empty);
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
    Embedded.getInstance().sendPacket(new PacketSendPlayerMessage(this.playerUniqueId, GsonComponentSerializer.gson().serialize(message)));
  }

  @Override
  public void disconnect(@NotNull Component kickReason) {
    Embedded.getInstance().sendPacket(new PacketDisconnectPlayer(this.playerUniqueId, GsonComponentSerializer.gson().serialize(kickReason)));
  }

  @Override
  public void playSound(@NotNull String sound, float volume, float pitch) {
    Embedded.getInstance().sendPacket(new PacketPlaySoundToPlayer(this.playerUniqueId, sound, volume, pitch));
  }

  @Override
  public void sendTitle(@NotNull Title title) {
    final Title.Times times = title.times();
    Embedded.getInstance().sendPacket(new PacketSendPlayerTitle(
      this.playerUniqueId,
      GsonComponentSerializer.gson().serialize(title.title()),
      GsonComponentSerializer.gson().serialize(title.subtitle()),
      times == null ? 0 : times.fadeIn().toMillis(),
      times == null ? 0 : times.stay().toMillis(),
      times == null ? 0 : times.fadeOut().toMillis()
    ));
  }

  @Override
  public void sendActionBar(@NotNull Component actionBar) {
    Embedded.getInstance().sendPacket(new PacketSendActionBar(this.playerUniqueId, GsonComponentSerializer.gson().serialize(actionBar)));
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
    Embedded.getInstance().sendPacket(new PacketSendBossBar(
      this.playerUniqueId,
      GsonComponentSerializer.gson().serialize(bossBar.name()),
      bossBar.progress(),
      bossBar.color().ordinal(),
      bossBar.overlay().ordinal(),
      bossBar.flags().stream().map(Enum::ordinal).collect(Collectors.toSet()),
      hide
    ));
  }

  @Override
  public void playEffect(@NotNull String effect) {
    Embedded.getInstance().sendPacket(new PacketPlayEffectToPlayer(this.playerUniqueId, effect));
  }

  @Override
  public void setLocation(@NotNull String world, double x, double y, double z, float yaw, float pitch) {
    Embedded.getInstance().sendPacket(new PacketSetPlayerLocation(this.playerUniqueId, world, x, y, z, yaw, pitch));
  }

  @Override
  public void connect(@NotNull String server) {
    Embedded.getInstance().sendPacket(new PacketConnectPlayerToServer(this.playerUniqueId, server));
  }

  @Override
  public void connect(@NotNull UUID otherPlayer) {
    Embedded.getInstance().sendPacket(new ApiToNodeConnectPlayerToPlayer(this.playerUniqueId, otherPlayer));
  }
}
