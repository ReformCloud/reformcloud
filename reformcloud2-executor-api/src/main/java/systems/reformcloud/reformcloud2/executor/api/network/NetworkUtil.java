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

    private NetworkUtil() {
        throw new UnsupportedOperationException();
    }

    public static void writeVarInt(@NotNull ByteBuf buf, int value) {
        do {
            byte temp = (byte) (value & 0x7f);
            value >>>= 7;
            if (value != 0) {
                temp |= 0x80;
            }

            buf.writeByte(temp);
        } while (value != 0);
    }

    public static int readVarInt(@NotNull ByteBuf buf) {
        int numRead = 0;
        int result = 0;
        byte read;

        do {
            read = buf.readByte();
            int value = (read & 0x7f);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0x80) != 0);

        return result;
    }
}
