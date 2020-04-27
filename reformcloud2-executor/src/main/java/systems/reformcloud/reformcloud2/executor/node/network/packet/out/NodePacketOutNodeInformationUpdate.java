package systems.reformcloud.reformcloud2.executor.node.network.packet.out;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

public class NodePacketOutNodeInformationUpdate implements Packet {

    public NodePacketOutNodeInformationUpdate() {
    }

    public NodePacketOutNodeInformationUpdate(NodeInformation nodeInformation) {
        this.nodeInformation = nodeInformation;
    }

    private NodeInformation nodeInformation;

    @Override
    public int getId() {
        return NetworkUtil.NODE_TO_NODE_BUS + 9;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        NodeExecutor.getInstance().getClusterSyncManager().handleNodeInformationUpdate(this.nodeInformation);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.nodeInformation);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.nodeInformation = buffer.readObject(NodeInformation.class);
    }
}
