package systems.reformcloud.reformcloud2.executor.api.common.groups.utils;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

public class AutomaticStartupConfiguration implements SerializableObject {

    @ApiStatus.Internal
    public AutomaticStartupConfiguration() {
    }

    public AutomaticStartupConfiguration(boolean enabled, int maxPercentOfPlayers, long checkIntervalInSeconds) {
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

    /**
     * @return The default values of an automatic startup config
     */
    @NotNull
    public static AutomaticStartupConfiguration defaults() {
        return new AutomaticStartupConfiguration(false, 70, 30);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeBoolean(this.enabled);
        buffer.writeInt(this.maxPercentOfPlayers);
        buffer.writeLong(this.checkIntervalInSeconds);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.enabled = buffer.readBoolean();
        this.maxPercentOfPlayers = buffer.readInt();
        this.checkIntervalInSeconds = buffer.readLong();
    }
}
