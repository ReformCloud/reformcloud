package systems.reformcloud.reformcloud2.executor.api.common.commands.permission;

import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.Collection;

public interface PermissionHolder extends Nameable {

    boolean hasPermission(String permission);

    boolean isPermissionSet(String permission);

    boolean hasPermission(Permission permission);

    boolean isPermissionSet(Permission permission);

    Collection<Permission> getEffectivePermissions();

    void recalculatePermissions();

    PermissionCheck check();
}
