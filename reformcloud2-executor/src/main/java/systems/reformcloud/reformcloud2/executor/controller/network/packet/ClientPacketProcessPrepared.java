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

import java.util.UUID;

public class ClientPacketProcessPrepared implements Packet {

    public ClientPacketProcessPrepared() {
    }

    public ClientPacketProcessPrepared(String processName, UUID processUniqueID, String template) {
        this.processName = processName;
        this.processUniqueID = processUniqueID;
        this.template = template;
    }

    private String processName;

    private UUID processUniqueID;

    private String template;

    @Override
    public int getId() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 21;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (sender == null) {
            return;
        }

        System.out.println(LanguageManager.get(
                "process-prepared",
                this.processName,
                this.processUniqueID,
                this.template,
                sender.getName()
        ));
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.processName);
        buffer.writeUniqueId(this.processUniqueID);
        buffer.writeString(this.template);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processName = buffer.readString();
        this.processUniqueID = buffer.readUniqueId();
        this.template = buffer.readString();
    }
}
