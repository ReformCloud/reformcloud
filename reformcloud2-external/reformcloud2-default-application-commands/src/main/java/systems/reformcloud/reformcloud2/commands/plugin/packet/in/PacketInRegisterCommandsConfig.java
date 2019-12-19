package systems.reformcloud.reformcloud2.commands.plugin.packet.in;

import com.google.gson.reflect.TypeToken;
import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.commands.plugin.CommandConfigHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public class PacketInRegisterCommandsConfig implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return NetworkUtil.EXTERNAL_BUS + 1;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    final CommandsConfig config =
        packet.content().get("config", new TypeToken<CommandsConfig>() {});
    if (config == null) {
      return;
    }

    CommandConfigHandler.getInstance().handleCommandConfigRelease(config);
  }
}
