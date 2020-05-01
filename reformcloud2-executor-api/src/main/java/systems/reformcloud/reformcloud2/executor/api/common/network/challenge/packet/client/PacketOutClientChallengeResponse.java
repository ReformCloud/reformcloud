package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public class PacketOutClientChallengeResponse implements Packet {

    public PacketOutClientChallengeResponse() {
    }

    public PacketOutClientChallengeResponse(String name, String hashedResult, JsonConfiguration extraData) {
        this.name = name;
        this.hashedResult = hashedResult;
        this.extraData = extraData;
    }

    private String name;

    private String hashedResult;

    private JsonConfiguration extraData;

    public String getName() {
        return name;
    }

    public String getHashedResult() {
        return hashedResult;
    }

    public JsonConfiguration getExtraData() {
        return extraData;
    }

    @Override
    public int getId() {
        return NetworkUtil.AUTH_BUS + 3;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        parent.auth = authHandler.handle(channel, this, this.name);
        if (parent.auth) {
            reader.setChannelHandlerContext(channel, this.name);
        }
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeString(this.hashedResult);
        buffer.writeArray(this.extraData.toPrettyBytes());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.name = buffer.readString();
        this.hashedResult = buffer.readString();
        this.extraData = new JsonConfiguration(buffer.readArray());
    }
}
