package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

import java.util.UUID;

public class PlayerServerSwitchEvent extends Event {

    public PlayerServerSwitchEvent(@NotNull UUID uuid, @NotNull String targetServer) {
        this.uuid = uuid;
        this.targetServer = targetServer;
    }

    private final UUID uuid;

    private final String targetServer;

    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    @NotNull
    public String getTargetServer() {
        return targetServer;
    }
}
