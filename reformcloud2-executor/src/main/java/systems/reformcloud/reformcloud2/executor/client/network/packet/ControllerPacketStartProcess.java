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
import systems.reformcloud.reformcloud2.executor.client.process.basic.DefaultRunningProcess;

public class ControllerPacketStartProcess extends Packet {

    public ControllerPacketStartProcess() {
    }

    public ControllerPacketStartProcess(ProcessInformation processInformation, boolean start) {
        this.processInformation = processInformation;
        this.start = start;
    }

    private ProcessInformation processInformation;

    private boolean start;

    @Override
    public int getId() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 2;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (this.start) {
            ProcessQueue.queue(processInformation);
        } else {
            RunningProcess process = new DefaultRunningProcess(processInformation);
            process.prepare().onComplete(e -> ClientExecutor.getInstance().getProcessManager().registerProcess(process));
        }
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.processInformation);
        buffer.writeBoolean(this.start);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processInformation = buffer.readObject(ProcessInformation.class);
        this.start = buffer.readBoolean();
    }
}
