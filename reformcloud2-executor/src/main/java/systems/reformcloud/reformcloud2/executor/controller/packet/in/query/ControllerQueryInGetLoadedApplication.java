package systems.reformcloud.reformcloud2.executor.controller.packet.in.query;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultLoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public final class ControllerQueryInGetLoadedApplication
    implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 3;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    String name = packet.content().getString("name");
    responses.accept(new DefaultPacket(
        -1, new JsonConfiguration().add("result",
                                        convert(ExecutorAPI.getInstance()
                                                    .getSyncAPI()
                                                    .getApplicationSyncAPI()
                                                    .getApplication(name)))));
  }

  private static DefaultLoadedApplication
  convert(LoadedApplication application) {
    if (application == null) {
      return null;
    }

    return new DefaultLoadedApplication(application.loader(),
                                        application.applicationConfig(),
                                        application.mainClass());
  }
}
