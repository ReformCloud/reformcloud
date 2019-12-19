package systems.reformcloud.reformcloud2.permissions.util;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import systems.reformcloud.reformcloud2.permissions.util.group.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

public interface PermissionUtil {

  /**
   * Get a specific group
   *
   * @param name The name of the group
   * @return The {@link PermissionGroup} or {@code null} if the group does not
   *     exists
   */
  @Nullable PermissionGroup getGroup(@Nonnull String name);

  /**
   * Updates a specific permission group
   *
   * @param permissionGroup The permission group which should be updated
   */
  void updateGroup(@Nonnull PermissionGroup permissionGroup);

  /**
   * Adds a specific permission to a permission group
   *
   * @param permissionGroup The group which should get the permission
   * @param permissionNode The permission which should be added
   */
  void addGroupPermission(@Nonnull PermissionGroup permissionGroup,
                          @Nonnull PermissionNode permissionNode);

  /**
   * Adds a specific permission to a permission group on a specific process
   * group
   *
   * @param processGroup The process group on which the permission should be
   *     valid
   * @param permissionGroup The group which should get the permission
   * @param permissionNode The permission which should be added
   */
  void addProcessGroupPermission(@Nonnull String processGroup,
                                 @Nonnull PermissionGroup permissionGroup,
                                 @Nonnull PermissionNode permissionNode);

  /**
   * Creates a new group
   *
   * @param name The name of the new group
   * @return The created group or the group which already exists
   */
  @Nonnull PermissionGroup createGroup(@Nonnull String name);

  /**
   * Adds a new default group to the collection of all default groups
   *
   * @param group The name of the group which should be added
   */
  void addDefaultGroup(@Nonnull String group);

  /**
   * Removes a default group from the collection of the default groups
   *
   * @param group The name of the group which should be removed
   */
  void removeDefaultGroup(@Nonnull String group);

  /**
   * @return A unmodifiable collection of all default groups
   */
  @Nonnull Collection<PermissionGroup> getDefaultGroups();

  /**
   * Deletes a specific permission group
   *
   * @param name The name of the group which should get deleted
   */
  void deleteGroup(@Nonnull String name);

  /**
   * Checks if a specific permission user has the given permission
   *
   * @see PermissionUser#hasPermission(String)
   *
   * @param permissionUser The user which should get the deep group check
   * @param permission The permission which should get checked
   * @return If the user has the given permission
   */
  boolean hasPermission(@Nonnull PermissionUser permissionUser,
                        @Nonnull String permission);

  /**
   * Checks if a specific permission group has the given permission
   *
   * @param group The group which should get permission check including all sub
   *     groups and their sub groups etc.
   * @param permission The permission which should get checked
   * @return If the group has the given permission
   */
  boolean hasPermission(@Nonnull PermissionGroup group,
                        @Nonnull String permission);

  /**
   * Loads a specific permission user or creates a new one
   *
   * @param uuid The uniqueID of the user which should get loaded
   * @return The loaded or created permission user
   */
  @Nonnull PermissionUser loadUser(@Nonnull UUID uuid);

  /**
   * Loads a specific permission user or creates a new one
   *
   * @param uuid The uniqueID of the user which should get loaded
   * @param name The name of the user which should get loaded or {@code null}
   *     then the name will not get inserted in the db
   * @return The loaded or created permission user
   */
  @Nonnull PermissionUser loadUser(@Nonnull UUID uuid, @Nullable String name);

  /**
   * Adds a specific permission to a user
   *
   * @param uuid The uniqueID of the permission user
   * @param permissionNode The permission node which should get added to the
   *     user
   */
  void addUserPermission(@Nonnull UUID uuid,
                         @Nonnull PermissionNode permissionNode);

  /**
   * Removes a specific group from a user
   *
   * @param uuid The uniqueID of the permission user
   * @param group The group which should get removed
   */
  void removeUserGroup(@Nonnull UUID uuid, @Nonnull String group);

  /**
   * Adds a specific group to a user
   *
   * @param uuid The uniqueID of the permission user
   * @param group The group which should get added
   */
  void addUserGroup(@Nonnull UUID uuid, @Nonnull NodeGroup group);

  /**
   * Updates a specific permission user
   *
   * @param permissionUser The permission user which should get updated
   */
  void updateUser(@Nonnull PermissionUser permissionUser);

  /**
   * Deletes a specific user irrevocable out of the database
   *
   * @param uuid The uniqueID of the user which should get deleted
   */
  void deleteUser(@Nonnull UUID uuid);

  // ===================================
  // The following methods are not documented because they are for internal use
  // only
  // ===================================

  void handleDisconnect(UUID uuid);

  void handleInternalPermissionGroupUpdate(PermissionGroup permissionGroup);

  void handleInternalPermissionGroupCreate(PermissionGroup permissionGroup);

  void handleInternalPermissionGroupDelete(PermissionGroup permissionGroup);

  void handleInternalUserUpdate(PermissionUser permissionUser);

  void handleInternalUserCreate(PermissionUser permissionUser);

  void handleInternalUserDelete(PermissionUser permissionUser);

  void handleInternalDefaultGroupsUpdate();
}
