package systems.reformcloud.reformcloud2.executor.api.common.groups.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class PlayerAccessConfiguration {

    public PlayerAccessConfiguration(String fullJoinPermission, boolean maintenance, String maintenanceJoinPermission,
                                     boolean joinOnlyPerPermission, String joinPermission,
                                     boolean playerControllerCommandReporting, boolean useCloudPlayerLimit, int maxPlayers) {
        this.fullJoinPermission = fullJoinPermission;
        this.maintenance = maintenance;
        this.maintenanceJoinPermission = maintenanceJoinPermission;
        this.joinOnlyPerPermission = joinOnlyPerPermission;
        this.joinPermission = joinPermission;
        this.playerControllerCommandReporting = playerControllerCommandReporting;
        this.useCloudPlayerLimit = useCloudPlayerLimit;
        this.maxPlayers = maxPlayers;
    }

    private final String fullJoinPermission;

    private boolean maintenance;

    private final String maintenanceJoinPermission;

    private final boolean joinOnlyPerPermission;

    private final String joinPermission;

    private final boolean playerControllerCommandReporting;

    private boolean useCloudPlayerLimit;

    private int maxPlayers;

    @NotNull
    public String getFullJoinPermission() {
        return fullJoinPermission == null ? "reformcloud.join.full" : fullJoinPermission;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    @NotNull
    public String getMaintenanceJoinPermission() {
        return maintenanceJoinPermission == null ? "reformcloud.join.maintenance" : maintenanceJoinPermission;
    }

    public boolean isJoinOnlyPerPermission() {
        return joinOnlyPerPermission;
    }

    @NotNull
    public String getJoinPermission() {
        return joinPermission == null ? "reformcloud.custom.permission" : joinPermission;
    }

    public boolean isPlayerControllerCommandReporting() {
        return playerControllerCommandReporting;
    }

    public boolean isUseCloudPlayerLimit() {
        return useCloudPlayerLimit;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    public void toggleMaintenance() {
        this.maintenance = !this.maintenance;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.useCloudPlayerLimit = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerAccessConfiguration)) return false;
        PlayerAccessConfiguration that = (PlayerAccessConfiguration) o;
        return isMaintenance() == that.isMaintenance() &&
                isJoinOnlyPerPermission() == that.isJoinOnlyPerPermission() &&
                isPlayerControllerCommandReporting() == that.isPlayerControllerCommandReporting() &&
                isUseCloudPlayerLimit() == that.isUseCloudPlayerLimit() &&
                getMaxPlayers() == that.getMaxPlayers() &&
                Objects.equals(getMaintenanceJoinPermission(), that.getMaintenanceJoinPermission()) &&
                Objects.equals(getJoinPermission(), that.getJoinPermission());
    }
}
