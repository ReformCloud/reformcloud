package systems.reformcloud.reformcloud2.executor.api.common.network.challenge;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public interface ChallengeAuthHandler {

    boolean handle(@NotNull ChannelHandlerContext channelHandlerContext, @NotNull Packet input, @NotNull String name);

    default void handleChannelActive(@NotNull ChannelHandlerContext channelHandlerContext) {
    }
}
