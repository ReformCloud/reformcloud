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
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client.PacketOutClientChallengeRequest;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client.PacketOutClientChallengeResponse;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.server.PacketOutServerChallengeStart;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.provider.ChallengeProvider;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ServerChallengeAuthHandler implements ChallengeAuthHandler {

    private final Function<String, String> name;
    private final ChallengeProvider provider;
    private final BiConsumer<ChannelHandlerContext, PacketOutClientChallengeResponse> afterSuccess;

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

    @Override
    public boolean handle(@NotNull ChannelHandlerContext channelHandlerContext, @NotNull Packet input, @NotNull String name) {
        if (input instanceof PacketOutClientChallengeRequest) {
            byte[] challenge = this.provider.createChallenge(name);
            if (challenge == null) {
                channelHandlerContext.channel().close().syncUninterruptibly();
                throw new RuntimeException("DO NOT REPORT THIS TO RC: Issue while generating challenge for sender: " + name);
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
