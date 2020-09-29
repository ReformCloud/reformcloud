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
package systems.reformcloud.reformcloud2.commands.config;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

import java.util.List;

public class CommandsConfig implements SerializableObject {

    private boolean leaveCommandEnabled;
    private List<String> leaveCommands;
    private boolean reformCloudCommandEnabled;
    private List<String> reformCloudCommands;

    @ApiStatus.Internal
    public CommandsConfig() {
    }

    public CommandsConfig(boolean leaveCommandEnabled, List<String> leaveCommands, boolean reformCloudCommandEnabled, List<String> reformCloudCommands) {
        this.leaveCommandEnabled = leaveCommandEnabled;
        this.leaveCommands = leaveCommands;
        this.reformCloudCommandEnabled = reformCloudCommandEnabled;
        this.reformCloudCommands = reformCloudCommands;
    }

    public boolean isLeaveCommandEnabled() {
        return this.leaveCommandEnabled;
    }

    public List<String> getLeaveCommands() {
        return this.leaveCommands;
    }

    public boolean isReformCloudCommandEnabled() {
        return this.reformCloudCommandEnabled;
    }

    public List<String> getReformCloudCommands() {
        return this.reformCloudCommands;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeBoolean(this.leaveCommandEnabled);
        buffer.writeStringArray(this.leaveCommands);
        buffer.writeBoolean(this.reformCloudCommandEnabled);
        buffer.writeStringArray(this.reformCloudCommands);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.leaveCommandEnabled = buffer.readBoolean();
        this.leaveCommands = buffer.readStringArray();
        this.reformCloudCommandEnabled = buffer.readBoolean();
        this.reformCloudCommands = buffer.readStringArray();
    }
}
