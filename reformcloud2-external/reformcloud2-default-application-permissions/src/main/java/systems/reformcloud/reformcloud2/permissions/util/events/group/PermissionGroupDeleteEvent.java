package systems.reformcloud.reformcloud2.permissions.util.events.group;

import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

public class PermissionGroupDeleteEvent extends Event {

    public PermissionGroupDeleteEvent(String name) {
        this.name = name;
    }

    private final String name;

    public String getName() {
        return name;
    }
}
