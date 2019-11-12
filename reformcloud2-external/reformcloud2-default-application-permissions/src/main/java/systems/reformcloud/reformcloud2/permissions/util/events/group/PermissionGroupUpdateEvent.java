package systems.reformcloud.reformcloud2.permissions.util.events.group;

import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;

public class PermissionGroupUpdateEvent extends Event {

    public PermissionGroupUpdateEvent(PermissionGroup permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    private final PermissionGroup permissionGroup;

    public PermissionGroup getPermissionGroup() {
        return permissionGroup;
    }
}
