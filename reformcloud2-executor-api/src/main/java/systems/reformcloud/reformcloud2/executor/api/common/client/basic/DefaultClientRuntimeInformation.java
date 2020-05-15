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
package systems.reformcloud.reformcloud2.executor.api.common.client.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.util.UUID;

public final class DefaultClientRuntimeInformation implements ClientRuntimeInformation, SerializableObject {

    private String startHost;
    private int maxMemory;
    private int maxProcesses;
    private String name;
    private UUID uniqueID;

    public DefaultClientRuntimeInformation() {
    }

    public DefaultClientRuntimeInformation(String startHost, int maxMemory, int maxProcesses, String name, UUID uniqueId) {
        this.startHost = startHost;
        this.maxMemory = maxMemory;
        this.maxProcesses = maxProcesses;
        this.name = name;
        this.uniqueID = uniqueId;
    }

    @NotNull
    @Override
    public String startHost() {
        return startHost;
    }

    @NotNull
    @Override
    public UUID uniqueID() {
        return uniqueID;
    }

    @Override
    public int maxMemory() {
        return maxMemory;
    }

    @Override
    public int maxProcessCount() {
        return maxProcesses;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.startHost);
        buffer.writeInt(this.maxMemory);
        buffer.writeInt(this.maxProcesses);
        buffer.writeString(this.name);
        buffer.writeUniqueId(this.uniqueID);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.startHost = buffer.readString();
        this.maxMemory = buffer.readInt();
        this.maxProcesses = buffer.readInt();
        this.name = buffer.readString();
        this.uniqueID = buffer.readUniqueId();
    }
}
