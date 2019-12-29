package systems.reformcloud.reformcloud2.executor.api.common.network.packet.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public final class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
        String uid = packet.queryUniqueID() != null ? packet.queryUniqueID().toString() : "null";
        byte[] contents = packet.content().toPrettyBytes();
        byte[] extra = packet.extra().length == 0 ? new byte[]{0} : packet.extra();

        NetworkUtil.write(byteBuf, packet.packetID());
        NetworkUtil.writeString(byteBuf, uid);
        NetworkUtil.write(byteBuf, contents.length).writeBytes(contents);
        NetworkUtil.write(byteBuf, extra.length).writeBytes(extra);
    }
}
