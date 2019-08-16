package de.klaro.reformcloud2.executor.api.common.network.packet.defaults;

import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.query.QueryHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class DefaultPacketHandler implements PacketHandler {

    private final QueryHandler queryHandler = new DefaultQueryHandler();

    private final List<NetworkHandler> handlers = new ArrayList<>();

    @Override
    public void registerHandler(NetworkHandler networkHandler) {
        handlers.add(networkHandler);
    }

    @Override
    public void registerHandler(Class<? extends NetworkHandler> networkHandler) {
        try {
            NetworkHandler handler = networkHandler.newInstance();
            registerHandler(handler);
        } catch (final InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void registerNetworkHandlers(NetworkHandler... networkHandlers) {
        for (NetworkHandler networkHandler : networkHandlers) {
            registerHandler(networkHandler);
        }
    }

    @Override
    public void unregisterNetworkHandler(NetworkHandler networkHandler) {
        handlers.remove(networkHandler);
    }

    @Override
    public void unregisterNetworkHandlers(int id) {
        new ArrayList<>(handlers).forEach(new Consumer<NetworkHandler>() {
            @Override
            public void accept(NetworkHandler networkHandler) {
                if (networkHandler.getHandlingPacketID() == id) {
                    handlers.remove(networkHandler);
                }
            }
        });
    }

    @Override
    public List<NetworkHandler> getNetworkHandlers(int id) {
        List<NetworkHandler> networkHandlers = new ArrayList<>();
        handlers.forEach(new Consumer<NetworkHandler>() {
            @Override
            public void accept(NetworkHandler networkHandler) {
                if (networkHandler.getHandlingPacketID() == id) {
                    networkHandlers.add(networkHandler);
                }
            }
        });
        return networkHandlers;
    }

    @Override
    public List<NetworkHandler> getAllNetworkHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    @Override
    public QueryHandler getQueryHandler() {
        return queryHandler;
    }
}
