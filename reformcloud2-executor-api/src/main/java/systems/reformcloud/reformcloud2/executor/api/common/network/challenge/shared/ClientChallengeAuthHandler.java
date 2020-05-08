package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client.PacketOutClientChallengeRequest;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client.PacketOutClientChallengeResponse;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.server.PacketOutServerChallengeStart;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.server.PacketOutServerGrantAccess;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.security.ChallengeSecurity;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

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
    public boolean handle(@NotNull ChannelHandlerContext channelHandlerContext, @NotNull Packet input, @NotNull String name) {
        if (input instanceof PacketOutServerChallengeStart) {
            PacketOutServerChallengeStart challengeStart = (PacketOutServerChallengeStart) input;

            String result = ChallengeSecurity.decodeChallengeRequest(this.key, challengeStart.getChallenge());
            if (result == null) {
                channelHandlerContext.close().syncUninterruptibly();
                return false;
            }

            String hashed = ChallengeSecurity.hash(result);
            if (hashed == null) {
                channelHandlerContext.close().syncUninterruptibly();
                return false;
            }

            channelHandlerContext.writeAndFlush(new PacketOutClientChallengeResponse(this.name, hashed, this.supplier.get())).syncUninterruptibly();
            return false;
        }

        if (input instanceof PacketOutServerGrantAccess) {
            PacketOutServerGrantAccess packetOutServerGrantAccess = (PacketOutServerGrantAccess) input;
            if (packetOutServerGrantAccess.isAccess()) {
                this.channelHandlerContextConsumer.accept(channelHandlerContext);
            } else {
                channelHandlerContext.channel().close().syncUninterruptibly();
            }

            return packetOutServerGrantAccess.isAccess();
        }

        return false;
    }

    @Override
    public void handleChannelActive(@NotNull ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.channel().writeAndFlush(new PacketOutClientChallengeRequest(this.name)).syncUninterruptibly();
    }
}
