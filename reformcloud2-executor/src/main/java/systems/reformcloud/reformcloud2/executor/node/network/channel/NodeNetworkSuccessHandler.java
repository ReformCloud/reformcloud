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
package systems.reformcloud.reformcloud2.executor.node.network.channel;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client.PacketOutClientChallengeResponse;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.server.PacketOutServerGrantAccess;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutConnectionInitDone;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;

public final class NodeNetworkSuccessHandler implements BiConsumer<ChannelHandlerContext, PacketOutClientChallengeResponse> {

    @Override
    public void accept(ChannelHandlerContext channelHandlerContext, PacketOutClientChallengeResponse packet) {
        ProcessInformation process = NodeExecutor.getInstance()
                .getNodeNetworkManager()
                .getNodeProcessHelper()
                .getLocalCloudProcess(packet.getName());

        if (process == null) {
            channelHandlerContext.channel().writeAndFlush(new PacketOutServerGrantAccess(
                    NodeExecutor.getInstance().getNodeConfig().getName(),
                    true
            )).syncUninterruptibly();
        } else {
            channelHandlerContext.channel().writeAndFlush(new PacketOutServerGrantAccess("Controller", true)).syncUninterruptibly();
        }

        if (process == null) {
            String address = ((InetSocketAddress) channelHandlerContext.channel().remoteAddress()).getAddress().getHostAddress();
            if (NodeExecutor.getInstance().getNodeConfig().getClusterNodes().stream().noneMatch(e -> e.getHost().equals(address))) {
                System.out.println(LanguageManager.get("network-node-connection-from-unknown-node", address));
                channelHandlerContext.channel().close().syncUninterruptibly();
                return;
            }

            NodeInformation nodeInformation = packet.getExtraData().get("info", NodeInformation.TYPE);
            if (nodeInformation == null) {
                System.out.println(LanguageManager.get("network-node-connection-from-unknown-node", address));
                channelHandlerContext.channel().close().syncUninterruptibly();
                return;
            }

            InternalNetworkCluster cluster = NodeExecutor.getInstance().getNodeNetworkManager().getCluster();
            if (cluster.getNode(nodeInformation.getName()) != null || cluster.getNode(nodeInformation.getNodeUniqueID()) != null) {
                System.out.println(LanguageManager.get("network-node-already-connected", nodeInformation.getName(), nodeInformation.getNodeUniqueID()));
                channelHandlerContext.channel().close().syncUninterruptibly();
                return;
            }

            NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getClusterManager().handleConnect(
                    NodeExecutor.getInstance().getNodeNetworkManager().getCluster(),
                    nodeInformation
            );

            channelHandlerContext.channel().writeAndFlush(new NodePacketOutConnectionInitDone(
                    NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode()
            )).syncUninterruptibly();
            NodeExecutor.getInstance().getClusterSyncManager().getWaitingConnections().remove(address);
            NodeExecutor.getInstance().sync(packet.getName());

            System.out.println(LanguageManager.get("network-node-other-node-connected", nodeInformation.getName(), address));
        } else {
            process.getNetworkInfo().setConnected(true);
            process.getProcessDetail().setProcessState(process.getProcessDetail().getInitialState());
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(process);
            NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleProcessConnection(process);

            System.out.println(LanguageManager.get("process-connected", process.getProcessDetail().getName(), process.getProcessDetail().getParentName()));
        }
    }
}
