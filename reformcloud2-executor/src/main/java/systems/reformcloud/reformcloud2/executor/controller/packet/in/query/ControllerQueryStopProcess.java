package systems.reformcloud.reformcloud2.executor.controller.packet.in.query;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Consumer;

public final class ControllerQueryStopProcess implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 32;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        ProcessInformation result;

        if (packet.content().has("name")) {
            String name = packet.content().getString("name");
            result = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().stopProcess(name);
        } else {
            UUID uniqueID = packet.content().get("uniqueID", UUID.class);
            result = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().stopProcess(uniqueID);
        }

        responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result", result)));
    }
}
