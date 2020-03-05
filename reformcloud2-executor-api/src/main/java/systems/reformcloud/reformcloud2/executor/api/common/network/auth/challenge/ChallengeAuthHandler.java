package systems.reformcloud.reformcloud2.executor.api.common.network.auth.challenge;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;

public interface ChallengeAuthHandler {

    boolean handle(@Nonnull ChannelHandlerContext channelHandlerContext, @Nonnull Packet input, @Nonnull String name);

    default void handleChannelActive(@Nonnull ChannelHandlerContext channelHandlerContext) {
    }
}
