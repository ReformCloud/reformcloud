package systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.DefaultProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.ProtocolBufferWrapper;

import java.util.List;

public final class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes() == 0) {
            return;
        }

        ByteBuf copy = byteBuf.copy();
        try {
            int id = NetworkUtil.read(byteBuf);
            list.add(new ProtocolBufferWrapper(id, new DefaultProtocolBuffer(copy)));
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            NetworkUtil.destroyByteBuf(byteBuf, false);
            NetworkUtil.destroyByteBuf(copy, false);
        }
    }
}
