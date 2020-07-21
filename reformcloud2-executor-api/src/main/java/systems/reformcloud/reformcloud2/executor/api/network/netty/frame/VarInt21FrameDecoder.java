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
package systems.reformcloud.reformcloud2.executor.api.network.netty.frame;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil;

import java.util.List;

public class VarInt21FrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        try {
            if (!channelHandlerContext.channel().isActive()) {
                byteBuf.skipBytes(byteBuf.readableBytes());
                return;
            }

            if (!byteBuf.isReadable()) {
                return;
            }

            int readerIndex = byteBuf.readerIndex();
            byte[] bytes = new byte[5];

            for (int i = 0; i < 5; i++) {
                if (!byteBuf.isReadable()) {
                    byteBuf.readerIndex(readerIndex);
                    return;
                }

                bytes[i] = byteBuf.readByte();
                if (bytes[i] >= 0) {
                    ByteBuf buf = Unpooled.wrappedBuffer(bytes);

                    try {
                        int length = NetworkUtil.readVarInt(buf);
                        if (byteBuf.readableBytes() < length) {
                            byteBuf.readerIndex(readerIndex);
                            return;
                        }

                        list.add(byteBuf.readBytes(length));
                    } finally {
                        buf.release();
                    }

                    return;
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
