package de.klaro.reformcloud2.executor.api.common.network.packet.handler;

import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.query.QueryHandler;

import java.util.List;

public interface PacketHandler {

    void registerHandler(NetworkHandler networkHandler);

    void registerHandler(Class<? extends NetworkHandler> networkHandler);

    void registerNetworkHandlers(NetworkHandler... networkHandlers);

    void unregisterNetworkHandler(NetworkHandler networkHandler);

    void unregisterNetworkHandlers(int id);

    List<NetworkHandler> getNetworkHandlers(int id);

    List<NetworkHandler> getAllNetworkHandlers();

    QueryHandler getQueryHandler();
}
