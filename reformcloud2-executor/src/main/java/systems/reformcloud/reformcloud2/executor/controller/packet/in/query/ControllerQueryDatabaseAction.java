package systems.reformcloud.reformcloud2.executor.controller.packet.in.query;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class ControllerQueryDatabaseAction implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 14;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        String name = packet.content().getString("name");

        switch (packet.content().getString("action")) {
            case "action_create": {
                responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result", ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().createDatabase(name))));
                break;
            }

            case "action_delete": {
                responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result", ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().deleteDatabase(name))));
                break;
            }

            case "action_size": {
                responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result", ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().size(name))));
                break;
            }
        }
    }
}
