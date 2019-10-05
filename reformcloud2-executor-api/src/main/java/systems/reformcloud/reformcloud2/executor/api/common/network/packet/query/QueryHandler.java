package systems.reformcloud.reformcloud2.executor.api.common.network.packet.query;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.UUID;

public interface QueryHandler {

    QueryRequest<Packet> getWaitingQuery(UUID uuid);

    boolean hasWaitingQuery(UUID uuid);

    QueryRequest<Packet> sendQueryAsync(PacketSender sender, Packet packet);

    Packet convertToQuery(Packet packet, UUID uuid);

    void clearQueries();
}
