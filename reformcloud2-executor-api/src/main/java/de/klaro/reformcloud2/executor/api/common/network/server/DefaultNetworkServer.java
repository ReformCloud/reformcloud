package de.klaro.reformcloud2.executor.api.common.network.server;

import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.auth.ServerAuthHandler;
import de.klaro.reformcloud2.executor.api.common.network.handler.ServerInitializerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class DefaultNetworkServer implements NetworkServer {

    private final Map<Integer, ChannelFuture> channelFutures = new HashMap<>();

    private final EventLoopGroup boss = NetworkUtil.eventLoopGroup();

    private final EventLoopGroup worker = NetworkUtil.eventLoopGroup();

    private final Class<? extends ServerSocketChannel> channelClass = NetworkUtil.serverSocketChannel();

    @Override
    public void bind(String host, int port, ServerAuthHandler authHandler) {
        if (!channelFutures.containsKey(port)) {
            try {
                this.channelFutures.put(port, new ServerBootstrap()
                        .channel(channelClass)
                        .group(boss, worker)

                        .childOption(ChannelOption.SO_REUSEADDR, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.AUTO_READ, true)
                        .childOption(ChannelOption.IP_TOS, 24)
                        .childOption(ChannelOption.TCP_NODELAY, true)

                        .childHandler(new ServerInitializerHandler(
                                authHandler.channelReader(),
                                authHandler.function(),
                                authHandler.onSuccess(),
                                authHandler.onAuthFailure()
                        ))

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
    public void close(int port) {
        ChannelFuture channelFuture = this.channelFutures.remove(port);
        if (channelFuture != null) {
            channelFuture.cancel(true);
        }
    }

    @Override
    public void closeAll() {
        channelFutures.forEach(new BiConsumer<Integer, ChannelFuture>() {
            @Override
            public void accept(Integer integer, ChannelFuture channelFuture) {
                channelFuture.cancel(true);
            }
        });

        channelFutures.clear();
        this.worker.shutdownGracefully();
        this.boss.shutdownGracefully();
    }
}
