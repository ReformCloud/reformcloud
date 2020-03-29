package systems.reformcloud.reformcloud2.executor.api.common.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ClientInitializerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class DefaultNetworkClient implements NetworkClient {

    private final EventLoopGroup eventLoopGroup = NetworkUtil.eventLoopGroup();

    private final Class<? extends SocketChannel> channelClass = NetworkUtil.socketChannel();

    private Channel channel;

    @Override
    public boolean connect(@NotNull String host, int port, @NotNull Supplier<NetworkChannelReader> supplier, @NotNull ChallengeAuthHandler challengeAuthHandler) {
        final Task<Boolean> connectTask = new DefaultTask<>();

        try {
            this.channel = new Bootstrap()
                    .group(eventLoopGroup)
                    .channel(channelClass)

                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.IP_TOS, 24)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CommonHelper.longToInt(TimeUnit.SECONDS.toMillis(5)))

                    .handler(new ClientInitializerHandler(supplier, challengeAuthHandler))

                    .connect(host, port)

                    .addListener(future -> connectTask.complete(future.isSuccess()))
                    .addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE, ChannelFutureListener.CLOSE_ON_FAILURE)
                    .channel();
        } catch (final Exception ex) {
            ex.printStackTrace();
            connectTask.complete(false);
        }

        Boolean result = connectTask.getUninterruptedly();
        return result != null && result;
    }

    @Override
    public void disconnect() {
        if (this.channel != null && channel.isOpen()) {
            channel.close();
        }

        eventLoopGroup.shutdownGracefully();
    }
}
