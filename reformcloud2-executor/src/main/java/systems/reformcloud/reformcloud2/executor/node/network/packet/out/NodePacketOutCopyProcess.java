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
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

import java.util.UUID;

public class NodePacketOutCopyProcess extends Packet {

    public NodePacketOutCopyProcess() {
    }

    public NodePacketOutCopyProcess(UUID processUniqueID, String targetTemplate, String targetTemplateStorage, String targetTemplateGroup) {
        this.processUniqueID = processUniqueID;
        this.targetTemplate = targetTemplate;
        this.targetTemplateStorage = targetTemplateStorage;
        this.targetTemplateGroup = targetTemplateGroup;
    }

    private UUID processUniqueID;

    private String targetTemplate;

    private String targetTemplateStorage;

    private String targetTemplateGroup;

    @Override
    public int getId() {
        return NetworkUtil.NODE_TO_NODE_BUS + 30;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        Streams.filterToReference(
                LocalProcessManager.getNodeProcesses(),
                e -> e.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(this.processUniqueID)
        ).ifPresent(e -> e.copy(this.targetTemplate, this.targetTemplateStorage, this.targetTemplateGroup));
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.processUniqueID);
        buffer.writeString(this.targetTemplate);
        buffer.writeString(this.targetTemplateStorage);
        buffer.writeString(this.targetTemplateGroup);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processUniqueID = buffer.readUniqueId();
        this.targetTemplate = buffer.readString();
        this.targetTemplateStorage = buffer.readString();
        this.targetTemplateGroup = buffer.readString();
    }
}
