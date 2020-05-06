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
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.process.ProcessQueue;
import systems.reformcloud.reformcloud2.executor.controller.network.packet.ClientPacketProcessStopped;

import java.util.UUID;

public class ControllerPacketStopProcess extends Packet {

    public ControllerPacketStopProcess() {
    }

    public ControllerPacketStopProcess(UUID processUniqueID) {
        this.processUniqueID = processUniqueID;
    }

    private UUID processUniqueID;

    @Override
    public int getId() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 3;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (sender == null) {
            return;
        }

        // Check if process is currently queued - if yes remove it from queue and send the success packet
        ProcessInformation queued = ProcessQueue.removeFromQueue(this.processUniqueID);
        if (queued != null) {
            sender.sendPacket(new ClientPacketProcessStopped(
                    queued.getProcessDetail().getProcessUniqueID(),
                    queued.getProcessDetail().getName()
            ));
            return;
        }

        ClientExecutor.getInstance().getProcessManager().getProcess(this.processUniqueID).ifPresent(RunningProcess::shutdown);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.processUniqueID);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processUniqueID = buffer.readUniqueId();
    }
}
