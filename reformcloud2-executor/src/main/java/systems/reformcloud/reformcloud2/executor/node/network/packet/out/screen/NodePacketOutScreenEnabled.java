package systems.reformcloud.reformcloud2.executor.node.network.packet.out.screen;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.Collection;
import java.util.UUID;

public class NodePacketOutScreenEnabled extends Packet {

    public NodePacketOutScreenEnabled() {
    }

    public NodePacketOutScreenEnabled(UUID processUniqueID, Collection<String> lines) {
        this.processUniqueID = processUniqueID;
        this.lines = lines;
    }

    private UUID processUniqueID;

    private Collection<String> lines;

    @Override
    public int getId() {
        return NetworkUtil.NODE_TO_NODE_BUS + 16;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(this.processUniqueID);
        if (processInformation == null) {
            return;
        }

        for (String line : lines) {
            System.out.println(LanguageManager.get("screen-line-added", processInformation.getProcessDetail().getName(), line));
        }
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.processUniqueID);
        buffer.writeStringArray(this.lines);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processUniqueID = buffer.readUniqueId();
        this.lines = buffer.readStringArray();
    }
}
