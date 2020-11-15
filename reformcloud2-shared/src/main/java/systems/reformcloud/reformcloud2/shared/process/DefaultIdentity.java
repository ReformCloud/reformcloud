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
import systems.reformcloud.reformcloud2.executor.api.process.Identity;

import java.util.UUID;

public class DefaultIdentity implements Identity {

    private String name;
    private String displayName;
    private UUID uniqueId;
    private int id;
    private String nodeName;
    private UUID nodeUniqueId;

    protected DefaultIdentity() {
    }

    public DefaultIdentity(String name, String displayName, UUID uniqueId, int id, String nodeName, UUID nodeUniqueId) {
        this.name = name;
        this.displayName = displayName;
        this.uniqueId = uniqueId;
        this.id = id;
        this.nodeName = nodeName;
        this.nodeUniqueId = nodeUniqueId;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public @NotNull UUID getNodeUniqueId() {
        return this.nodeUniqueId;
    }

    @Override
    public @NotNull String getNodeName() {
        return this.nodeName;
    }

    @Override
    public @NotNull String getDisplayName() {
        return this.displayName;
    }

    @Override
    public @NotNull Identity clone() {
        return new DefaultIdentity(this.name, this.displayName, this.uniqueId, this.id, this.nodeName, this.nodeUniqueId);
    }

    @Override
    public int compareTo(@NotNull Identity o) {
        return Integer.compare(this.id, o.getId());
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeString(this.displayName);
        buffer.writeUniqueId(this.uniqueId);
        buffer.writeInt(this.id);
        buffer.writeString(this.nodeName);
        buffer.writeUniqueId(this.nodeUniqueId);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.name = buffer.readString();
        this.displayName = buffer.readString();
        this.uniqueId = buffer.readUniqueId();
        this.id = buffer.readInt();
        this.nodeName = buffer.readString();
        this.nodeUniqueId = buffer.readUniqueId();
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }
}
