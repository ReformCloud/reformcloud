package systems.reformcloud.reformcloud2.executor.api.network.packets.out;

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

import java.util.UUID;

public class APIPacketOutPlayerCommandExecute implements Packet {

    public APIPacketOutPlayerCommandExecute(String playerName, UUID playerUniqueID, String command) {
        this.playerName = playerName;
        this.playerUniqueID = playerUniqueID;
        this.command = command;
    }

    protected String playerName;

    protected UUID playerUniqueID;

    protected String command;

    @Override
    public int getId() {
        return NetworkUtil.PLAYER_INFORMATION_BUS + 5;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.playerName);
        buffer.writeUniqueId(this.playerUniqueID);
        buffer.writeString(this.command);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.playerName = buffer.readString();
        this.playerUniqueID = buffer.readUniqueId();
        this.command = buffer.readString();
    }
}
