package systems.reformcloud.reformcloud2.executor.api.common.network.packet.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class PacketDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext,
                        ByteBuf byteBuf, List<Object> list) {
    if (byteBuf.readableBytes() == 0) {
      return;
    }

    try {
      int id = NetworkUtil.read(byteBuf);
      JsonConfiguration content =
          new JsonConfiguration(NetworkUtil.readString(byteBuf));
      String query = NetworkUtil.readString(byteBuf);

      if (query.equals("null")) {
        list.add(new DefaultPacket(id, content));
      } else {
        list.add(new DefaultPacket(id, content, UUID.fromString(query)));
      }
    } catch (final Throwable throwable) {
      throwable.printStackTrace();
    }
  }
}
