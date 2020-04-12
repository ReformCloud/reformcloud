package systems.reformcloud.reformcloud2.executor.controller.network.packets.in.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.UUID;
import java.util.function.Consumer;

public final class ControllerQueryGetProcess extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 33;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        ProcessInformation result;

        if (packet.content().has("name")) {
            String name = packet.content().getString("name");
            result = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(name);
        } else {
            UUID uniqueID = packet.content().get("uniqueID", UUID.class);
            if (uniqueID == null) {
                result = null;
            } else {
                result = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(uniqueID);
            }
        }

        responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result", result)));
    }
}
