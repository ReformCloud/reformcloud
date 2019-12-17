package systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        new ArrayList<>(handlers).forEach(networkHandler -> {
            if (networkHandler.getHandlingPacketID() == id) {
                handlers.remove(networkHandler);
            }
        });
    }

    @Nonnull
    @Override
    public List<NetworkHandler> getNetworkHandlers(int id) {
        List<NetworkHandler> networkHandlers = new ArrayList<>();
        handlers.forEach(networkHandler -> {
            if (networkHandler.getHandlingPacketID() == id) {
                networkHandlers.add(networkHandler);
            }
        });
        return networkHandlers;
    }

    @Nonnull
    @Override
    public List<NetworkHandler> getAllNetworkHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    @Nonnull
    @Override
    public QueryHandler getQueryHandler() {
        return queryHandler;
    }

    @Override
    public void clearHandlers() {
        this.handlers.clear();
    }
}
