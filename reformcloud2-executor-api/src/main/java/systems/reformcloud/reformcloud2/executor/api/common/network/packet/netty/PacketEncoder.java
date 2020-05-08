package systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.DefaultProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.exception.SilentNetworkException;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public final class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
        try {
            ProtocolBuffer protocolBuffer = new DefaultProtocolBuffer(byteBuf);

            protocolBuffer.writeInt(packet.getId());
            protocolBuffer.writeUniqueId(packet.getQueryUniqueID());

            packet.write(protocolBuffer);
        } catch (final Throwable ex) {
            new SilentNetworkException(ex).printStackTrace();
        }
    }
}
