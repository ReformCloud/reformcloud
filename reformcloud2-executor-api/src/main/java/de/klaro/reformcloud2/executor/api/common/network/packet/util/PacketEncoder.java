package de.klaro.reformcloud2.executor.api.common.network.packet.util;

import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public final class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
        byte[] content = packet.content().toPrettyBytes();
        String uid = packet.queryUniqueID() != null ? packet.queryUniqueID().toString() : "null";

        NetworkUtil.write(byteBuf, packet.packetID());
        NetworkUtil.write(byteBuf, content.length).writeBytes(content);
        NetworkUtil.write(byteBuf, uid);
    }
}
