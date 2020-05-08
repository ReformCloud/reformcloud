/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
