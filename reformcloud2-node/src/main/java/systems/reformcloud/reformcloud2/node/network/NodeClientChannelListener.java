/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.reformcloud.reformcloud2.node.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.network.PacketIds;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.channel.shared.SharedChannelListener;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.shared.node.DefaultNodeInformation;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.protocol.shared.PacketAuthBegin;
import systems.reformcloud.reformcloud2.protocol.shared.PacketAuthSuccess;

public final class NodeClientChannelListener extends SharedChannelListener {

    private boolean wasActive = false;

    @Override
    public boolean shouldHandle(@NotNull Packet packet) {
        return super.networkChannel.isAuthenticated() || packet.getId() == PacketIds.AUTH_BUS_END;
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext context) {
        Channel channel = context.channel();
        if (channel.isOpen() && channel.isWritable()) {
            return;
        }

        if (super.networkChannel.getName().isEmpty()) {
            // channel is not authenticated - no need to do anything
            return;
        }

        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).unregisterChannel(super.networkChannel);
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).handleNodeDisconnect(super.networkChannel.getName());
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext context) {
        synchronized (this) {
            if (!this.wasActive) {
                this.wasActive = true;
            } else {
                return;
            }
        }

        context.writeAndFlush(new PacketAuthBegin(
            NodeExecutor.getInstance().getNodeExecutorConfig().getConnectionKey(),
            1,
            new JsonConfiguration().add("node", NodeExecutor.getInstance().getCurrentNodeInformation())
        ), context.voidPromise());
    }

    @Override
    public void handle(@NotNull Packet input) {
        if (input.getId() == PacketIds.AUTH_BUS + 1) {
            if (!(input instanceof PacketAuthSuccess)) {
                // should never happen
                super.networkChannel.close();
                return;
            }

            PacketAuthSuccess packet = (PacketAuthSuccess) input;
            DefaultNodeInformation node = packet.getData().get("node", DefaultNodeInformation.TYPE);
            if (node == null) {
                // invalid result sent by other node
                super.networkChannel.close();
                return;
            }

            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).handleNodeConnect(node);

            super.networkChannel.setName(node.getName());
            super.networkChannel.setAuthenticated(true);

            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).registerChannel(super.networkChannel);

            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishProcessGroupSet(
                ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroups()
            );
            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishMainGroupSet(
                ExecutorAPI.getInstance().getMainGroupProvider().getMainGroups()
            );
            return;
        }

        super.handle(input);
    }
}
