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
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.UUID;

public class PacketAPIActionSendTitle extends Packet {

    public PacketAPIActionSendTitle() {
    }

    public PacketAPIActionSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        this.player = player;
        this.title = title;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    private UUID player;

    private String title;

    private String subTitle;

    private int fadeIn;

    private int stay;

    private int fadeOut;

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 104;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        ExecutorAPI.getInstance().getSyncAPI().getPlayerSyncAPI().sendTitle(this.player, this.title, this.subTitle, this.fadeIn, this.stay, this.fadeOut);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.player);
        buffer.writeString(this.title);
        buffer.writeString(this.subTitle);
        buffer.writeInt(this.fadeIn);
        buffer.writeInt(this.stay);
        buffer.writeInt(this.fadeOut);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.player = buffer.readUniqueId();
        this.title = buffer.readString();
        this.subTitle = buffer.readString();
        this.fadeIn = buffer.readInt();
        this.stay = buffer.readInt();
        this.fadeOut = buffer.readInt();
    }
}
