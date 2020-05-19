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
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryHandler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DefaultQueryHandler implements QueryHandler {

    private final Map<UUID, Task<Packet>> waiting = new HashMap<>();

    @Override
    public Task<Packet> getWaitingQuery(@NotNull UUID uuid) {
        return this.waiting.remove(uuid);
    }

    @Override
    public boolean hasWaitingQuery(@NotNull UUID uuid) {
        return this.waiting.containsKey(uuid);
    }

    @NotNull
    @Override
    public Task<Packet> sendQueryAsync(@NotNull PacketSender sender, @NotNull Packet packet) {
        return this.sendQueryAsync(sender, UUID.randomUUID(), packet);
    }

    @NotNull
    @Override
    public Task<Packet> sendQueryAsync(@NotNull PacketSender sender, @NotNull UUID queryUniqueID, @NotNull Packet packet) {
        Task<Packet> task = new DefaultTask<>();

        this.waiting.put(queryUniqueID, task);
        packet.setQueryUniqueID(queryUniqueID);

        sender.sendPacket(packet);
        return task;
    }

    @Override
    public void sendQueryResultAsync(@NotNull PacketSender sender, @NotNull UUID queryUniqueID, @NotNull Packet packet) {
        packet.setQueryUniqueID(queryUniqueID);
        sender.sendPacket(packet);
    }

    @Override
    public void clearQueries() {
        this.waiting.clear();
    }
}
