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
package systems.reformcloud.reformcloud2.shared.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.network.channel.EndpointChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.network.transport.EventLoopGroupType;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class DefaultNetworkClient implements NetworkClient {

    private final EventLoopGroup eventLoopGroup = NetworkUtil.TRANSPORT_TYPE.getEventLoopGroupFactory(EventLoopGroupType.WORKER);
    private Channel channel;

    @Override
    public boolean connect(@NotNull String host, int port, @NotNull Supplier<EndpointChannelReader> supplier) {
        try {
            ChannelFuture future = this.connect0(host, port, supplier).sync();
            if (future.isSuccess()) {
                this.channel = future.channel();
            }

            return future.isSuccess();
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    private @NotNull ChannelFuture connect0(@NotNull String host, int port, @NotNull Supplier<EndpointChannelReader> supplier) {
        return new Bootstrap()
                .group(this.eventLoopGroup)
                .channelFactory(NetworkUtil.TRANSPORT_TYPE.getSocketChannelFactory())
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.IP_TOS, 24)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CommonHelper.longToInt(TimeUnit.SECONDS.toMillis(5)))
                .handler(new ClientChannelInitializer(supplier))
                .connect(host, port)
                .addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE, ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    @Override
    public void disconnect() {
        if (this.channel != null && this.channel.isOpen()) {
            this.channel.close();
        }

        this.eventLoopGroup.shutdownGracefully();
    }
}
