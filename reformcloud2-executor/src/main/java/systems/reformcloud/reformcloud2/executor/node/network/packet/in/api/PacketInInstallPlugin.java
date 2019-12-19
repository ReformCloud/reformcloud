package systems.reformcloud.reformcloud2.executor.node.network.packet.in.api;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;

public final class PacketInInstallPlugin implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 27;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    String process = packet.content().getString("process");
    DefaultInstallablePlugin defaultPlugin =
        packet.content().get("plugin", InstallablePlugin.INSTALLABLE_TYPE);
    ExecutorAPI.getInstance().getSyncAPI().getPluginSyncAPI().installPlugin(
        process, defaultPlugin);
  }
}
