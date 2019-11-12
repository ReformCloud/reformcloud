package systems.reformcloud.reformcloud2.permissions.util.events.user;

import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

import java.util.UUID;

public class PermissionUserDeleteEvent extends Event {

    public PermissionUserDeleteEvent(UUID uuid) {
        this.uuid = uuid;
    }

    private final UUID uuid;

    public UUID getUuid() {
        return uuid;
    }
}
