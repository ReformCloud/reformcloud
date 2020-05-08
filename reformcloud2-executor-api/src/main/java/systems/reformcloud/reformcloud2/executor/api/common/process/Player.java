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
package systems.reformcloud.reformcloud2.executor.api.common.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.util.Objects;
import java.util.UUID;

public final class Player implements Comparable<Player>, SerializableObject {

    public Player(@NotNull UUID uniqueID, @NotNull String name) {
        this.uniqueID = uniqueID;
        this.name = name;
    }

    private UUID uniqueID;

    private String name;

    private long joined = System.currentTimeMillis();

    @NotNull
    public UUID getUniqueID() {
        return uniqueID;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public long getJoined() {
        return joined;
    }

    @Override
    public int compareTo(@NotNull Player o) {
        return Long.compare(getJoined(), o.getJoined());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return getUniqueID().equals(player.getUniqueID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUniqueID());
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.uniqueID);
        buffer.writeString(this.name);
        buffer.writeLong(this.joined);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.uniqueID = buffer.readUniqueId();
        this.name = buffer.readString();
        this.joined = buffer.readLong();
    }
}
