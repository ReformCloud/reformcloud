package systems.reformcloud.reformcloud2.executor.api.common.network.auth;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.DoubleFunction;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface ServerAuthHandler {

    /**
     * @return The packet handler which should be used to handle the incoming packets after the auth
     */
    @Nonnull
    PacketHandler packetHandler();

    /**
     * @return The consumer which should accept all disconnecting packet sender
     */
    @Nonnull
    Consumer<PacketSender> onDisconnect();

    /**
     * @return Magic value
     */
    @Nonnull
    DoubleFunction<Packet, String, Boolean> function();

    /**
     * @return This function converts the given name and channel to the final {@link PacketSender}
     */
    @Nonnull
    BiFunction<String, ChannelHandlerContext, PacketSender> onSuccess();

    /**
     * @return A consumer which will accept the channel of a failed auth
     */
    @Nonnull
    Consumer<ChannelHandlerContext> onAuthFailure();
}
