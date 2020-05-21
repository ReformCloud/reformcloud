/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

    private final String key;
    private final String name;
    private final Supplier<JsonConfiguration> supplier;
    private final Consumer<ChannelHandlerContext> channelHandlerContextConsumer;

    public ClientChallengeAuthHandler(String key, String name, Supplier<JsonConfiguration> supplier, Consumer<ChannelHandlerContext> consumer) {
        this.key = key;
        this.name = name;
        this.supplier = supplier;
        this.channelHandlerContextConsumer = consumer;
    }

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
