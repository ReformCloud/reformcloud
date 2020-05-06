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

public class NodePacketOutExecuteCommand extends Packet {

    public NodePacketOutExecuteCommand() {
    }

    public NodePacketOutExecuteCommand(String processName, String command) {
        this.processName = processName;
        this.command = command;
    }

    private String processName;

    private String command;

    @Override
    public int getId() {
        return NetworkUtil.NODE_TO_NODE_BUS + 14;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        Streams.filterToReference(
                LocalProcessManager.getNodeProcesses(),
                e -> e.getProcessInformation().getProcessDetail().getName().equals(this.processName)
        ).ifPresent(runningProcess -> runningProcess.sendCommand(this.command));
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.processName);
        buffer.writeString(this.command);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processName = buffer.readString();
        this.command = buffer.readString();
    }
}
