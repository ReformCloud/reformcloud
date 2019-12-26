package systems.reformcloud.reformcloud2.executor.api.common.network.packet.serialisation;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;

public final class LengthSerializer extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
        int readable = byteBuf.readableBytes();
        int space = NetworkUtil.getVarIntSize(readable);

        if (space > 3) {
            throw new RuntimeException("Unable to fit " + readable + " into " + 3);
        }

        byteBuf2.ensureWritable(space + readable);
        NetworkUtil.write(byteBuf2, readable);
        byteBuf2.writeBytes(byteBuf, byteBuf.readerIndex(), readable);
    }
}
