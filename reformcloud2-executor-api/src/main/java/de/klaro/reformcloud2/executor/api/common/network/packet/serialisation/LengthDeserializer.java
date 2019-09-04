package de.klaro.reformcloud2.executor.api.common.network.packet.serialisation;

import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public final class LengthDeserializer extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        byteBuf.markReaderIndex();
        byte[] bytes = new byte[5];

        for (int i = 0; i < 5; i++) {
            if (!byteBuf.isReadable()) {
                byteBuf.resetReaderIndex();
                return;
            }

            bytes[i] = byteBuf.readByte();
            if (bytes[i] >= 0) {
                ByteBuf buf = Unpooled.wrappedBuffer(bytes);
                try {
                    int length = NetworkUtil.read(buf);
                    if (byteBuf.readableBytes() < length) {
                        byteBuf.resetReaderIndex();
                        return;
                    }

                    list.add(byteBuf.readBytes(length));
                } finally {
                    buf.release();
                }

                return;
            }
        }
    }
}
