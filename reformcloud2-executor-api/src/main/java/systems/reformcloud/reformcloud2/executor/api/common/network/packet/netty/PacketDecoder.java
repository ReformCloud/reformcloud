package systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultWrappedByteInput;

import java.util.List;

public final class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes() == 0) {
            return;
        }

        try {
            int id = NetworkUtil.read(byteBuf);
            list.add(new DefaultWrappedByteInput(id, NetworkUtil.readBytes(byteBuf)));
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            NetworkUtil.destroyByteBuf(byteBuf, false);
        }
    }
}
