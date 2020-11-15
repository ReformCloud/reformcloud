/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.shared.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.process.Player;

import java.util.UUID;

public class DefaultPlayer implements Player {

    private UUID uniqueId;
    private String name;
    private long joinTime;

    protected DefaultPlayer() {
    }

    public DefaultPlayer(UUID uniqueId, String name, long joinTime) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.joinTime = joinTime;
    }

    @Override
    public @NotNull UUID getUniqueID() {
        return this.uniqueId;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public long getJoined() {
        return this.joinTime;
    }

    @Override
    public @NotNull Player clone() {
        return new DefaultPlayer(this.uniqueId, this.name, this.joinTime);
    }

    @Override
    public int compareTo(@NotNull Player o) {
        return Long.compare(this.joinTime, o.getJoined());
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.uniqueId);
        buffer.writeString(this.name);
        buffer.writeLong(this.joinTime);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.uniqueId = buffer.readUniqueId();
        this.name = buffer.readString();
        this.joinTime = buffer.readLong();
    }
}
