package systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.query.DefaultQueryNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryHandler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DefaultPacketHandler implements PacketHandler {

    private static final QueryHandler QUERY_HANDLER = new DefaultQueryHandler();

    private final List<NetworkHandler> handlers = new CopyOnWriteArrayList<>();

    public DefaultPacketHandler() {
        this.registerHandler(new DefaultQueryNetworkHandler(QUERY_HANDLER));
    }

    @Override
    public void registerHandler(NetworkHandler networkHandler) {
        handlers.add(networkHandler);
    }

    @Override
    public void registerHandler(Class<? extends NetworkHandler> networkHandler) {
        try {
            NetworkHandler handler = networkHandler.getDeclaredConstructor().newInstance();
            registerHandler(handler);
        } catch (final IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
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
        Streams.allOf(handlers, e -> e.getHandlingPacketID() == id).forEach(handlers::remove);
    }

    @Nonnull
    @Override
    public List<NetworkHandler> getNetworkHandlers(int id) {
        return Streams.list(handlers, e -> e.getHandlingPacketID() == id);
    }

    @Nonnull
    @Override
    public List<NetworkHandler> getAllNetworkHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    @Nonnull
    @Override
    public QueryHandler getQueryHandler() {
        return QUERY_HANDLER;
    }

    @Override
    public void clearHandlers() {
        this.handlers.clear();
    }
}
