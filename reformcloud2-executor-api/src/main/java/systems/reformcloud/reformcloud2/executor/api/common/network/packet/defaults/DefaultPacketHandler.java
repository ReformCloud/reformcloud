package systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.exception.SilentNetworkException;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DefaultPacketHandler implements PacketHandler {

    private final QueryHandler queryHandler = new DefaultQueryHandler();

    private final Map<Integer, Class<? extends Packet>> packets = new ConcurrentHashMap<>();

    @Override
    public void registerHandler(Class<? extends Packet> packetClass) {
        try {
            Packet asPacket = packetClass.getDeclaredConstructor().newInstance();
            this.packets.put(asPacket.getId(), packetClass);
        } catch (final NoSuchMethodException ex) {
            System.err.println("Missing NoArgsConstructor in packet class " + packetClass.getName());
        } catch (final Throwable throwable) {
            throw new SilentNetworkException("Unable to register packet " + packetClass.getName(), throwable);
        }
    }

    @Override
    @SafeVarargs
    public final void registerNetworkHandlers(Class<? extends Packet>... packetClasses) {
        for (Class<? extends Packet> packetClass : packetClasses) {
            this.registerHandler(packetClass);
        }
    }

    @Override
    public void unregisterNetworkHandler(int id) {
        this.packets.remove(id);
    }

    @Nullable
    @Override
    public Packet getNetworkHandler(int id) {
        Class<? extends Packet> packetClass = this.packets.get(id);
        if (packetClass == null) {
            return null;
        }

        try {
            return packetClass.getDeclaredConstructor().newInstance();
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    @NotNull
    @Override
    public QueryHandler getQueryHandler() {
        return this.queryHandler;
    }

    @Override
    public void clearHandlers() {
        this.packets.clear();
        this.queryHandler.clearQueries();
    }
}
