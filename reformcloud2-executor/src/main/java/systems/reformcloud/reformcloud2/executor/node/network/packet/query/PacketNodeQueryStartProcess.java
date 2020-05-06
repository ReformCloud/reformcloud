package systems.reformcloud.reformcloud2.executor.node.network.packet.query;

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
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

public class PacketNodeQueryStartProcess extends Packet {

    public PacketNodeQueryStartProcess() {
    }

    public PacketNodeQueryStartProcess(ProcessConfiguration processConfiguration, boolean start) {
        this.processConfiguration = processConfiguration;
        this.start = start;
    }

    private ProcessConfiguration processConfiguration;

    private boolean start;

    @Override
    public int getId() {
        return NetworkUtil.NODE_TO_NODE_QUERY_BUS + 1;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (sender == null) {
            return;
        }

        sender.sendQueryResult(
                this.getQueryUniqueID(),
                new PacketNodeQueryStartProcessResult(NodeExecutor.getInstance().getNodeNetworkManager().prepareProcess(this.processConfiguration, this.start))
        );
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.processConfiguration);
        buffer.writeBoolean(this.start);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processConfiguration = buffer.readObject(ProcessConfiguration.class);
        this.start = buffer.readBoolean();
    }
}
