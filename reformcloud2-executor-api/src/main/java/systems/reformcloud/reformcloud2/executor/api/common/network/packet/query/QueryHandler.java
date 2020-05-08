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
package systems.reformcloud.reformcloud2.executor.api.common.network.packet.query;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.UUID;

public interface QueryHandler {

    /**
     * Tries to get the waiting query of the given id
     *
     * @param queryUniqueId The id of the query
     * @return The waiting query request or {@code null} if no such request is known
     */
    @Nullable
    Task<Packet> getWaitingQuery(@NotNull UUID queryUniqueId);

    /**
     * Checks if a id has a waiting query
     *
     * @param queryUniqueId The id of the query
     * @return If the id has a waiting query
     */
    boolean hasWaitingQuery(@NotNull UUID queryUniqueId);

    /**
     * Sends a query async to a packet sender
     * Note: It's not needed to give the packet as a query packet, because the cloud is going to convert it internal
     *
     * @param sender The sender who should receive the packet
     * @param packet The packet itself which will be converted to q query packet
     * @return The query request which got created
     */
    @NotNull
    Task<Packet> sendQueryAsync(@NotNull PacketSender sender, @NotNull Packet packet);

    @NotNull
    Task<Packet> sendQueryAsync(@NotNull PacketSender sender, @NotNull UUID queryUniqueID, @NotNull Packet packet);

    void sendQueryResultAsync(@NotNull PacketSender sender, @NotNull UUID queryUniqueID, @NotNull Packet packet);

    /**
     * Clears all waiting queries
     */
    void clearQueries();
}
