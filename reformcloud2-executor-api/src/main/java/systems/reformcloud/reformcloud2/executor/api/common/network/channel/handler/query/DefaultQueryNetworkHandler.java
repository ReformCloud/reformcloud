package systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.query;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryHandler;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class DefaultQueryNetworkHandler extends DefaultJsonNetworkHandler {

    public DefaultQueryNetworkHandler(@Nonnull QueryHandler queryHandler) {
        super(-1);
        this.queryHandler = queryHandler;
    }

    private final QueryHandler queryHandler;

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        if (packet.queryUniqueID() != null && queryHandler.hasWaitingQuery(packet.queryUniqueID())) {
            queryHandler.getWaitingQuery(packet.queryUniqueID()).complete(packet);
        }
    }
}
