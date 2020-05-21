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
