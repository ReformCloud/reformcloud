package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;

public class PacketAPIProcessCopyByName extends PacketAPIProcessCopy {

    public PacketAPIProcessCopyByName() {
        super(null, null, null);
    }

    public PacketAPIProcessCopyByName(String processName, String targetTemplate, String targetTemplateStorage, String targetTemplateGroup) {
        super(targetTemplate, targetTemplateStorage, targetTemplateGroup);
        this.processName = processName;
    }

    private String processName;

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 38;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (super.targetTemplate != null && super.targetTemplateGroup != null && super.targetTemplateStorage != null) {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(
                    this.processName,
                    super.targetTemplate,
                    super.targetTemplateStorage,
                    super.targetTemplateGroup
            );
        } else if (super.targetTemplate != null && super.targetTemplateStorage != null) {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(this.processName, super.targetTemplate, super.targetTemplateStorage);
        } else if (super.targetTemplate != null) {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(this.processName, super.targetTemplate);
        } else {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(this.processName);
        }
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        super.write(buffer);
        buffer.writeString(this.processName);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        super.read(buffer);
        this.processName = buffer.readString();
    }
}
