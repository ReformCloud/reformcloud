package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client.PacketOutClientChallengeRequest;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client.PacketOutClientChallengeResponse;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.server.PacketOutServerChallengeStart;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.provider.ChallengeProvider;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ServerChallengeAuthHandler implements ChallengeAuthHandler {

    public ServerChallengeAuthHandler(ChallengeProvider provider, BiConsumer<ChannelHandlerContext, PacketOutClientChallengeResponse> afterSuccess) {
        this.provider = provider;
        this.afterSuccess = afterSuccess;
        this.name = ignored -> "Controller";
    }

    public ServerChallengeAuthHandler(ChallengeProvider provider, BiConsumer<ChannelHandlerContext, PacketOutClientChallengeResponse> afterSuccess, Function<String, String> name) {
        this.provider = provider;
        this.afterSuccess = afterSuccess;
        this.name = name;
    }

    private final Function<String, String> name;

    private final ChallengeProvider provider;

    private final BiConsumer<ChannelHandlerContext, PacketOutClientChallengeResponse> afterSuccess;

    @Override
    public boolean handle(@NotNull ChannelHandlerContext channelHandlerContext, @NotNull Packet input, @NotNull String name) {
        if (input instanceof PacketOutClientChallengeRequest) {
            byte[] challenge = this.provider.createChallenge(name);
            if (challenge == null) {
                channelHandlerContext.channel().close().syncUninterruptibly();
                throw new RuntimeException("Unexpected issue while generating challenge for sender " + name);
            }

            channelHandlerContext.channel().writeAndFlush(new PacketOutServerChallengeStart(this.name.apply(name), challenge)).syncUninterruptibly();
            return false;
        }

        if (input instanceof PacketOutClientChallengeResponse) {
            PacketOutClientChallengeResponse response = (PacketOutClientChallengeResponse) input;
            if (this.provider.checkResult(name, response.getHashedResult())) {
                this.afterSuccess.accept(channelHandlerContext, response);
                return true;
            }

            channelHandlerContext.channel().close().syncUninterruptibly();
            return false;
        }

        return false;
    }
}
