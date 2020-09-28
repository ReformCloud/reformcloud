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
package systems.reformcloud.reformcloud2.executor.api.groups.utils;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

public class AutomaticStartupConfiguration implements SerializableObject {

    private boolean enabled;
    private int maxPercentOfPlayers;
    private long checkIntervalInSeconds;

    @ApiStatus.Internal
    public AutomaticStartupConfiguration() {
    }

    public AutomaticStartupConfiguration(boolean enabled, int maxPercentOfPlayers, long checkIntervalInSeconds) {
        this.enabled = enabled;
        this.maxPercentOfPlayers = maxPercentOfPlayers;
        this.checkIntervalInSeconds = checkIntervalInSeconds;
    }

    /**
     * @return The default values of an automatic startup config
     */
    @NotNull
    public static AutomaticStartupConfiguration defaults() {
        return new AutomaticStartupConfiguration(false, 70, 30);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getMaxPercentOfPlayers() {
        return this.maxPercentOfPlayers;
    }

    public long getCheckIntervalInSeconds() {
        return this.checkIntervalInSeconds;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeBoolean(this.enabled);
        buffer.writeInt(this.maxPercentOfPlayers);
        buffer.writeLong(this.checkIntervalInSeconds);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.enabled = buffer.readBoolean();
        this.maxPercentOfPlayers = buffer.readInt();
        this.checkIntervalInSeconds = buffer.readLong();
    }
}
