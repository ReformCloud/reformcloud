package systems.reformcloud.reformcloud2.executor.api.common.process;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.net.InetSocketAddress;

public final class NetworkInfo implements SerializableObject {

    @ApiStatus.Internal
    public NetworkInfo() {
    }

    public NetworkInfo(String host, int port) {
        this.host = host;
        this.port = port;
        this.connectTime = -1L;
    }

    private String host;

    private int port;

    private long connectTime;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public InetSocketAddress toInet() {
        return new InetSocketAddress(getHost(), getPort());
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isConnected() {
        return connectTime != -1;
    }

    public void setConnected(boolean connected) {
        if (connected) {
            this.connectTime = System.currentTimeMillis();
        } else {
            this.connectTime = -1;
        }
    }

    public long getConnectTime() {
        return connectTime;
    }

    @Override
    public String toString() {
        return host + ":" + port;
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
