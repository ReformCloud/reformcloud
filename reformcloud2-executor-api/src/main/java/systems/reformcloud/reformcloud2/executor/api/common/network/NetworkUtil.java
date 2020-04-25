package systems.reformcloud.reformcloud2.executor.api.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.concurrent.FastNettyThreadFactory;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.serialisation.LengthSerializer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class NetworkUtil {

    private NetworkUtil() {
        throw new UnsupportedOperationException();
    }

    /* ============================= */

    public static final int AUTH_BUS = -550;

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

    public static final MessageToByteEncoder<ByteBuf> SERIALIZER = new LengthSerializer();

    public static final WriteBufferWaterMark WATER_MARK = new WriteBufferWaterMark(1 << 20, 1 << 21);

    private static final boolean EPOLL = Epoll.isAvailable();

    @NotNull
    public static EventLoopGroup eventLoopGroup() {
        if (!Boolean.getBoolean("reformcloud.disable.native")) {
            if (EPOLL) {
                return new EpollEventLoopGroup(0, newThreadFactory("Epoll"));
            }
        }

        return new NioEventLoopGroup(0, newThreadFactory("Nio"));
    }

    @NotNull
    public static Class<? extends ServerSocketChannel> serverSocketChannel() {
        if (!Boolean.getBoolean("reformcloud.disable.native") && EPOLL) {
            return EpollServerSocketChannel.class;
        }

        return NioServerSocketChannel.class;
    }

    @NotNull
    public static Class<? extends SocketChannel> socketChannel() {
        if (!Boolean.getBoolean("reformcloud.disable.native") && EPOLL) {
            return EpollSocketChannel.class;
        }

        return NioSocketChannel.class;
    }

    @NotNull
    public static synchronized ByteBuf write(@NotNull ByteBuf byteBuf, int value) {
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

    public static synchronized int read(@NotNull ByteBuf byteBuf) {
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

    public static void destroyByteBuf(@NotNull ByteBuf byteBuf, boolean force) {
        if (byteBuf.refCnt() == 0) {
            // buffer is already released and will be garbage collected
            return;
        }

        if (byteBuf.refCnt() == 1 && !force) {
            // release of buffer will be made after channel read (we don't need to destroy it now)
            return;
        }

        boolean destroyed = byteBuf.release();
        while (!destroyed) {
            if (byteBuf.refCnt() == 1 && !force) {
                break;
            }

            destroyed = byteBuf.release();
        }
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

    @NotNull
    public static ThreadFactory newThreadFactory(@NotNull String type) {
        return new FastNettyThreadFactory("Netty Local " + type + " Thread#%d");
    }
}
