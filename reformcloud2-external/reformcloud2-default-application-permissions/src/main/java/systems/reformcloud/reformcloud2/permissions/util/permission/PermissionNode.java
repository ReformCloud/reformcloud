package systems.reformcloud.reformcloud2.permissions.util.permission;

import javax.annotation.Nonnull;

public class PermissionNode {

  public PermissionNode(long addTime, long timeout, boolean set,
                        @Nonnull String actualPermission) {
    this.addTime = addTime;
    this.timeout = timeout;
    this.set = set;
    this.actualPermission = actualPermission;
  }

  private final long addTime;

  private final long timeout;

  private final boolean set;

  private final String actualPermission;

  public long getAddTime() { return addTime; }

  public long getTimeout() { return timeout; }

  public boolean isSet() {
    return set && (timeout == -1 || timeout > System.currentTimeMillis());
  }

  public boolean isValid() {
    return timeout == -1 || timeout > System.currentTimeMillis();
  }

  @Nonnull
  public String getActualPermission() {
    return actualPermission;
  }
}
