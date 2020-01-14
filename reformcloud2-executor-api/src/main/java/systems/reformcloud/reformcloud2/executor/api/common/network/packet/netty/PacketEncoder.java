package systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public final class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
        try {
            byte[] content = write(packet);

            NetworkUtil.write(byteBuf, packet.packetID());
            NetworkUtil.write(byteBuf, content.length).writeBytes(content);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    private static byte[] write(Packet packet) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            packet.write(objectOutputStream);
            return outputStream.toByteArray();
        }
    }
}
