package systems.reformcloud.reformcloud2.executor.client.packet.in;

import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Consumer;

public final class ClientPacketInStopProcess implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 3;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        UUID uniqueID = packet.content().get("uniqueID", UUID.class);
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
