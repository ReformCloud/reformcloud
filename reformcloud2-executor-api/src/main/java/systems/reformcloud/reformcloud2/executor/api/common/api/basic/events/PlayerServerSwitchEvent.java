package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

import java.util.UUID;

public class PlayerServerSwitchEvent extends Event {

    public PlayerServerSwitchEvent(UUID uniqueId, String name, String targetServer) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.targetServer = targetServer;
    }

    private final UUID uniqueId;

    private final String name;

    private final String targetServer;

    @NotNull
    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    @NotNull
    public String getTargetServer() {
        return targetServer;
    }
}
