package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

import java.util.UUID;

public class PlayerLogoutEvent extends Event {

    public PlayerLogoutEvent(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    private final String name;

    private final UUID uuid;

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }
}
