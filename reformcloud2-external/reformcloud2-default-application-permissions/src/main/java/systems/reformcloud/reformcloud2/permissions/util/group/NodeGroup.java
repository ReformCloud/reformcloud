package systems.reformcloud.reformcloud2.permissions.util.group;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

public class NodeGroup implements SerializableObject {

    public NodeGroup() {
    }

    public NodeGroup(long addTime, long timeout, @NotNull String groupName) {
        this.addTime = addTime;
        this.timeout = timeout;
        this.groupName = groupName;
    }

    private long addTime;

    private long timeout;

    private String groupName;

    public long getAddTime() {
        return addTime;
    }

    public long getTimeout() {
        return timeout;
    }

    public boolean isValid() {
        return timeout == -1 || timeout > System.currentTimeMillis();
    }

    @NotNull
    public String getGroupName() {
        return groupName;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeLong(this.addTime);
        buffer.writeLong(this.timeout);
        buffer.writeString(this.groupName);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.addTime = buffer.readLong();
        this.timeout = buffer.readLong();
        this.groupName = buffer.readString();
    }
}
