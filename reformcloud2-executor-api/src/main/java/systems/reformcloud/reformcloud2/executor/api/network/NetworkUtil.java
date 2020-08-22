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
package systems.reformcloud.reformcloud2.executor.api.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.WriteBufferWaterMark;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.network.transport.TransportType;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class NetworkUtil {

    public static final int AUTH_BUS = 2000;
    public static final int AUTH_BUS_END = 2001;

    public static final int EMBEDDED_BUS = 3000;
    public static final int NODE_BUS = 4000;
    public static final int API_BUS = 5000;
    public static final int RESERVED_EXTRA_BUS = 6000;

    public static final Executor EXECUTOR = Executors.newCachedThreadPool();

    public static final WriteBufferWaterMark WATER_MARK = new WriteBufferWaterMark(524_288, 2_097_152);
    public static final TransportType TRANSPORT_TYPE = TransportType.getBestType();

    private static final RuntimeException BAD_VAR_INT_RECEIVED = new RuntimeException("Bad VarInt received");

    private NetworkUtil() {
        throw new UnsupportedOperationException();
    }

    public static void writeVarInt(@NotNull ByteBuf buf, int value) {
        while (true) {
            if ((value & -128) == 0) {
                buf.writeByte(value);
                return;
            }

            buf.writeByte(value & 127 | 128);
            value >>>= 7;
        }
    }

    public static int readVarInt(@NotNull ByteBuf byteBuf) {
        Integer varInt = readVarIntUnchecked(byteBuf);
        if (varInt == null) {
            throw BAD_VAR_INT_RECEIVED;
        }

        return varInt;
    }

    public static @Nullable Integer readVarIntUnchecked(@NotNull ByteBuf byteBuf) {
        int i = 0;
        int maxRead = Math.min(5, byteBuf.readableBytes());
        for (int j = 0; j < maxRead; j++) {
            int k = byteBuf.readByte();
            i |= (k & 127) << j * 7;
            if ((k & 128) != 128) {
                return i;
            }
        }

        return null;
    }
}
