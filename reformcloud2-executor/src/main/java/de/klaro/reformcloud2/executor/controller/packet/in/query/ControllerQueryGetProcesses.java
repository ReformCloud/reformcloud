package de.klaro.reformcloud2.executor.controller.packet.in.query;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.List;
import java.util.function.Consumer;

public final class ControllerQueryGetProcesses implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 34;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        List<ProcessInformation> result;

        if (packet.content().has("group")) {
            String group = packet.content().getString("group");
            result = ExecutorAPI.getInstance().getProcesses(group);
        } else {
            result = ExecutorAPI.getInstance().getAllProcesses();
        }

        responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", result)));
    }
}
