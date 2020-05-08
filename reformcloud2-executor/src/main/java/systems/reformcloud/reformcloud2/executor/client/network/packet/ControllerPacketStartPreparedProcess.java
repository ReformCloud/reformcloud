package systems.reformcloud.reformcloud2.executor.client.network.packet;

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
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.process.ProcessQueue;

public class ControllerPacketStartPreparedProcess extends Packet {

    public ControllerPacketStartPreparedProcess() {
    }

    public ControllerPacketStartPreparedProcess(ProcessInformation processInformation) {
        this.processInformation = processInformation;
    }

    private ProcessInformation processInformation;

    @Override
    public int getId() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 14;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (this.processInformation.getProcessDetail().getProcessState().equals(ProcessState.PREPARED)) {
            ClientExecutor.getInstance()
                    .getProcessManager()
                    .getProcess(this.processInformation.getProcessDetail().getProcessUniqueID())
                    .ifPresent(ProcessQueue::queue);
        }
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.processInformation);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processInformation = buffer.readObject(ProcessInformation.class);
    }
}
