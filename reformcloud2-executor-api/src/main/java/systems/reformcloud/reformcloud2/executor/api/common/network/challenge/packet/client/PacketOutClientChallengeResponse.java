package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.PacketCallable;

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

    @NotNull
    @Override
    public PacketCallable onPacketReceive() {
        return (reader, authHandler, parent, sender) -> {
            authHandler.handle(sender.getChannelContext(), this, this.name);
        };
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
