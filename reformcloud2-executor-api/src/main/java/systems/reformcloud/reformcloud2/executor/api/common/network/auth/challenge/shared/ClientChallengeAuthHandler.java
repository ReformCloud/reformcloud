package systems.reformcloud.reformcloud2.executor.api.common.network.auth.challenge.shared;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.challenge.security.ChallengeSecurity;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ClientChallengeAuthHandler implements ChallengeAuthHandler {

    public ClientChallengeAuthHandler(String key, String name, Supplier<JsonConfiguration> supplier, Consumer<ChannelHandlerContext> consumer) {
        this.key = key;
        this.name = name;
        this.supplier = supplier;
        this.channelHandlerContextConsumer = consumer;
    }

    private final String key;

    private final String name;

    private final Supplier<JsonConfiguration> supplier;

    private final Consumer<ChannelHandlerContext> channelHandlerContextConsumer;

    @Override
    public boolean handle(@Nonnull ChannelHandlerContext channelHandlerContext, @Nonnull Packet input, @Nonnull String name) {
        if (input.packetID() == -512) {
            // challenge from server
            byte[] bytes = input.content().get("challenge", byte[].class);
            if (bytes == null) {
                channelHandlerContext.channel().close().syncUninterruptibly();
                return false;
            }

            String result = ChallengeSecurity.decodeChallengeRequest(this.key, bytes);
            if (result == null) {
                channelHandlerContext.channel().close().syncUninterruptibly();
                return false;
            }

            String hashed = ChallengeSecurity.hash(result);
            if (hashed == null) {
                channelHandlerContext.channel().close().syncUninterruptibly();
                return false;
            }

            channelHandlerContext.channel().writeAndFlush(new JsonPacket(
                    -511, supplier.get().add("result", hashed)
            )).syncUninterruptibly();
            return false;
        }

        if (input.packetID() == -511 && input.content().getBoolean("access")) {
            this.channelHandlerContextConsumer.accept(channelHandlerContext);
            return true;
        }

        return false;
    }

    @Override
    public void handleChannelActive(@Nonnull ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.channel().writeAndFlush(new JsonPacket(
                -512, new JsonConfiguration().add("name", this.name)
        )).syncUninterruptibly();
    }
}
