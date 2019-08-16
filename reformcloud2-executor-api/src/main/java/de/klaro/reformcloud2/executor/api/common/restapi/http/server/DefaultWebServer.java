package de.klaro.reformcloud2.executor.api.common.restapi.http.server;

import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.restapi.http.init.DefaultChannelInitializerHandler;
import de.klaro.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

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
        channelFutures.forEach(new BiConsumer<Integer, ChannelFuture>() {
            @Override
            public void accept(Integer integer, ChannelFuture channelFuture) {
                channelFuture.cancel(true);
            }
        });
        channelFutures.clear();

        worker.shutdownGracefully();
        boss.shutdownGracefully();
    }
}
