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
package systems.reformcloud.reformcloud2.executor.api.common.node;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessRuntimeInformation;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class NodeInformation implements SerializableObject {

    public static final TypeToken<NodeInformation> TYPE = new TypeToken<NodeInformation>() {
    };

    private String name;

    private UUID nodeUniqueID;

    private long startupTime;

    private long lastUpdate;

    private long usedMemory;

    private long maxMemory;

    private ProcessRuntimeInformation processRuntimeInformation;

    private Collection<NodeProcess> startedProcesses;

    public NodeInformation(String name, UUID nodeUniqueID, long startupTime,
                           long usedMemory, long maxMemory, Collection<NodeProcess> startedProcesses) {
        this.name = name;
        this.nodeUniqueID = nodeUniqueID;
        this.startupTime = this.lastUpdate = startupTime;
        this.usedMemory = usedMemory;
        this.maxMemory = maxMemory;
        this.processRuntimeInformation = ProcessRuntimeInformation.create();
        this.startedProcesses = new CopyOnWriteArrayList<>(startedProcesses);
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public UUID getNodeUniqueID() {
        return this.nodeUniqueID;
    }

    public long getStartupTime() {
        return this.startupTime;
    }

    public long getLastUpdate() {
        return this.lastUpdate;
    }

    public long getUsedMemory() {
        return this.usedMemory;
    }

    public long getMaxMemory() {
        return this.maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    @NotNull
    public ProcessRuntimeInformation getProcessRuntimeInformation() {
        return this.processRuntimeInformation;
    }

    @NotNull
    public Collection<NodeProcess> getStartedProcesses() {
        return this.startedProcesses;
    }

    public void addUsedMemory(int memory) {
        this.usedMemory += memory;
    }

    public void removeUsedMemory(int memory) {
        this.usedMemory -= memory;
    }

    public void update() {
        this.processRuntimeInformation = ProcessRuntimeInformation.create();
        this.lastUpdate = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeInformation)) return false;
        NodeInformation that = (NodeInformation) o;
        return this.getStartupTime() == that.getStartupTime() &&
                this.getLastUpdate() == that.getLastUpdate() &&
                this.getUsedMemory() == that.getUsedMemory() &&
                this.getMaxMemory() == that.getMaxMemory() &&
                Objects.equals(this.getName(), that.getName()) &&
                Objects.equals(this.getNodeUniqueID(), that.getNodeUniqueID()) &&
                Objects.equals(this.getProcessRuntimeInformation(), that.getProcessRuntimeInformation()) &&
                Objects.equals(this.getStartedProcesses(), that.getStartedProcesses());
    }

    public boolean canEqual(@NotNull NodeInformation other) {
        return this.getNodeUniqueID().equals(other.getNodeUniqueID());
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeUniqueId(this.nodeUniqueID);

        buffer.writeLong(this.startupTime);
        buffer.writeLong(this.lastUpdate);
        buffer.writeLong(this.usedMemory);
        buffer.writeLong(this.maxMemory);

        buffer.writeObject(this.processRuntimeInformation);
        buffer.writeObjects(this.startedProcesses);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.name = buffer.readString();
        this.nodeUniqueID = buffer.readUniqueId();

        this.startupTime = buffer.readLong();
        this.lastUpdate = buffer.readLong();
        this.usedMemory = buffer.readLong();
        this.maxMemory = buffer.readLong();

        this.processRuntimeInformation = buffer.readObject(ProcessRuntimeInformation.class);
        this.startedProcesses = buffer.readObjects(NodeProcess.class);
    }
}
