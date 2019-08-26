package de.klaro.reformcloud2.executor.api.common.network.packet.defaults;

import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.network.packet.query.QueryHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.query.QueryRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DefaultQueryHandler implements QueryHandler {

    private final Map<UUID, QueryRequest<Packet>> waiting = new HashMap<>();

    @Override
    public QueryRequest<Packet> getWaitingQuery(UUID uuid) {
        return waiting.remove(uuid);
    }

    @Override
    public boolean hasWaitingQuery(UUID uuid) {
        return waiting.containsKey(uuid);
    }

    @Override
    public QueryRequest<Packet> sendQueryAsync(PacketSender sender, Packet packet) {
        this.convertToQuery(packet, UUID.randomUUID());
        QueryRequest<Packet> queryRequest = new QueryRequest<>();
        this.waiting.put(packet.queryUniqueID(), queryRequest);
        sender.sendPacket(packet);
        return queryRequest;
    }

    @Override
    public Packet convertToQuery(Packet packet, UUID uuid) {
        packet.setQueryID(uuid);
        return packet;
    }

    @Override
    public void clearQueries() {
        this.waiting.clear();
    }
}
