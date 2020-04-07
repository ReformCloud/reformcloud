package systems.reformcloud.reformcloud2.permissions.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.permissions.util.group.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

import java.util.Collection;
import java.util.UUID;

public interface PermissionUtil {

    /**
     * Get a specific group
     *
     * @param name The name of the group
     * @return The {@link PermissionGroup} or {@code null} if the group does not exists
     */
    @Nullable
    PermissionGroup getGroup(@NotNull String name);

    /**
     * Updates a specific permission group
     *
     * @param permissionGroup The permission group which should be updated
     */
    void updateGroup(@NotNull PermissionGroup permissionGroup);

    /**
     * Adds a specific permission to a permission group
     *
     * @param permissionGroup The group which should get the permission
     * @param permissionNode  The permission which should be added
     */
    void addGroupPermission(
            @NotNull PermissionGroup permissionGroup,
            @NotNull PermissionNode permissionNode
    );

    /**
     * Adds a specific permission to a permission group on a specific process group
     *
     * @param processGroup    The process group on which the permission should be valid
     * @param permissionGroup The group which should get the permission
     * @param permissionNode  The permission which should be added
     */
    void addProcessGroupPermission(
            @NotNull String processGroup,
            @NotNull PermissionGroup permissionGroup,
            @NotNull PermissionNode permissionNode
    );

    /**
     * Creates a new group
     *
     * @param name The name of the new group
     * @return The created group or the group which already exists
     */
    @NotNull
    PermissionGroup createGroup(@NotNull String name);

    /**
     * Adds a new default group to the collection of all default groups
     *
     * @param group The name of the group which should be added
     */
    void addDefaultGroup(@NotNull String group);

    /**
     * Removes a default group from the collection of the default groups
     *
     * @param group The name of the group which should be removed
     */
    void removeDefaultGroup(@NotNull String group);

    /**
     * @return A unmodifiable collection of all default groups
     */
    @NotNull
    Collection<PermissionGroup> getDefaultGroups();

    /**
     * Deletes a specific permission group
     *
     * @param name The name of the group which should get deleted
     */
    void deleteGroup(@NotNull String name);

    /**
     * Checks if a specific permission user has the given permission
     *
     * @param permissionUser The user which should get the deep group check
     * @param permission     The permission which should get checked
     * @return If the user has the given permission
     * @see PermissionUser#hasPermission(String)
     */
    boolean hasPermission(
            @NotNull PermissionUser permissionUser,
            @NotNull String permission
    );

    /**
     * Checks if a specific permission group has the given permission
     *
     * @param group      The group which should get permission check including all sub groups and their sub groups etc.
     * @param permission The permission which should get checked
     * @return If the group has the given permission
     */
    boolean hasPermission(
            @NotNull PermissionGroup group,
            @NotNull String permission
    );

    /**
     * Loads a specific permission user or creates a new one
     *
     * @param uuid The uniqueID of the user which should get loaded
     * @return The loaded or created permission user
     */
    @NotNull
    PermissionUser loadUser(@NotNull UUID uuid);

    /**
     * Loads a specific permission user or creates a new one
     *
     * @param uuid The uniqueID of the user which should get loaded
     * @param name The name of the user which should get loaded or {@code null} then the name will not get inserted in the db
     * @return The loaded or created permission user
     */
    @NotNull
    PermissionUser loadUser(@NotNull UUID uuid, @Nullable String name);

    /**
     * Adds a specific permission to a user
     *
     * @param uuid           The uniqueID of the permission user
     * @param permissionNode The permission node which should get added to the user
     */
    void addUserPermission(
            @NotNull UUID uuid,
            @NotNull PermissionNode permissionNode
    );

    /**
     * Removes a specific group from a user
     *
     * @param uuid  The uniqueID of the permission user
     * @param group The group which should get removed
     */
    void removeUserGroup(
            @NotNull UUID uuid,
            @NotNull String group
    );

    /**
     * Adds a specific group to a user
     *
     * @param uuid  The uniqueID of the permission user
     * @param group The group which should get added
     */
    void addUserGroup(
            @NotNull UUID uuid,
            @NotNull NodeGroup group
    );

    /**
     * Updates a specific permission user
     *
     * @param permissionUser The permission user which should get updated
     */
    void updateUser(@NotNull PermissionUser permissionUser);

    /**
     * Deletes a specific user irrevocable out of the database
     *
     * @param uuid The uniqueID of the user which should get deleted
     */
    void deleteUser(@NotNull UUID uuid);

    // ===================================
    // The following methods are not documented because they are for internal use only
    // ===================================

    @ApiStatus.Internal
    void handleDisconnect(UUID uuid);

    @ApiStatus.Internal
    void handleInternalPermissionGroupUpdate(PermissionGroup permissionGroup);

    @ApiStatus.Internal
    void handleInternalPermissionGroupCreate(PermissionGroup permissionGroup);

    @ApiStatus.Internal
    void handleInternalPermissionGroupDelete(PermissionGroup permissionGroup);

    @ApiStatus.Internal
    void handleInternalUserUpdate(PermissionUser permissionUser);

    @ApiStatus.Internal
    void handleInternalUserCreate(PermissionUser permissionUser);

    @ApiStatus.Internal
    void handleInternalUserDelete(PermissionUser permissionUser);

    @ApiStatus.Internal
    void handleInternalDefaultGroupsUpdate();
}
