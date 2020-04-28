package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

import java.util.UUID;

public class PlayerServerSwitchEvent extends Event {

    public PlayerServerSwitchEvent(UUID uniqueId, String name, String previousServer, String targetServer) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.targetServer = targetServer;
        this.previousServer = previousServer;
    }

    private final UUID uniqueId;

    private final String name;

    private final String targetServer;

    private final String previousServer;

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

    @Nullable
    public String getPreviousServer() {
        return previousServer;
    }
}
