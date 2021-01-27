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
package systems.reformcloud.reformcloud2.shared.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.address.NetworkAddress;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.listener.ChannelListener;
import systems.reformcloud.reformcloud2.executor.api.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.shared.network.channel.DefaultNetworkChannel;
import systems.reformcloud.reformcloud2.shared.network.handler.NettyChannelInitializer;
import systems.reformcloud.reformcloud2.shared.network.transport.EventLoopGroupType;
import systems.reformcloud.reformcloud2.shared.network.transport.TransportType;

import java.util.function.Function;

@SuppressWarnings("deprecation") // 1.8 is too old to use the new channel factory
public class DefaultNetworkClient extends DefaultNetworkChannel implements NetworkClient {

  private final EventLoopGroup workerGroup = TransportType.BEST_TYPE.getEventLoopGroup(EventLoopGroupType.WORKER);

  @Override
  public boolean connect(@NotNull String host, int port, @NotNull Function<NetworkChannel, ChannelListener> channelListenerFactory) {
    try {
      ChannelFuture future = this.connect0(host, port, channelListenerFactory).awaitUninterruptibly();
      if (future.isSuccess()) {
        this.setChannel(future.channel());
      }

      return future.isSuccess();
    } catch (Exception exception) {
      exception.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean connect(@NotNull NetworkAddress address, @NotNull Function<NetworkChannel, ChannelListener> channelListenerFactory) {
    return this.connect(address.getHost(), address.getPort(), channelListenerFactory);
  }

  private @NotNull ChannelFuture connect0(@NotNull String host, int port, @NotNull Function<NetworkChannel, ChannelListener> channelListenerFactory) {
    return new Bootstrap()
      .group(this.workerGroup)
      .channelFactory(TransportType.BEST_TYPE.getSocketChannelFactory())
      .option(ChannelOption.SO_REUSEADDR, true)
      .option(ChannelOption.SO_KEEPALIVE, true)
      .option(ChannelOption.AUTO_READ, true)
      .option(ChannelOption.IP_TOS, 24)
      .option(ChannelOption.TCP_NODELAY, true)
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
      .handler(new NettyChannelInitializer(channelListenerFactory))
      .connect(host, port)
      .addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE, ChannelFutureListener.CLOSE_ON_FAILURE);
  }

  @Override
  public void closeSync() {
    super.closeSync();
    this.workerGroup.shutdownGracefully().syncUninterruptibly();
  }
}
