package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query;

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
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;

public class PacketAPIQueryProcessStartNew extends Packet {

    public PacketAPIQueryProcessStartNew() {
    }

    public PacketAPIQueryProcessStartNew(ProcessConfiguration processConfiguration, boolean start) {
        this.processConfiguration = processConfiguration;
        this.start = start;
    }

    private ProcessConfiguration processConfiguration;

    private boolean start;

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 31;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (sender == null) {
            return;
        }

        ProcessInformation processInformation;
        if (this.start) {
            processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(this.processConfiguration);
        } else {
            processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().prepareProcess(this.processConfiguration);
        }

        sender.sendQueryResult(
                this.getQueryUniqueID(),
                new PacketAPIQueryProcessStartNewResult(processInformation)
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
