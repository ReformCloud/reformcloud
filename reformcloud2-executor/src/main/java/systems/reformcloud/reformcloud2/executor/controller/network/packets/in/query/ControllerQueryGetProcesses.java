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

import java.util.List;
import java.util.function.Consumer;

public final class ControllerQueryGetProcesses extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 34;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        List<ProcessInformation> result;

        if (packet.content().has("group")) {
            String group = packet.content().getString("group");
            result = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(group);
        } else {
            result = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses();
        }

        responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result", result)));
    }
}
