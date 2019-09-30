package de.klaro.reformcloud2.permissions.util.events.user;

import de.klaro.reformcloud2.permissions.util.user.PermissionUser;

public class PermissionUserCreateEvent {

    public PermissionUserCreateEvent(PermissionUser permissionUser) {
        this.permissionUser = permissionUser;
    }

    private final PermissionUser permissionUser;

    public PermissionUser getPermissionUser() {
        return permissionUser;
    }
}
