package de.klaro.reformcloud2.executor.controller.packet.in.query;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.Consumer;

public final class ControllerQueryDatabaseAction implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 14;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        String name = packet.content().getString("name");

        switch (packet.content().getString("action")) {
            case "action_create": {
                responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", ExecutorAPI.getInstance().createDatabase(name))));
                break;
            }

            case "action_delete": {
                responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", ExecutorAPI.getInstance().deleteDatabase(name))));
                break;
            }

            case "action_size": {
                responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", ExecutorAPI.getInstance().size(name))));
                break;
            }
        }
    }
}
