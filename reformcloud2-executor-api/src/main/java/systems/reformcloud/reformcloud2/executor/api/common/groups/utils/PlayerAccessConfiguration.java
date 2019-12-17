package systems.reformcloud.reformcloud2.executor.api.common.groups.utils;

import java.util.Objects;
import javax.annotation.Nonnull;

public final class PlayerAccessConfiguration {

  public PlayerAccessConfiguration(String fullJoinPermission,
                                   boolean maintenance,
                                   String maintenanceJoinPermission,
                                   boolean joinOnlyPerPermission,
                                   String joinPermission, boolean onlyProxyJoin,
                                   boolean playerControllerCommandReporting,
                                   boolean useCloudPlayerLimit,
                                   int maxPlayers) {
    this.fullJoinPermission = fullJoinPermission;
    this.maintenance = maintenance;
    this.maintenanceJoinPermission = maintenanceJoinPermission;
    this.joinOnlyPerPermission = joinOnlyPerPermission;
    this.joinPermission = joinPermission;
    this.onlyProxyJoin = onlyProxyJoin;
    this.playerControllerCommandReporting = playerControllerCommandReporting;
    this.useCloudPlayerLimit = useCloudPlayerLimit;
    this.maxPlayers = maxPlayers;
  }

  private String fullJoinPermission;

  private boolean maintenance;

  private String maintenanceJoinPermission;

  private boolean joinOnlyPerPermission;

  private String joinPermission;

  private boolean onlyProxyJoin;

  private boolean playerControllerCommandReporting;

  private boolean useCloudPlayerLimit;

  private int maxPlayers;

  @Nonnull
  public String getFullJoinPermission() {
    return fullJoinPermission == null ? "reformcloud.join.full"
                                      : fullJoinPermission;
  }

  public boolean isMaintenance() { return maintenance; }

  @Nonnull
  public String getMaintenanceJoinPermission() {
    return maintenanceJoinPermission == null ? "reformcloud.join.maintenance"
                                             : maintenanceJoinPermission;
  }

  public boolean isJoinOnlyPerPermission() { return joinOnlyPerPermission; }

  @Nonnull
  public String getJoinPermission() {
    return joinPermission == null ? "reformcloud.custom.permission"
                                  : joinPermission;
  }

  public boolean isOnlyProxyJoin() { return onlyProxyJoin; }

  public boolean isPlayerControllerCommandReporting() {
    return playerControllerCommandReporting;
  }

  public boolean isUseCloudPlayerLimit() { return useCloudPlayerLimit; }

  public int getMaxPlayers() { return maxPlayers; }

  public void toggleMaintenance() { this.maintenance = !this.maintenance; }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof PlayerAccessConfiguration))
      return false;
    PlayerAccessConfiguration that = (PlayerAccessConfiguration)o;
    return isMaintenance() == that.isMaintenance() &&
        isJoinOnlyPerPermission() == that.isJoinOnlyPerPermission() &&
        isOnlyProxyJoin() == that.isOnlyProxyJoin() &&
        isPlayerControllerCommandReporting() ==
            that.isPlayerControllerCommandReporting() &&
        isUseCloudPlayerLimit() == that.isUseCloudPlayerLimit() &&
        getMaxPlayers() == that.getMaxPlayers() &&
        Objects.equals(getMaintenanceJoinPermission(),
                       that.getMaintenanceJoinPermission()) &&
        Objects.equals(getJoinPermission(), that.getJoinPermission());
  }
}
