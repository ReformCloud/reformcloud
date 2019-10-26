package systems.reformcloud.reformcloud2.executor.api.common.network.packet.query;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.annotiations.Nullable;

import java.util.UUID;

public interface QueryHandler {

    /**
     * Tries to get the waiting query of the given id
     *
     * @param uuid The id of the query
     * @return The waiting query request or {@code null} if no such request is known
     */
    @Nullable QueryRequest<Packet> getWaitingQuery(UUID uuid);

    /**
     * Checks if a id has a waiting query
     *
     * @param uuid The id of the query
     * @return If the id has a waiting query
     */
    boolean hasWaitingQuery(UUID uuid);

    /**
     * Sends a query async to a packet sender
     *
     * @param sender The sender who should receive the packet
     * @param packet The packet itself which will be converted to q query packet
     * @return The query request which got created
     * @apiNote It's not needed to give the packet as a query packet, because the cloud is going to convert it internal
     */
    QueryRequest<Packet> sendQueryAsync(PacketSender sender, Packet packet);

    /**
     * Converts a packet to a query packet
     *
     * @param packet The packet which should get converted
     * @param uuid The uuid of the packet which should be used
     * @return The packet which got converted
     */
    Packet convertToQuery(Packet packet, UUID uuid);

    /**
     * Clears all waiting queries
     */
    void clearQueries();
}
