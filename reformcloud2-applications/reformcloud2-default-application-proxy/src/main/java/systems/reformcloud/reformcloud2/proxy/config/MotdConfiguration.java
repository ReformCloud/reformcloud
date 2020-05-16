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
package systems.reformcloud.reformcloud2.proxy.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

public class MotdConfiguration implements SerializableObject {

    private String firstLine;
    private String secondLine;
    private String[] playerInfo;
    private String protocol;
    private long waitUntilNextInSeconds;

    public MotdConfiguration() {
    }

    public MotdConfiguration(String firstLine, String secondLine, String[] playerInfo, String protocol, long waitUntilNextInSeconds) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
        this.playerInfo = playerInfo;
        this.protocol = protocol;
        this.waitUntilNextInSeconds = waitUntilNextInSeconds;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }

    public String[] getPlayerInfo() {
        return playerInfo;
    }

    public String getProtocol() {
        return protocol;
    }

    public long getWaitUntilNextInSeconds() {
        return waitUntilNextInSeconds;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.firstLine);
        buffer.writeString(this.secondLine);
        buffer.writeStringArrays(this.playerInfo);
        buffer.writeString(this.protocol);
        buffer.writeLong(this.waitUntilNextInSeconds);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.firstLine = buffer.readString();
        this.secondLine = buffer.readString();
        this.playerInfo = buffer.readStringArrays();
        this.protocol = buffer.readString();
        this.waitUntilNextInSeconds = buffer.readLong();
    }
}
