package systems.reformcloud.reformcloud2.executor.api.common.network.packet;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;

import java.util.UUID;

public abstract class Packet implements SerializableObject {

    protected UUID queryUniqueID;

    public abstract int getId();

    @Nullable
    public UUID getQueryUniqueID() {
        return this.queryUniqueID;
    }

    public void setQueryUniqueID(@Nullable UUID queryUniqueID) {
        this.queryUniqueID = queryUniqueID;
    }

    public abstract void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler,
                                             @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel);

}
