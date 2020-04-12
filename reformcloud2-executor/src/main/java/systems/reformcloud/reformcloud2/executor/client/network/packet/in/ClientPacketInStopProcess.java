package systems.reformcloud.reformcloud2.executor.client.network.packet.in;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.network.packet.out.ClientPacketOutProcessStopped;
import systems.reformcloud.reformcloud2.executor.client.process.ProcessQueue;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Consumer;

public final class ClientPacketInStopProcess extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 3;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        UUID uniqueID = packet.content().get("uniqueID", UUID.class);

        // Check if process is currently queued - if yes remove it from queue and send the success packet
        ProcessInformation queued = ProcessQueue.removeFromQueue(uniqueID);
        if (queued != null) {
            packetSender.sendPacket(new ClientPacketOutProcessStopped(
                    queued.getProcessDetail().getProcessUniqueID(),
                    queued.getProcessDetail().getName()
            ));
            return;
        }

        ClientExecutor.getInstance().getProcessManager().getProcess(uniqueID).ifPresent(e -> {
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
