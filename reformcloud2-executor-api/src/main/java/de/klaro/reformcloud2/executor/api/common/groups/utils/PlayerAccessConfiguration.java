package de.klaro.reformcloud2.executor.api.common.groups.utils;

public final class PlayerAccessConfiguration {

    public PlayerAccessConfiguration(boolean maintenance, String maintenanceJoinPermission,
                                     boolean joinOnlyPerPermission, String joinPermission,
                                     boolean onlyProxyJoin, boolean playerControllerCommandReporting,
                                     boolean useCloudPlayerLimit, int maxPlayers) {
        this.maintenance = maintenance;
        this.maintenanceJoinPermission = maintenanceJoinPermission;
        this.joinOnlyPerPermission = joinOnlyPerPermission;
        this.joinPermission = joinPermission;
        this.onlyProxyJoin = onlyProxyJoin;
        this.playerControllerCommandReporting = playerControllerCommandReporting;
        this.useCloudPlayerLimit = useCloudPlayerLimit;
        this.maxPlayers = maxPlayers;
    }

    private boolean maintenance;

    private String maintenanceJoinPermission;

    private boolean joinOnlyPerPermission;

    private String joinPermission;

    private boolean onlyProxyJoin;

    private boolean playerControllerCommandReporting;

    private boolean useCloudPlayerLimit;

    private int maxPlayers;

    public boolean isMaintenance() {
        return maintenance;
    }

    public String getMaintenanceJoinPermission() {
        return maintenanceJoinPermission;
    }

    public boolean isJoinOnlyPerPermission() {
        return joinOnlyPerPermission;
    }

    public String getJoinPermission() {
        return joinPermission;
    }

    public boolean isOnlyProxyJoin() {
        return onlyProxyJoin;
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

    public void toggleMaintenance() {
        this.maintenance = !this.maintenance;
    }
}
