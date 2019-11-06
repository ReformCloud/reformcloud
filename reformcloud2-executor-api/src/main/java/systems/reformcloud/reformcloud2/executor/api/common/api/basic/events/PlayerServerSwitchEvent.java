package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PlayerServerSwitchEvent extends Event {

    public PlayerServerSwitchEvent(@Nonnull UUID uuid, @Nonnull String targetServer) {
        this.uuid = uuid;
        this.targetServer = targetServer;
    }

    private final UUID uuid;

    private final String targetServer;

    @Nonnull
    public UUID getUuid() {
        return uuid;
    }

    @Nonnull
    public String getTargetServer() {
        return targetServer;
    }
}
