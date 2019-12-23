package systems.reformcloud.reformcloud2.executor.controller.packet.in.query;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class ControllerQueryDatabaseUpdateDocument implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 12;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        String table = packet.content().getString("table");
        String key = packet.content().getString("key");
        JsonConfiguration data = packet.content().get("data");

        switch (packet.content().getString("action")) {
            case "update_key": {
                responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().update(table, key, data))));
                break;
            }

            case "update_identifier": {
                responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().updateIfAbsent(table, key, data))));
                break;
            }
        }
    }
}
