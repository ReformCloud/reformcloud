package systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.SyncAction;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.Collection;

public class PacketOutSyncProcessGroups extends Packet {

    public PacketOutSyncProcessGroups() {
    }

    public PacketOutSyncProcessGroups(Collection<ProcessGroup> processGroups, SyncAction syncAction) {
        this.processGroups = processGroups;
        this.syncAction = syncAction;
    }

    private Collection<ProcessGroup> processGroups;

    private SyncAction syncAction;

    @Override
    public int getId() {
        return NetworkUtil.NODE_TO_NODE_BUS + 3;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        NodeExecutor.getInstance().getClusterSyncManager().handleProcessGroupSync(this.processGroups, this.syncAction);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObjects(this.processGroups);
        buffer.writeVarInt(this.syncAction.ordinal());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processGroups = buffer.readObjects(ProcessGroup.class);
        this.syncAction = SyncAction.values()[buffer.readVarInt()];
    }
}
