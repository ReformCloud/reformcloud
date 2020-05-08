package systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster;

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
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.Collection;

public class PacketOutSyncProcessInformation extends Packet {

    public PacketOutSyncProcessInformation() {
    }

    public PacketOutSyncProcessInformation(Collection<ProcessInformation> processInformation) {
        this.processInformation = processInformation;
    }

    private Collection<ProcessInformation> processInformation;

    @Override
    public int getId() {
        return NetworkUtil.NODE_TO_NODE_BUS + 12;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        NodeExecutor.getInstance().getClusterSyncManager().handleProcessInformationSync(this.processInformation);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObjects(this.processInformation);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processInformation = buffer.readObjects(ProcessInformation.class);
    }
}
