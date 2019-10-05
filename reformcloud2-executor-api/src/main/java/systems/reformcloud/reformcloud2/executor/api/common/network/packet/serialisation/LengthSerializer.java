package systems.reformcloud.reformcloud2.executor.api.common.network.packet.serialisation;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;

public final class LengthSerializer extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
        int readable = byteBuf.readableBytes();

        int space = getVarIntSize(readable);

        if (space > 5) {
            throw new RuntimeException();
        }

        byteBuf2.ensureWritable(space + readable);
        NetworkUtil.write(byteBuf2, readable);
        byteBuf2.writeBytes(byteBuf, byteBuf.readerIndex(), readable);
    }

    private int getVarIntSize(int value) {
        if ((value & -128) == 0)
            return 1;
        else if ((value & -16384) == 0)
            return 2;
        else if ((value & -2097152) == 0)
            return 3;
        else if ((value & -268435456) == 0)
            return 4;

        return 5;
    }
}
