package systems.reformcloud.reformcloud2.executor.api.common.network.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.ServerAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ServerInitializerHandler;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public final class DefaultNetworkServer implements NetworkServer {

    private final Map<Integer, ChannelFuture> channelFutures = new HashMap<>();

    private final EventLoopGroup boss = NetworkUtil.eventLoopGroup();

    private final EventLoopGroup worker = NetworkUtil.eventLoopGroup();

    private final Class<? extends ServerSocketChannel> channelClass = NetworkUtil.serverSocketChannel();

    @Override
    public void bind(@Nonnull String host, int port, @Nonnull ServerAuthHandler authHandler) {
        if (!channelFutures.containsKey(port)) {
                new ServerBootstrap()
                        .channel(channelClass)
                        .group(boss, worker)

                        .childOption(ChannelOption.SO_REUSEADDR, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.AUTO_READ, true)
                        .childOption(ChannelOption.IP_TOS, 0x18)
                        .childOption(ChannelOption.TCP_NODELAY, true)

                        .childHandler(new ServerInitializerHandler(
                                authHandler.packetHandler(),
                                authHandler.onDisconnect(),
                                authHandler.function(),
                                authHandler.onSuccess(),
                                authHandler.onAuthFailure()
                        ))

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
