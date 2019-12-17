package systems.reformcloud.reformcloud2.signs.packets.api.in;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class APIPacketInDeleteSign implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return PacketUtil.SIGN_BUS + 5;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    CloudSign sign = packet.content().get("sign", CloudSign.TYPE);
    if (sign == null) {
      return;
    }

    SignSystemAdapter.getInstance().handleInternalSignDelete(sign);
  }
}
