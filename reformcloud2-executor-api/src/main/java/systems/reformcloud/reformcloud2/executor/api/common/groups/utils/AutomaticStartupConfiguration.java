package systems.reformcloud.reformcloud2.executor.api.common.groups.utils;

public class AutomaticStartupConfiguration {

    private AutomaticStartupConfiguration(boolean enabled, int maxPercentOfPlayers, long checkIntervalInSeconds) {
        this.enabled = enabled;
        this.maxPercentOfPlayers = maxPercentOfPlayers;
        this.checkIntervalInSeconds = checkIntervalInSeconds;
    }

    private boolean enabled;

    private int maxPercentOfPlayers;

    private long checkIntervalInSeconds;

    public boolean isEnabled() {
        return enabled;
    }

    public int getMaxPercentOfPlayers() {
        return maxPercentOfPlayers;
    }

    public long getCheckIntervalInSeconds() {
        return checkIntervalInSeconds;
    }

    static AutomaticStartupConfiguration defaults() {
        return new AutomaticStartupConfiguration(false, 70, 30);
    }
}
