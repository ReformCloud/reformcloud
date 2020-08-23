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
package systems.reformcloud.reformcloud2.node.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.wrappers.PlayerWrapper;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.protocol.*;
import systems.reformcloud.reformcloud2.protocol.shared.*;

import java.util.Optional;
import java.util.UUID;

public class DefaultNodePlayerWrapper implements PlayerWrapper {

    DefaultNodePlayerWrapper(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    private final UUID uniqueId;

    @NotNull
    private Optional<Duo<UUID, UUID>> getPlayerProcess() {
        UUID proxy = null;
        UUID server = null;

        for (ProcessInformation process : ExecutorAPI.getInstance().getProcessProvider().getProcesses()) {
            if (process.getProcessDetail().getTemplate().isServer()
                    && process.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(this.uniqueId)
                    && server == null) {
                server = process.getProcessDetail().getProcessUniqueID();
            } else if (!process.getProcessDetail().getTemplate().isServer()
                    && process.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(this.uniqueId)
                    && proxy == null) {
                proxy = process.getProcessDetail().getProcessUniqueID();
            }
        }

        return proxy == null || server == null ? Optional.empty() : Optional.of(new Duo<>(proxy, server));
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
        ProcessInformation proxy = this.getPlayerProxy();
        if (proxy == null) {
            return;
        }

        if (proxy.getProcessDetail().getParentUniqueID().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
            this.sendPacketToPlayerProxy(new PacketSendPlayerMessage(this.uniqueId, message));
        } else {
            this.sendPacketToParent(proxy, new NodeToNodeSendPlayerMessage(this.uniqueId, message));
        }
    }

    @Override
    public void disconnect(@NotNull String kickReason) {
        ProcessInformation proxy = this.getPlayerProxy();
        if (proxy == null) {
            return;
        }

        if (proxy.getProcessDetail().getParentUniqueID().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
            this.sendPacketToPlayerProxy(new PacketDisconnectPlayer(this.uniqueId, kickReason));
        } else {
            this.sendPacketToParent(proxy, new NodeToNodeDisconnectPlayer(this.uniqueId, kickReason));
        }
    }

    @Override
    public void playSound(@NotNull String sound, float volume, float pitch) {
        ProcessInformation server = this.getPlayerServer();
        if (server == null) {
            return;
        }

        if (server.getProcessDetail().getParentUniqueID().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
            this.sendPacketToPlayerProxy(new PacketPlaySoundToPlayer(this.uniqueId, sound, volume, pitch));
        } else {
            this.sendPacketToParent(server, new NodeToNodePlaySoundToPlayer(this.uniqueId, sound, volume, pitch));
        }
    }

    @Override
    public void sendTitle(@NotNull String title, @NotNull String subTitle, int fadeIn, int stay, int fadeOut) {
        ProcessInformation proxy = this.getPlayerProxy();
        if (proxy == null) {
            return;
        }

        if (proxy.getProcessDetail().getParentUniqueID().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
            this.sendPacketToPlayerProxy(new PacketSendPlayerTitle(this.uniqueId, title, subTitle, fadeIn, stay, fadeOut));
        } else {
            this.sendPacketToParent(proxy, new NodeToNodeSendPlayerTitle(this.uniqueId, title, subTitle, fadeIn, stay, fadeOut));
        }
    }

    @Override
    public void playEffect(@NotNull String effect) {
        ProcessInformation server = this.getPlayerServer();
        if (server == null) {
            return;
        }

        if (server.getProcessDetail().getParentUniqueID().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
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

        if (server.getProcessDetail().getParentUniqueID().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
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

        if (proxy.getProcessDetail().getParentUniqueID().equals(NodeExecutor.getInstance().getNodeConfig().getUniqueID())) {
            this.sendPacketToPlayerProxy(new PacketConnectPlayerToServer(this.uniqueId, server));
        } else {
            this.sendPacketToParent(proxy, new NodeToNodeSendPlayerToServer(this.uniqueId, server));
        }
    }

    @Override
    public void connect(@NotNull UUID otherPlayer) {
        for (ProcessInformation process : ExecutorAPI.getInstance().getProcessProvider().getProcesses()) {
            if (process.getProcessDetail().getTemplate().isServer()
                    && process.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(otherPlayer)) {
                this.connect(process.getProcessDetail().getName());
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
                .getChannel(proxy.getProcessDetail().getName())
                .ifPresent(channel -> channel.sendPacket(packet));
    }

    private void sendPacketToParent(@NotNull ProcessInformation processInformation, @NotNull Packet packet) {
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class)
                .getChannel(processInformation.getProcessDetail().getParentName())
                .ifPresent(channel -> channel.sendPacket(packet));
    }

    private @Nullable ProcessInformation getPlayerProxy() {
        for (ProcessInformation process : ExecutorAPI.getInstance().getProcessProvider().getProcesses()) {
            if (!process.getProcessDetail().getTemplate().isServer()
                    && process.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(this.uniqueId)) {
                return process;
            }
        }

        return null;
    }

    private @Nullable ProcessInformation getPlayerServer() {
        for (ProcessInformation process : ExecutorAPI.getInstance().getProcessProvider().getProcesses()) {
            if (process.getProcessDetail().getTemplate().isServer()
                    && process.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(this.uniqueId)) {
                return process;
            }
        }

        return null;
    }
}
