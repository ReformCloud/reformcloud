package systems.reformcloud.reformcloud2.executor.api.common.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.packet.PacketOutAuth;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ClientInitializerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

public final class DefaultNetworkClient implements NetworkClient {

  private final EventLoopGroup eventLoopGroup = NetworkUtil.eventLoopGroup();

  private final Class<? extends SocketChannel> channelClass =
      NetworkUtil.socketChannel();

  private Channel channel;

  @Override
  public boolean connect(@Nonnull String host, int port, @Nonnull Auth auth,
                         @Nonnull NetworkChannelReader channelReader) {
    final Task<Boolean> connectTask = new DefaultTask<>();

    try {
      this.channel =
          new Bootstrap()
              .group(eventLoopGroup)
              .channel(channelClass)

              .option(ChannelOption.SO_REUSEADDR, true)
              .option(ChannelOption.SO_KEEPALIVE, true)
              .option(ChannelOption.AUTO_READ, true)
              .option(ChannelOption.IP_TOS, 24)
              .option(ChannelOption.TCP_NODELAY, true)

              .handler(new ClientInitializerHandler(channelReader, this))

              .connect(host, port)

              .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
              .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
              .addListener((ChannelFutureListener)channelFuture -> {
                if (channelFuture.isSuccess()) {
                  channelFuture.channel()
                      .writeAndFlush(new PacketOutAuth(auth))
                      .syncUninterruptibly();
                }

                connectTask.complete(channelFuture.isSuccess());
              })
              .channel();
    } catch (final Exception ex) {
      ex.printStackTrace();
      connectTask.complete(false);
    }

    return connectTask.getUninterruptedly();
  }

  @Override
  public void disconnect() {
    if (this.channel != null && channel.isOpen()) {
      channel.close();
    }

    eventLoopGroup.shutdownGracefully();
  }
}
