package de.klaro.reformcloud2.executor.api.common.network;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.defaults.DefaultPacketSender;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.MultithreadEventExecutorGroup;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class NetworkUtil {

    /* ============================= */

    public static final int CONTROLLER_INFORMATION_PACKETS = 2000;

    /* ============================ */


    public static final Executor EXECUTOR = Executors.newCachedThreadPool();

    public static final Consumer<ChannelHandlerContext> DEFAULT_AUTH_FAILURE_HANDLER = new Consumer<ChannelHandlerContext>() {
        @Override
        public void accept(ChannelHandlerContext context) {
            context.channel().writeAndFlush(new DefaultPacket(-511, new JsonConfiguration().add("access", false))).syncUninterruptibly().channel().close();
        }
    };

    public static final BiFunction<String, ChannelHandlerContext, PacketSender> DEFAULT_SUCCESS_HANDLER = new BiFunction<String, ChannelHandlerContext, PacketSender>() {
        @Override
        public PacketSender apply(String s, ChannelHandlerContext context) {
            PacketSender packetSender = new DefaultPacketSender(context.channel());
            packetSender.setName(s);
            return packetSender;
        }
    };

    private static final boolean EPOLL = Epoll.isAvailable();

    private static final boolean K_QUEUE = KQueue.isAvailable();

    private static final ThreadFactory THREAD_FACTORY = new DefaultThreadFactory(MultithreadEventExecutorGroup.class, true, Thread.MIN_PRIORITY);

    public static EventLoopGroup eventLoopGroup() {
        if (EPOLL) {
            return new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors(), THREAD_FACTORY);
        }

        if (K_QUEUE) {
            return new KQueueEventLoopGroup(Runtime.getRuntime().availableProcessors(), THREAD_FACTORY);
        }

        return new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), THREAD_FACTORY);
    }

    public static Class<? extends ServerSocketChannel> serverSocketChannel() {
        if (EPOLL) {
            return EpollServerSocketChannel.class;
        }

        if (K_QUEUE) {
            return KQueueServerSocketChannel.class;
        }

        return NioServerSocketChannel.class;
    }

    public static Class<? extends SocketChannel> socketChannel() {
        if (EPOLL) {
            return EpollSocketChannel.class;
        }

        if (K_QUEUE) {
            return KQueueSocketChannel.class;
        }

        return NioSocketChannel.class;
    }

    public static byte[] readBytes(ByteBuf byteBuf, int size) {
        byte[] result = new byte[size];
        byteBuf.readBytes(result);
        return result;
    }

    public static ByteBuf write(ByteBuf byteBuf, int value) {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            byteBuf.writeByte(temp);
        } while (value != 0);

        return byteBuf;
    }

    public static int read(ByteBuf byteBuf) {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = byteBuf.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public static ByteBuf write(ByteBuf byteBuf, String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        write(byteBuf, bytes.length);
        return byteBuf.writeBytes(bytes);
    }

    public static String readString(ByteBuf byteBuf) {
        int size = read(byteBuf);
        byte[] bytes = new byte[size];
        byteBuf.readBytes(bytes, 0, size);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private NetworkUtil() {}
}
