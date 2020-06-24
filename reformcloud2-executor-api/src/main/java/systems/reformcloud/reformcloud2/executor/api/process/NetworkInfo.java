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
package systems.reformcloud.reformcloud2.executor.api.process;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

import java.net.InetSocketAddress;

public final class NetworkInfo implements SerializableObject {

    private String host;
    private int port;
    private long connectTime;

    @ApiStatus.Internal
    public NetworkInfo() {
    }

    public NetworkInfo(String host, int port) {
        this.host = host;
        this.port = port;
        this.connectTime = -1L;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public InetSocketAddress toInet() {
        return new InetSocketAddress(this.getHost(), this.getPort());
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isConnected() {
        return this.connectTime != -1;
    }

    public void setConnected(boolean connected) {
        if (connected) {
            this.connectTime = System.currentTimeMillis();
        } else {
            this.connectTime = -1;
        }
    }

    public long getConnectTime() {
        return this.connectTime;
    }

    @Override
    public String toString() {
        return this.host + ":" + this.port;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.host);
        buffer.writeVarInt(this.port);
        buffer.writeVarLong(this.connectTime);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.host = buffer.readString();
        this.port = buffer.readVarInt();
        this.connectTime = buffer.readVarLong();
    }
}
