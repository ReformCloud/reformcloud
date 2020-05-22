/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.concurrent.FastNettyThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class NetworkUtil {

    public static final int AUTH_BUS = -550;

    /* ============================= */
    public static final int NODE_TO_NODE_BUS = 20000;
    public static final int NODE_TO_NODE_QUERY_BUS = 25000;
    public static final int EVENT_BUS = 3000;
    public static final int PLAYER_INFORMATION_BUS = 5000;
    public static final int EXTERNAL_BUS = 50000;
    public static final int MESSAGING_BUS = 60000;
    public static final Executor EXECUTOR = Executors.newCachedThreadPool();

    /* ============================ */
    public static final WriteBufferWaterMark WATER_MARK = new WriteBufferWaterMark(524288, 2097152);
    private static final boolean EPOLL = Epoll.isAvailable();

    private NetworkUtil() {
        throw new UnsupportedOperationException();
    }

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
    public static ByteBuf write(@NotNull ByteBuf byteBuf, int value) {
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

    public static int read(@NotNull ByteBuf byteBuf) {
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
