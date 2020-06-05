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
package systems.reformcloud.reformcloud2.node.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.channel.shared.SharedEndpointChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.node.process.DefaultNodeLocalProcessWrapper;
import systems.reformcloud.reformcloud2.protocol.shared.PacketAuthBegin;
import systems.reformcloud.reformcloud2.protocol.shared.PacketAuthSuccess;

import java.util.Optional;
import java.util.UUID;

public class NodeServerEndpointChannelReader extends SharedEndpointChannelReader {

    private int type = 0;

    @Override
    public boolean shouldHandle(@NotNull Packet packet) {
        return super.networkChannel.isAuthenticated() || packet.getId() == NetworkUtil.AUTH_BUS;
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext context) {
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

        if (this.type == 1) {
            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).unregisterChannel(super.networkChannel);
            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).handleNodeDisconnect(super.networkChannel.getName());
        } else if (this.type == 2) {
            ExecutorAPI.getInstance().getProcessProvider().getProcessByName(super.networkChannel.getName())
                    .ifPresent(processWrapper -> processWrapper.setRuntimeState(ProcessState.STOPPED));
        }
    }

    @Override
    public void read(@NotNull Packet input) {
        if (input.getId() == NetworkUtil.AUTH_BUS) {
            if (!(input instanceof PacketAuthBegin)) {
                // should never happen
                super.networkChannel.close();
                return;
            }

            PacketAuthBegin packet = (PacketAuthBegin) input;
            this.type = packet.getType();

            if (packet.getType() == 1) {
                NodeInformation nodeInformation = packet.getData().get("node", NodeInformation.TYPE);
                if (nodeInformation == null) {
                    // invalid type to id
                    super.networkChannel.close();
                    return;
                }

                if (!packet.getConnectionKey().equals(NodeExecutor.getInstance().getNodeExecutorConfig().getConnectionKey())) {
                    // invalid connection key sent by the node
                    super.networkChannel.close();
                    return;
                }

                super.networkChannel.setName(nodeInformation.getName());
                ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).handleNodeConnect(nodeInformation);
            } else if (packet.getType() == 2) {
                UUID processUniqueId = packet.getData().get("pid", UUID.class);
                if (processUniqueId == null) {
                    // invalid data
                    super.networkChannel.close();
                    return;
                }

                Optional<DefaultNodeLocalProcessWrapper> wrapper = NodeExecutor.getInstance().getDefaultNodeProcessProvider().getProcessWrapperByUniqueId(processUniqueId);
                if (!wrapper.isPresent()) {
                    // either the process is not registered or not running on the local node
                    super.networkChannel.close();
                    return;
                }

                DefaultNodeLocalProcessWrapper processWrapper = wrapper.get();
                if (!processWrapper.getConnectionKey().equals(packet.getConnectionKey())) {
                    // the provided connection key by the process is invalid
                    super.networkChannel.close();
                    return;
                }

                ProcessInformation information = processWrapper.getProcessInformation();
                super.networkChannel.setName(information.getProcessDetail().getName());

                information.getNetworkInfo().setConnected(true);
                information.getProcessDetail().setProcessState(information.getProcessDetail().getInitialState());

                ExecutorAPI.getInstance().getProcessProvider().updateProcessInformation(information);
            } else {
                // invalid data
                super.networkChannel.close();
                return;
            }

            super.networkChannel.setAuthenticated(true);
            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).registerChannel(super.networkChannel);

            JsonConfiguration data;
            if (this.type == 1) {
                data = new JsonConfiguration().add("node", NodeExecutor.getInstance().getCurrentNodeInformation());
            } else {
                data = new JsonConfiguration();
            }

            super.networkChannel.sendPacket(new PacketAuthSuccess(data));

            if (this.type == 1) {
                ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishProcessGroupSet(
                        ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroups()
                );
                ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishMainGroupSet(
                        ExecutorAPI.getInstance().getMainGroupProvider().getMainGroups()
                );
                ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishProcessSet(
                        ExecutorAPI.getInstance().getProcessProvider().getProcesses()
                );
            }
            return;
        }

        super.read(input);
    }
}
