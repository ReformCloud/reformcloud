package systems.reformcloud.reformcloud2.executor.client.packet.in;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.packet.out.ClientPacketOutProcessStopped;
import systems.reformcloud.reformcloud2.executor.client.process.ProcessQueue;

public final class ClientPacketInStopProcess implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return NetworkUtil.CONTROLLER_INFORMATION_BUS + 3;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    UUID uniqueID = packet.content().get("uniqueID", UUID.class);

    // Check if process is currently queued - if yes remove it from queue and
    // send the success packet
    ProcessInformation queued = ProcessQueue.removeFromQueue(uniqueID);
    if (queued != null) {
      packetSender.sendPacket(new ClientPacketOutProcessStopped(
          queued.getProcessUniqueID(), queued.getName()));
      return;
    }

    ClientExecutor.getInstance()
        .getProcessManager()
        .getProcess(uniqueID)
        .ifPresent(e -> {
          e.shutdown();

          try {
            Method method = e.getClass().getMethod("finalize");
            method.setAccessible(true);
            method.invoke(e);
          } catch (final Throwable ignored) {
          }
        });
  }
}
