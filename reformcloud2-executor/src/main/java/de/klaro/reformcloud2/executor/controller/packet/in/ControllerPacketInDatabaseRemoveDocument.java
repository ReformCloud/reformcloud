package de.klaro.reformcloud2.executor.controller.packet.in;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.Consumer;

public final class ControllerPacketInDatabaseRemoveDocument implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 13;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        String table = packet.content().getString("table");
        String key = packet.content().getString("key");

        switch (packet.content().getString("action")) {
            case "remove_key": {
                ExecutorAPI.getInstance().remove(table, key);
                break;
            }

            case "remove_identifier": {
                ExecutorAPI.getInstance().removeIfAbsent(table, key);
                break;
            }
        }
    }
}
