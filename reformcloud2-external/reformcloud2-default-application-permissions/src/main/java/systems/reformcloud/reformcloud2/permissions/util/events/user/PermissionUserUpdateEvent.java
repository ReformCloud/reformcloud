package systems.reformcloud.reformcloud2.permissions.util.events.user;

import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

public class PermissionUserUpdateEvent extends Event {

    public PermissionUserUpdateEvent(PermissionUser permissionUser) {
        this.permissionUser = permissionUser;
    }

    private final PermissionUser permissionUser;

    public PermissionUser getPermissionUser() {
        return permissionUser;
    }
}
