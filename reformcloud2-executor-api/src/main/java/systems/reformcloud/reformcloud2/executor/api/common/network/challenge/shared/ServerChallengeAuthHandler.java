package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.provider.ChallengeProvider;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public final class ServerChallengeAuthHandler implements ChallengeAuthHandler {

    public ServerChallengeAuthHandler(ChallengeProvider provider, BiConsumer<ChannelHandlerContext, Packet> afterSuccess) {
        this.provider = provider;
        this.afterSuccess = afterSuccess;
    }

    private final ChallengeProvider provider;

    private final BiConsumer<ChannelHandlerContext, Packet> afterSuccess;

    @Override
    public boolean handle(@Nonnull ChannelHandlerContext channelHandlerContext, @Nonnull Packet input, @Nonnull String name) {
        if (input.packetID() == -512) {
            // request of challenge
            byte[] challenge = this.provider.createChallenge(name);
            if (challenge == null) {
                channelHandlerContext.channel().close().syncUninterruptibly();
                throw new RuntimeException("Unexpected issue while generating challenge for sender " + name);
            }

            System.out.println("created challenge for " + name);
            channelHandlerContext.channel().writeAndFlush(new JsonPacket(
                    -512, new JsonConfiguration().add("challenge", challenge).add("name", "Controller")
            )).syncUninterruptibly();
            return false;
        }

        if (input.packetID() == -511) {
            // result of challenge
            String challengeResult = input.content().getOrDefault("result", (String) null);
            System.out.println("Received result from " + name);
            if (challengeResult == null) {
                channelHandlerContext.channel().close().syncUninterruptibly();
                return false;
            }

            System.out.println("result sent");

            if (this.provider.checkResult(name, challengeResult)) {
                this.afterSuccess.accept(channelHandlerContext, input);
                return true;
            }

            System.out.println("Check failed");

            channelHandlerContext.channel().close().syncUninterruptibly();
            return false;
        }

        return false;
    }
}
