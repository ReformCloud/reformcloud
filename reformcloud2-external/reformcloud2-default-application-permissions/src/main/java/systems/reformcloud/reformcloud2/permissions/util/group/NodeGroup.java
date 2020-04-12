package systems.reformcloud.reformcloud2.permissions.util.group;

import org.jetbrains.annotations.NotNull;

public class NodeGroup {

    public NodeGroup(long addTime, long timeout, @NotNull String groupName) {
        this.addTime = addTime;
        this.timeout = timeout;
        this.groupName = groupName;
    }

    private final long addTime;

    private final long timeout;

    private final String groupName;

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
}
