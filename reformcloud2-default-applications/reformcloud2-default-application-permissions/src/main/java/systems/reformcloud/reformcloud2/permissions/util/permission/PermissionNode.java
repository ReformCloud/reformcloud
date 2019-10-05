package systems.reformcloud.reformcloud2.permissions.util.permission;

public class PermissionNode {

    public PermissionNode(long addTime, long timeout, boolean set, String actualPermission) {
        this.addTime = addTime;
        this.timeout = timeout;
        this.set = set;
        this.actualPermission = actualPermission;
    }

    private final long addTime;

    private final long timeout;

    private final boolean set;

    private final String actualPermission;

    public long getAddTime() {
        return addTime;
    }

    public boolean isSet() {
        return set && (timeout == -1 || timeout > System.currentTimeMillis());
    }

    public boolean isValid() {
        return timeout == -1 || timeout > System.currentTimeMillis();
    }

    public String getActualPermission() {
        return actualPermission;
    }
}
