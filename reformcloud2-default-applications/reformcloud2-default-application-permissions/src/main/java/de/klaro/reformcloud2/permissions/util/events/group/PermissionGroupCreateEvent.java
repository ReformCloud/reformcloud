package de.klaro.reformcloud2.permissions.util.events.group;

import de.klaro.reformcloud2.executor.api.common.event.Event;
import de.klaro.reformcloud2.permissions.util.group.PermissionGroup;

public class PermissionGroupCreateEvent extends Event {

    public PermissionGroupCreateEvent(PermissionGroup permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    private final PermissionGroup permissionGroup;

    public PermissionGroup getPermissionGroup() {
        return permissionGroup;
    }
}
