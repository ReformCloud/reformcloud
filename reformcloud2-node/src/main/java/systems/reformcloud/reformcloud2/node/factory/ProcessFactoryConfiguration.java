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
package systems.reformcloud.reformcloud2.node.factory;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessInclusion;

import java.util.Collection;
import java.util.UUID;

public class ProcessFactoryConfiguration implements SerializableObject {

    protected String node;
    protected String displayName;
    protected String messageOfTheDay;

    protected ProcessGroup processGroup;
    protected Template template;
    protected Collection<ProcessInclusion> inclusions;
    protected JsonConfiguration extra;
    protected ProcessState initialState;
    protected UUID processUniqueId;

    protected int memory;
    protected int id;
    protected int maxPlayers;

    public ProcessFactoryConfiguration(String node, String displayName, String messageOfTheDay, ProcessGroup processGroup, Template template,
                                       Collection<ProcessInclusion> inclusions, JsonConfiguration extra, ProcessState initialState,
                                       UUID processUniqueId, int memory, int id, int maxPlayers) {
        this.node = node;
        this.displayName = displayName;
        this.messageOfTheDay = messageOfTheDay;
        this.processGroup = processGroup;
        this.template = template;
        this.inclusions = inclusions;
        this.extra = new JsonConfiguration();
        this.extra = extra;
        this.initialState = initialState;
        this.processUniqueId = processUniqueId;
        this.memory = memory;
        this.id = id;
        this.maxPlayers = maxPlayers;
    }

    public String getNode() {
        return this.node;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getMessageOfTheDay() {
        return this.messageOfTheDay;
    }

    public ProcessGroup getProcessGroup() {
        return this.processGroup;
    }

    public Template getTemplate() {
        return this.template;
    }

    public Collection<ProcessInclusion> getInclusions() {
        return this.inclusions;
    }

    public JsonConfiguration getExtra() {
        return this.extra;
    }

    public ProcessState getInitialState() {
        return this.initialState;
    }

    public UUID getProcessUniqueId() {
        return this.processUniqueId;
    }

    public int getMemory() {
        return this.memory;
    }

    public int getId() {
        return this.id;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.node);
        buffer.writeString(this.displayName);
        buffer.writeString(this.messageOfTheDay);

        buffer.writeObject(this.processGroup);
        buffer.writeObject(this.template);
        buffer.writeObjects(this.inclusions);
        buffer.writeArray(this.extra.toPrettyBytes());
        buffer.writeInt(this.initialState.ordinal());
        buffer.writeUniqueId(this.processUniqueId);

        buffer.writeInt(this.memory);
        buffer.writeInt(this.id);
        buffer.writeInt(this.maxPlayers);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.node = buffer.readString();
        this.displayName = buffer.readString();
        this.messageOfTheDay = buffer.readString();

        this.processGroup = buffer.readObject(ProcessGroup.class);
        this.template = buffer.readObject(Template.class);
        this.inclusions = buffer.readObjects(ProcessInclusion.class);
        this.extra = new JsonConfiguration(buffer.readArray());
        this.initialState = ProcessState.values()[buffer.readInt()];
        this.processUniqueId = buffer.readUniqueId();

        this.memory = buffer.readInt();
        this.id = buffer.readInt();
        this.maxPlayers = buffer.readInt();
    }
}
