/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.refomcloud.reformcloud2.embedded.player;

import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.wrappers.PlayerWrapper;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeConnectPlayerToPlayer;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetCurrentPlayerProcessUniqueIds;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetCurrentPlayerProcessUniqueIdsResult;
import systems.reformcloud.reformcloud2.protocol.shared.*;

import java.util.Optional;
import java.util.UUID;

public class DefaultEmbeddedPlayerWrapper implements PlayerWrapper {

    DefaultEmbeddedPlayerWrapper(UUID playerUniqueId) {
        this.playerUniqueId = playerUniqueId;
    }

    private final UUID playerUniqueId;

    @NotNull
    private Optional<Duo<UUID, UUID>> getPlayerProcess() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetCurrentPlayerProcessUniqueIds(this.playerUniqueId))
                .map(result -> {
                    if (result instanceof ApiToNodeGetCurrentPlayerProcessUniqueIdsResult) {
                        return Optional.ofNullable(((ApiToNodeGetCurrentPlayerProcessUniqueIdsResult) result).getResult());
                    }

                    return Optional.<Duo<UUID, UUID>>empty();
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
        return this.getPlayerProcess().map(Duo::getFirst);
    }

    @Override
    public @NotNull Optional<UUID> getConnectedServerUniqueId() {
        return this.getPlayerProcess().map(Duo::getSecond);
    }

    @Override
    public void sendMessage(@NotNull String message) {
        Embedded.getInstance().sendPacket(new PacketSendPlayerMessage(this.playerUniqueId, message));
    }

    @Override
    public void disconnect(@NotNull String kickReason) {
        Embedded.getInstance().sendPacket(new PacketDisconnectPlayer(this.playerUniqueId, kickReason));
    }

    @Override
    public void playSound(@NotNull String sound, float volume, float pitch) {
        Embedded.getInstance().sendPacket(new PacketPlaySoundToPlayer(this.playerUniqueId, sound, volume, pitch));
    }

    @Override
    public void sendTitle(@NotNull String title, @NotNull String subTitle, int fadeIn, int stay, int fadeOut) {
        Embedded.getInstance().sendPacket(new PacketSendPlayerTitle(this.playerUniqueId, title, subTitle, fadeIn, stay, fadeOut));
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
