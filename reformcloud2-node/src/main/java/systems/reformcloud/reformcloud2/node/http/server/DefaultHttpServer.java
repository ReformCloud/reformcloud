/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.node.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.http.listener.HttpListenerRegistry;
import systems.reformcloud.reformcloud2.executor.api.http.server.HttpServer;
import systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.network.transport.EventLoopGroupType;
import systems.reformcloud.reformcloud2.node.http.listener.DefaultHttpListenerRegistry;
import systems.reformcloud.reformcloud2.node.http.response.DefaultHttpServerResponseFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public class DefaultHttpServer implements HttpServer {

    private final Map<Integer, ChannelFuture> boundServers = new ConcurrentHashMap<>();
    private final HttpListenerRegistry listenerRegistry = new DefaultHttpListenerRegistry();

    private final EventLoopGroup bossGroup = NetworkUtil.TRANSPORT_TYPE.getEventLoopGroup(EventLoopGroupType.BOSS);
    private final EventLoopGroup workerGroup = NetworkUtil.TRANSPORT_TYPE.getEventLoopGroup(EventLoopGroupType.WORKER);

    public DefaultHttpServer() {
        DefaultHttpServerResponseFactory.init();
    }

    @Override
    public boolean bind(@NotNull String host, int port) {
        if (this.boundServers.containsKey(port)) {
            return false;
        }

        try {
            this.boundServers.put(port, new ServerBootstrap()
                .group(this.bossGroup, this.workerGroup)
                .channelFactory(NetworkUtil.TRANSPORT_TYPE.getServerSocketChannelFactory())

                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.IP_TOS, 24)
                .childOption(ChannelOption.AUTO_READ, true)
                .childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)

                .childHandler(new DefaultHttpServerChannelInitializer(this.listenerRegistry))

                .bind(host, port)

                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)

                .sync()
                .channel()
                .closeFuture());
            return true;
        } catch (InterruptedException exception) {
            return false;
        }
    }

    @Override
    public @NotNull HttpListenerRegistry getListenerRegistry() {
        return this.listenerRegistry;
    }

    @Override
    public void close(int port) {
        ChannelFuture future = this.boundServers.remove(port);
        if (future != null) {
            future.cancel(true);
        }
    }

    @Override
    public void closeAll() {
        for (Integer integer : this.boundServers.keySet()) {
            this.close(integer);
        }
    }
}
