package systems.reformcloud.reformcloud2.permissions.util.permission;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

public class PermissionNode implements SerializableObject {

    public PermissionNode() {
    }

    public PermissionNode(long addTime, long timeout, boolean set, @NotNull String actualPermission) {
        this.addTime = addTime;
        this.timeout = timeout;
        this.set = set;
        this.actualPermission = actualPermission;
    }

    private long addTime;

    private long timeout;

    private boolean set;

    private String actualPermission;

    public long getAddTime() {
        return addTime;
    }

    public long getTimeout() {
        return timeout;
    }

    public boolean isSet() {
        return set && (timeout == -1 || timeout > System.currentTimeMillis());
    }

    public boolean isValid() {
        return timeout == -1 || timeout > System.currentTimeMillis();
    }

    @NotNull
    public String getActualPermission() {
        return actualPermission;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeLong(this.addTime);
        buffer.writeLong(this.timeout);
        buffer.writeBoolean(this.set);
        buffer.writeString(this.actualPermission);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.addTime = buffer.readLong();
        this.timeout = buffer.readLong();
        this.set = buffer.readBoolean();
        this.actualPermission = buffer.readString();
    }
}
