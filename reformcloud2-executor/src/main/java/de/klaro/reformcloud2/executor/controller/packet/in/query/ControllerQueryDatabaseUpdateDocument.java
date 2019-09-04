package de.klaro.reformcloud2.executor.controller.packet.in.query;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.Consumer;

public final class ControllerQueryDatabaseUpdateDocument implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 12;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        String table = packet.content().getString("table");
        String key = packet.content().getString("key");
        JsonConfiguration data = packet.content().get("data");

        switch (packet.content().getString("action")) {
            case "update_key": {
                responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", ExecutorAPI.getInstance().update(table, key, data))));
                break;
            }

            case "update_identifier": {
                responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", ExecutorAPI.getInstance().updateIfAbsent(table, key, data))));
                break;
            }
        }
    }
}
