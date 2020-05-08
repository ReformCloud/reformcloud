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
package systems.reformcloud.reformcloud2.executor.api.common.restapi.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.http.init.DefaultChannelInitializerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;

import java.util.HashMap;
import java.util.Map;

public final class DefaultWebServer implements WebServer {

    private final EventLoopGroup worker = NetworkUtil.eventLoopGroup();

    private final EventLoopGroup boss = NetworkUtil.eventLoopGroup();

    private final Class<? extends ServerSocketChannel> socketClass = NetworkUtil.serverSocketChannel();

    private final Map<Integer, ChannelFuture> channelFutures = new HashMap<>();

    @Override
    public void add(String host, int port, RequestListenerHandler requestListenerHandler) {
        if (!channelFutures.containsKey(port)) {
            try {
                channelFutures.put(port, new ServerBootstrap()
                        .channel(socketClass)
                        .group(boss, worker)

                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childOption(ChannelOption.IP_TOS, 24)
                        .childOption(ChannelOption.AUTO_READ, true)
                        .childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)

                        .childHandler(new DefaultChannelInitializerHandler(requestListenerHandler))

                        .bind(host, port)

                        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)

                        .sync()
                        .channel()
                        .closeFuture()
                );
            } catch (final InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void closeFuture(int port) {
        ChannelFuture channelFuture = this.channelFutures.remove(port);
        if (channelFuture != null) {
            channelFuture.cancel(true);
        }
    }

    @Override
    public void close() {
        channelFutures.forEach((integer, channelFuture) -> channelFuture.cancel(true));
        channelFutures.clear();

        worker.shutdownGracefully();
        boss.shutdownGracefully();
    }
}
