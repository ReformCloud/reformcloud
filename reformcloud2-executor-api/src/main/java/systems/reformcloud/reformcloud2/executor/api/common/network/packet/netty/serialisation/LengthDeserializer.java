package systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.serialisation;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;

import java.util.List;

public final class LengthDeserializer extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        byteBuf.markReaderIndex();

        byte[] bytes = new byte[5];
        for (int i = 0; i < bytes.length; i++) {
            if (!byteBuf.isReadable()) {
                byteBuf.resetReaderIndex();
                return;
            }

            bytes[i] = byteBuf.readByte();
            if (bytes[i] >= 0) {
                int length = NetworkUtil.read(Unpooled.wrappedBuffer(bytes));
                if (length == 0 || byteBuf.readableBytes() < length) {
                    byteBuf.resetReaderIndex();
                    return;
                }

                if (byteBuf.hasMemoryAddress()) {
                    list.add(byteBuf.slice(byteBuf.readerIndex(), length).retain());
                    byteBuf.skipBytes(length);
                    return;
                }

                ByteBuf channel = channelHandlerContext.alloc().directBuffer(length);
                channel.readBytes(byteBuf);
                list.add(channel);

                return;
            }
        }
    }
}
