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
package systems.reformcloud.reformcloud2.executor.api.common.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public final class ChannelReaderHelper extends SimpleChannelInboundHandler<Packet> {

    private final NetworkChannelReader channelReader;
    private final ChallengeAuthHandler authHandler;
    public boolean auth = false;
    private boolean wasActive = false;

    public ChannelReaderHelper(NetworkChannelReader channelReader, ChallengeAuthHandler authHandler) {
        this.channelReader = channelReader;
        this.authHandler = authHandler;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        this.channelReader.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (!this.auth && !this.wasActive) {
            this.wasActive = true;
            this.authHandler.handleChannelActive(ctx);
        }

        this.channelReader.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.channelReader.channelInactive(ctx);
        this.wasActive = false;
        this.auth = false;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        this.channelReader.readOperationCompleted(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet input) {
        if (!auth) {
            if (input.getId() > NetworkUtil.AUTH_BUS + 4 || input.getId() < NetworkUtil.AUTH_BUS) {
                channelHandlerContext.channel().close().syncUninterruptibly();
                return;
            }
        }

        channelReader.read(channelHandlerContext, this.authHandler, this, input);
    }
}
