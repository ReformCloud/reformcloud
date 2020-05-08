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
package systems.reformcloud.reformcloud2.executor.api.common.groups.template;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class RuntimeConfiguration implements SerializableObject {

    @ApiStatus.Internal
    public RuntimeConfiguration() {
    }

    public RuntimeConfiguration(int maxMemory, List<String> processParameters, Map<String, String> systemProperties) {
        this(maxMemory, processParameters, new ArrayList<>(), systemProperties);
    }

    public RuntimeConfiguration(int maxMemory, List<String> processParameters, List<String> jvmOptions, Map<String, String> systemProperties) {
        this(maxMemory, processParameters, jvmOptions, systemProperties, new ArrayList<>());
    }

    public RuntimeConfiguration(int maxMemory, List<String> processParameters, List<String> jvmOptions,
                                Map<String, String> systemProperties, Collection<String> shutdownCommands) {
        this(maxMemory, -1, processParameters, jvmOptions, systemProperties, shutdownCommands);
    }

    public RuntimeConfiguration(int maxMemory, int dynamicMemory, List<String> processParameters, List<String> jvmOptions,
                                Map<String, String> systemProperties, Collection<String> shutdownCommands) {
        this.maxMemory = maxMemory;
        this.dynamicMemory = dynamicMemory;
        this.processParameters = processParameters;
        this.jvmOptions = jvmOptions;
        this.systemProperties = systemProperties;
        this.shutdownCommands = shutdownCommands;
    }

    private int maxMemory;

    private int dynamicMemory;

    private List<String> processParameters;

    private List<String> jvmOptions;

    private Map<String, String> systemProperties;

    private Collection<String> shutdownCommands;

    public int getMaxMemory() {
        return maxMemory;
    }

    public int getDynamicMemory() {
        return dynamicMemory;
    }

    public void setMaxMemory(int maxMemory) {
        this.maxMemory = maxMemory;
    }

    public List<String> getProcessParameters() {
        return processParameters;
    }

    public List<String> getJvmOptions() {
        return jvmOptions;
    }

    public Map<String, String> getSystemProperties() {
        return systemProperties;
    }

    /* Needs a null check because of add in version 2.0.2 */
    public Collection<String> getShutdownCommands() {
        return shutdownCommands == null ? new CopyOnWriteArrayList<>() : new CopyOnWriteArrayList<>(shutdownCommands);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeVarInt(this.maxMemory);
        buffer.writeVarInt(this.dynamicMemory);
        buffer.writeStringArray(this.processParameters);
        buffer.writeStringArray(this.jvmOptions);
        buffer.writeStringMap(this.systemProperties);
        buffer.writeStringArray(this.shutdownCommands);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.maxMemory = buffer.readVarInt();
        this.dynamicMemory = buffer.readVarInt();
        this.processParameters = buffer.readStringArray();
        this.jvmOptions = buffer.readStringArray();
        this.systemProperties = buffer.readStringMap();
        this.shutdownCommands = buffer.readStringArray();
    }
}
