package systems.reformcloud.reformcloud2.executor.api.common.groups.utils;

import org.jetbrains.annotations.NotNull;

public class AutomaticStartupConfiguration {

    public AutomaticStartupConfiguration(boolean enabled, int maxPercentOfPlayers, long checkIntervalInSeconds) {
        this.enabled = enabled;
        this.maxPercentOfPlayers = maxPercentOfPlayers;
        this.checkIntervalInSeconds = checkIntervalInSeconds;
    }

    private final boolean enabled;

    private final int maxPercentOfPlayers;

    private final long checkIntervalInSeconds;

    public boolean isEnabled() {
        return enabled;
    }

    public int getMaxPercentOfPlayers() {
        return maxPercentOfPlayers;
    }

    public long getCheckIntervalInSeconds() {
        return checkIntervalInSeconds;
    }

    /**
     * @return The default values of an automatic startup config
     */
    @NotNull
    public static AutomaticStartupConfiguration defaults() {
        return new AutomaticStartupConfiguration(false, 70, 30);
    }
}
