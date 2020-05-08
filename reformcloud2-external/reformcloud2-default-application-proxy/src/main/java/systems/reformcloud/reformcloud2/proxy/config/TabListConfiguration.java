package systems.reformcloud.reformcloud2.proxy.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

public class TabListConfiguration implements SerializableObject {

    public TabListConfiguration() {
    }

    public TabListConfiguration(String header, String footer, long waitUntilNextInSeconds) {
        this.header = header;
        this.footer = footer;
        this.waitUntilNextInSeconds = waitUntilNextInSeconds;
    }

    private String header;

    private String footer;

    private long waitUntilNextInSeconds;

    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }

    public long getWaitUntilNextInSeconds() {
        return waitUntilNextInSeconds;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.header);
        buffer.writeString(this.footer);
        buffer.writeLong(this.waitUntilNextInSeconds);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.header = buffer.readString();
        this.footer = buffer.readString();
        this.waitUntilNextInSeconds = buffer.readLong();
    }
}
