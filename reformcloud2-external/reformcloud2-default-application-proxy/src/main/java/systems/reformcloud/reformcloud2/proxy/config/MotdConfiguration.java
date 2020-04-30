package systems.reformcloud.reformcloud2.proxy.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

public class MotdConfiguration implements SerializableObject {

    public MotdConfiguration() {
    }

    public MotdConfiguration(String firstLine, String secondLine, String[] playerInfo, String protocol, long waitUntilNextInSeconds) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
        this.playerInfo = playerInfo;
        this.protocol = protocol;
        this.waitUntilNextInSeconds = waitUntilNextInSeconds;
    }

    private String firstLine;

    private String secondLine;

    private String[] playerInfo;

    private String protocol;

    private long waitUntilNextInSeconds;

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
