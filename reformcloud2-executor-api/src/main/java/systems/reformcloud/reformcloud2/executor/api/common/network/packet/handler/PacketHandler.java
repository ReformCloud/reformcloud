package systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryHandler;

import java.util.List;

public interface PacketHandler {

    /**
     * Registers a packet handler
     *
     * @param networkHandler The packet handler which should get registered
     */
    void registerHandler(NetworkHandler networkHandler);

    /**
     * Registers a packet handler
     *
     * @param networkHandler The class of the packet handler which should get registered
     */
    void registerHandler(Class<? extends NetworkHandler> networkHandler);

    /**
     * Registers many packet handler
     *
     * @param networkHandlers All network handler which should get registered
     */
    void registerNetworkHandlers(NetworkHandler... networkHandlers);

    /**
     * Unregisters a specific network handler
     *
     * @param networkHandler The network handler which should get unregistered
     */
    void unregisterNetworkHandler(NetworkHandler networkHandler);

    /**
     * Unregisters all packet handler of the given type-id
     *
     * @param id The id of the packet handlers which should get unregistered
     */
    void unregisterNetworkHandlers(int id);

    /**
     * Get all network handlers of a specific id
     *
     * @param id The id which should get filtered
     * @return All registered network handlers handling the given id
     */
    List<NetworkHandler> getNetworkHandlers(int id);

    /**
     * @return All registered network handlers
     */
    List<NetworkHandler> getAllNetworkHandlers();

    /**
     * @return The query handler of the current instance
     */
    QueryHandler getQueryHandler();

    /**
     * Unregisters all waiting queries
     */
    void clearHandlers();
}
