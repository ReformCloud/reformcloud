package systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.DefaultProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.exception.SilentNetworkException;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.List;

public final class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        try {
            ProtocolBuffer protocolBuffer = new DefaultProtocolBuffer(byteBuf);
            Packet packet = ExecutorAPI.getInstance().getPacketHandler().getNetworkHandler(protocolBuffer.readInt());

            if (packet == null) {
                return;
            }

            packet.setQueryUniqueID(protocolBuffer.readUniqueId());
            packet.read(protocolBuffer);

            list.add(packet);
        } catch (final Throwable throwable) {
            throw new SilentNetworkException(throwable);
        }
    }
}
