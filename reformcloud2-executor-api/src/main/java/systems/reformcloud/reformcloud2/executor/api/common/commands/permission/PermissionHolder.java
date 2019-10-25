package systems.reformcloud.reformcloud2.executor.api.common.commands.permission;

import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.Collection;

public interface PermissionHolder extends Nameable {

    /**
     * Checks if the user has the specified permission
     *
     * @param permission The permission which should be checked
     * @return If the user has the permission
     */
    boolean hasPermission(String permission);

    /**
     * Checks if the given permission value is set
     *
     * @param permission The permission which should be checked
     * @return If the permission is set
     */
    boolean isPermissionSet(String permission);

    /**
     * Checks if the user has the specified permission
     *
     * @param permission Checks if the user has the given {@link Permission}
     * @return If the user has the permission else {@link Permission#defaultResult()}
     */
    boolean hasPermission(Permission permission);

    /**
     * Checks if the user has the specified permission
     *
     * @param permission Checks if the user has the given {@link Permission}
     * @return If the user has the permission else {@link Permission#defaultResult()}
     */
    boolean isPermissionSet(Permission permission);

    /**
     * @return All permissions of the user
     */
    Collection<Permission> getEffectivePermissions();

    /**
     * Recalculates the permission of the user
     */
    void recalculatePermissions();

    /**
     * @return The permission check for the current user
     */
    PermissionCheck check();
}
