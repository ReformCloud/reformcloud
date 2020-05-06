package systems.reformcloud.reformcloud2.executor.controller.network.packet;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;

import java.util.UUID;

public class ClientPacketProcessStopped extends Packet {

    public ClientPacketProcessStopped() {
    }

    public ClientPacketProcessStopped(UUID processUniqueID, String processName) {
        this.processUniqueID = processUniqueID;
        this.processName = processName;
    }

    private UUID processUniqueID;

    private String processName;

    @Override
    public int getId() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 1;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        ControllerExecutor.getInstance().getProcessManager().unregisterProcess(this.processUniqueID);
        if (sender == null) {
            return;
        }

        System.out.println(LanguageManager.get(
                "process-stopped",
                this.processName,
                this.processUniqueID,
                sender.getName()
        ));
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.processName);
        buffer.writeUniqueId(this.processUniqueID);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processName = buffer.readString();
        this.processUniqueID = buffer.readUniqueId();
    }
}
