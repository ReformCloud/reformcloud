package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

import java.util.UUID;

public class PlayerServerSwitchEvent extends Event {

    public PlayerServerSwitchEvent(UUID uuid, String targetServer) {
        this.uuid = uuid;
        this.targetServer = targetServer;
    }

    private final UUID uuid;

    private final String targetServer;

    public UUID getUuid() {
        return uuid;
    }

    public String getTargetServer() {
        return targetServer;
    }
}
