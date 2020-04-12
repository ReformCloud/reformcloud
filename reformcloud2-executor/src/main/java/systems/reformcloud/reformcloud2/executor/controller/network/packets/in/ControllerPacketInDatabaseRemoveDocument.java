package systems.reformcloud.reformcloud2.executor.controller.network.packets.in;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.Consumer;

public final class ControllerPacketInDatabaseRemoveDocument extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 13;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        String table = packet.content().getString("table");
        String key = packet.content().getString("key");

        switch (packet.content().getString("action")) {
            case "remove_key": {
                ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().remove(table, key);
                break;
            }

            case "remove_identifier": {
                ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().removeIfAbsent(table, key);
                break;
            }
        }
    }
}
