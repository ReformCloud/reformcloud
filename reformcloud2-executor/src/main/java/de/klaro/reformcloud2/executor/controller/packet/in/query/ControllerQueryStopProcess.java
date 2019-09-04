package de.klaro.reformcloud2.executor.controller.packet.in.query;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.UUID;
import java.util.function.Consumer;

public final class ControllerQueryStopProcess implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 32;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        ProcessInformation result;

        if (packet.content().has("name")) {
            String name = packet.content().getString("name");
            result = ExecutorAPI.getInstance().stopProcess(name);
        } else {
            UUID uniqueID = packet.content().get("uniqueID", UUID.class);
            result = ExecutorAPI.getInstance().stopProcess(uniqueID);
        }

        responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", result)));
    }
}
