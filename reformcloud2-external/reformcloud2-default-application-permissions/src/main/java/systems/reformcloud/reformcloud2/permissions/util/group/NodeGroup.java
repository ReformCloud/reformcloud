package systems.reformcloud.reformcloud2.permissions.util.group;

import javax.annotation.Nonnull;

public class NodeGroup {

  public NodeGroup(long addTime, long timeout, @Nonnull String groupName) {
    this.addTime = addTime;
    this.timeout = timeout;
    this.groupName = groupName;
  }

  private final long addTime;

  private final long timeout;

  private final String groupName;

  public long getAddTime() { return addTime; }

  public long getTimeout() { return timeout; }

  public boolean isValid() {
    return timeout == -1 || timeout > System.currentTimeMillis();
  }

  @Nonnull
  public String getGroupName() {
    return groupName;
  }
}
