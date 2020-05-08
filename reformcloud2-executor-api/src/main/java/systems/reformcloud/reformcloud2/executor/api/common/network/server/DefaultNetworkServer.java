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
package systems.reformcloud.reformcloud2.executor.api.common.network.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ServerInitializerHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class DefaultNetworkServer implements NetworkServer {

    private final Map<Integer, ChannelFuture> channelFutures = new HashMap<>();

    private final EventLoopGroup boss = NetworkUtil.eventLoopGroup();

    private final EventLoopGroup worker = NetworkUtil.eventLoopGroup();

    private final Class<? extends ServerSocketChannel> channelClass = NetworkUtil.serverSocketChannel();

    @Override
    public void bind(@NotNull String host, int port, @NotNull Supplier<NetworkChannelReader> readerHelper, @NotNull ChallengeAuthHandler challengeAuthHandler) {
        if (!channelFutures.containsKey(port)) {
                new ServerBootstrap()
                        .channel(channelClass)
                        .group(boss, worker)

                        .childOption(ChannelOption.SO_REUSEADDR, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.AUTO_READ, true)
                        .childOption(ChannelOption.IP_TOS, 0x18)
                        .childOption(ChannelOption.TCP_NODELAY, true)

                        .childHandler(new ServerInitializerHandler(readerHelper, challengeAuthHandler))

                        .bind(host, port)

                        .addListener((ChannelFutureListener) channelFuture -> {
                            if (channelFuture.isSuccess()) {
                                DefaultNetworkServer.this.channelFutures.put(port, channelFuture);
                            } else {
                                channelFuture.cause().printStackTrace();
                            }
                        });
        }
    }

    @Override
    public void close(int port) {
        ChannelFuture channelFuture = this.channelFutures.remove(port);
        if (channelFuture != null) {
            channelFuture.cancel(true);
        }
    }

    @Override
    public void closeAll() {
        channelFutures.forEach((integer, channelFuture) -> channelFuture.cancel(true));
        channelFutures.clear();

        this.worker.shutdownGracefully();
        this.boss.shutdownGracefully();
    }
}
