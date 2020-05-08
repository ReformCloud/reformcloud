package systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryHandler;

public interface PacketHandler {

    void registerHandler(Class<? extends Packet> packetClass);

    void registerNetworkHandlers(Class<? extends Packet>... packetClasses);

    /**
     * Unregisters all packet handler of the given type-id
     *
     * @param id The id of the packet handlers which should get unregistered
     */
    void unregisterNetworkHandler(int id);

    /**
     * Get all network handlers of a specific id
     *
     * @param id The id which should get filtered
     * @return All registered network handlers handling the given id
     */
    @Nullable
    Packet getNetworkHandler(int id);

    /**
     * @return The query handler of the current instance
     */
    @NotNull
    QueryHandler getQueryHandler();

    /**
     * Unregisters all waiting queries
     */
    void clearHandlers();
}
