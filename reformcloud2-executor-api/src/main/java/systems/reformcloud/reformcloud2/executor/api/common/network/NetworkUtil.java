package systems.reformcloud.reformcloud2.executor.api.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.MultithreadEventExecutorGroup;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class NetworkUtil {

    private NetworkUtil() {
        throw new UnsupportedOperationException();
    }

    /* ============================= */

    public static final int FILE_BUS = 1000;

    public static final int NODE_TO_NODE_BUS = 20000;

    public static final int NODE_TO_NODE_QUERY_BUS = 25000;

    public static final int CONTROLLER_INFORMATION_BUS = 2000;

    public static final int EVENT_BUS = 3000;

    public static final int CONTROLLER_QUERY_BUS = 4000;

    public static final int PLAYER_INFORMATION_BUS = 5000;

    public static final int EXTERNAL_BUS = 50000;

    public static final int MESSAGING_BUS = 60000;

    /* ============================ */

    public static final Executor EXECUTOR = Executors.newCachedThreadPool();

    private static final boolean EPOLL = Epoll.isAvailable();

    private static final ThreadFactory THREAD_FACTORY = new DefaultThreadFactory(MultithreadEventExecutorGroup.class, true, Thread.MIN_PRIORITY);

    public static EventLoopGroup eventLoopGroup() {
        if (!Boolean.getBoolean("reformcloud.disable.native")) {
            if (EPOLL) {
                return new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors(), THREAD_FACTORY);
            }
        }

        return new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), THREAD_FACTORY);
    }

    public static Class<? extends ServerSocketChannel> serverSocketChannel() {
        if (!Boolean.getBoolean("reformcloud.disable.native")) {
            if (EPOLL) {
                return EpollServerSocketChannel.class;
            }
        }

        return NioServerSocketChannel.class;
    }

    public static Class<? extends SocketChannel> socketChannel() {
        if (!Boolean.getBoolean("reformcloud.disable.native")) {
            if (EPOLL) {
                return EpollSocketChannel.class;
            }
        }

        return NioSocketChannel.class;
    }

    public static synchronized ByteBuf write(ByteBuf byteBuf, int value) {
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

    public static synchronized int read(ByteBuf byteBuf) {
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

    public static synchronized byte[] readBytes(ByteBuf byteBuf) {
        int length = read(byteBuf);
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    public static int getVarIntSize(int readable) {
        if ((readable & -128) == 0) {
            return 1;
        } else if ((readable & -16384) == 0) {
            return 2;
        } else if ((readable & -2097152) == 0) {
            return 3;
        } else if ((readable & -268435456) == 0) {
            return 4;
        }

        return 5;
    }
}
