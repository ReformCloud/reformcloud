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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class NetworkInfo implements SerializableObject {

    private transient InetAddress inetHost;
    private String plainHost; // for json writes only
    private int port;
    private long connectTime;

    @ApiStatus.Internal
    public NetworkInfo() {
    }

    public NetworkInfo(int port) {
        this.port = port;
        this.connectTime = -1L;
    }

    public String getHostPlain() {
        return this.getHost().getHostAddress();
    }

    public InetAddress getHost() {
        if (this.inetHost == null && this.plainHost != null) {
            try {
                this.inetHost = InetAddress.getByName(this.plainHost);
            } catch (UnknownHostException exception) {
                this.inetHost = InetAddress.getLoopbackAddress();
            }
        }

        return this.inetHost;
    }

    public void setHost(InetAddress host) {
        this.inetHost = host;
        this.plainHost = this.getHostPlain();
    }

    public InetSocketAddress toInet() {
        return new InetSocketAddress(this.inetHost, this.getPort());
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
        return this.getHostPlain() + ":" + this.port;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.getHostPlain());
        buffer.writeInt(this.port);
        buffer.writeLong(this.connectTime);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        try {
            this.inetHost = InetAddress.getByName(buffer.readString());
        } catch (UnknownHostException exception) {
            this.inetHost = InetAddress.getLoopbackAddress();
        }

        this.port = buffer.readInt();
        this.connectTime = buffer.readLong();
    }
}
