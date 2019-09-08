package de.klaro.reformcloud2.executor.api.common.network.client;

import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.auth.Auth;
import de.klaro.reformcloud2.executor.api.common.network.auth.packet.PacketOutAuth;
import de.klaro.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import de.klaro.reformcloud2.executor.api.common.network.handler.ClientInitializerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;

public final class DefaultNetworkClient implements NetworkClient {

    private final EventLoopGroup eventLoopGroup = NetworkUtil.eventLoopGroup();

    private final Class<? extends SocketChannel> channelClass = NetworkUtil.socketChannel();

    private Channel channel;

    @Override
    public boolean connect(String host, int port, Auth auth, NetworkChannelReader channelReader) {
        try {
            this.channel = new Bootstrap().group(eventLoopGroup)
                    .channel(channelClass)

                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.IP_TOS, 24)
                    .option(ChannelOption.TCP_NODELAY, true)

                    .handler(new ClientInitializerHandler(channelReader, this))

                    .connect(host, port)

                    .addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) {
                            if (channelFuture.isSuccess()) {
                                channelFuture.channel().writeAndFlush(new PacketOutAuth(auth)).syncUninterruptibly();
                            }
                        }
                    }).channel();
            return true;
        } catch (final Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void disconnect() {
        if (this.channel != null && channel.isOpen()) {
            channel.close();
        }
    }
}
