package systems.reformcloud.reformcloud2.signs.packets.api.in;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

public class APIPacketInReloadConfig implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return PacketUtil.SIGN_BUS + 6;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    SignConfig config = packet.content().get("config", SignConfig.TYPE);
    if (config == null) {
      return;
    }

    SignSystemAdapter.getInstance().handleSignConfigUpdate(config);
  }
}
