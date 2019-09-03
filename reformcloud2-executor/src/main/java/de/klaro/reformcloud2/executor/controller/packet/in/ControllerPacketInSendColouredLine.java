package de.klaro.reformcloud2.executor.controller.packet.in;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.Consumer;

public final class ControllerPacketInSendColouredLine implements NetworkHandler {
    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 6;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        try {
            ExecutorAPI.getInstance().sendColouredLine(packet.content().getString("line"));
        } catch (final IllegalAccessException ignored) {
            //Ignore this because it is not caused by a direct controller mistake (but eg by developer not the owner)
        }
    }
}
