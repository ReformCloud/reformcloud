/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.permissions;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.permissions.defaults.DefaultPermissionManagement;
import systems.reformcloud.reformcloud2.permissions.events.system.PermissionManagerSetupEvent;
import systems.reformcloud.reformcloud2.permissions.nodes.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.nodes.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public abstract class PermissionManagement {

  private static PermissionManagement instance;

  @ApiStatus.Internal
  public static void setup() {
    Conditions.isTrue(PermissionManagement.instance == null, "Cannot redefine singleton permission management instance");

    PermissionManagerSetupEvent permissionManagerSetupEvent = new PermissionManagerSetupEvent(new DefaultPermissionManagement());
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(permissionManagerSetupEvent);

    PermissionManagement.instance = permissionManagerSetupEvent.getPermissionManagement();
  }

  @NotNull
  public static PermissionManagement getInstance() {
    return Objects.requireNonNull(instance, "Permission management instance is not set yet");
  }

  /**
   * Get a specific group
   *
   * @param name The name of the group
   * @return The {@link PermissionGroup} or {@code null} if the group does not exists
   * @deprecated Use {@link #getPermissionGroup(String)} instead
   */
  @Nullable
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.10.2")
  public PermissionGroup getGroup(@NotNull String name) {
    return this.getPermissionGroup(name).orElse(null);
  }

  /**
   * Get a specific permission group by it's name
   *
   * @param name The name of the group
   * @return An optional holding the requested permission group or empty if the group does not exists
   */
  @NotNull
  public abstract Optional<PermissionGroup> getPermissionGroup(@NotNull String name);

  /**
   * Updates a specific permission group
   *
   * @param permissionGroup The permission group which should be updated
   */
  public abstract void updateGroup(@NotNull PermissionGroup permissionGroup);

  /**
   * Adds a specific permission to a permission group
   *
   * @param permissionGroup The group which should get the permission
   * @param permissionNode  The permission which should be added
   */
  public abstract void addGroupPermission(@NotNull PermissionGroup permissionGroup, @NotNull PermissionNode permissionNode);

  /**
   * Adds a specific permission to a permission group on a specific process group
   *
   * @param processGroup    The process group on which the permission should be valid
   * @param permissionGroup The group which should get the permission
   * @param permissionNode  The permission which should be added
   */
  public abstract void addProcessGroupPermission(@NotNull String processGroup, @NotNull PermissionGroup permissionGroup, @NotNull PermissionNode permissionNode);

  /**
   * Creates a new group
   *
   * @param name The name of the new group
   * @return The created group or the group which already exists
   * @deprecated Use {@link #createPermissionGroup(PermissionGroup)} instead
   */
  @NotNull
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "2.10.2")
  public abstract PermissionGroup createGroup(@NotNull String name);

  /**
   * Creates a new permission group
   *
   * @param permissionGroup The permission group which should get created
   * @return The created group or the group which already exists
   */
  @NotNull
  public abstract PermissionGroup createPermissionGroup(@NotNull PermissionGroup permissionGroup);

  /**
   * @return A unmodifiable collection of all default groups
   */
  @NotNull
  public abstract Collection<PermissionGroup> getDefaultGroups();

  /**
   * @return All existing permission groups
   */
  @NotNull
  @UnmodifiableView
  public abstract Collection<PermissionGroup> getPermissionGroups();

  /**
   * Deletes a specific permission group
   *
   * @param name The name of the group which should get deleted
   */
  public abstract void deleteGroup(@NotNull String name);

  /**
   * Checks if a specific permission user has the given permission
   *
   * @param permissionUser The user which should get the deep group check
   * @param permission     The permission which should get checked
   * @return If the user has the given permission
   * @see PermissionUser#hasPermission(String)
   */
  public abstract boolean hasPermission(@NotNull PermissionUser permissionUser, @NotNull String permission);

  /**
   * Checks if a specific permission group has the given permission
   *
   * @param group      The group which should get permission check including all sub groups and their sub groups etc.
   * @param permission The permission which should get checked
   * @return If the group has the given permission
   */
  public abstract boolean hasPermission(@NotNull PermissionGroup group, @NotNull String permission);

  /**
   * Loads a specific permission user or creates a new one
   *
   * @param uniqueId The uniqueID of the user which should get loaded
   * @return The loaded or created permission user
   */
  @NotNull
  public abstract PermissionUser loadUser(@NotNull UUID uniqueId);

  /**
   * Loads a specific permission user which already exists by it's name
   *
   * @param name The name of the permission user
   * @return The loaded permission user
   */
  @NotNull
  public abstract Optional<PermissionUser> getFirstExistingUser(@NotNull String name);

  /**
   * Loads a specific permission user
   *
   * @param name The name of the permission user
   * @return The loaded or newly created permission user
   */
  @NotNull
  public abstract Optional<PermissionUser> loadUser(@NotNull String name);

  /**
   * Loads a specific permission user
   *
   * @param uniqueId The uniqueID of the user which should get loaded
   * @return The loaded permission user
   */
  @NotNull
  public abstract Optional<PermissionUser> getExistingUser(@NotNull UUID uniqueId);

  /**
   * Loads a specific permission user or creates a new one
   *
   * @param uniqueId The uniqueID of the user which should get loaded
   * @param name     The name of the user which should get loaded or {@code null} then the name will not get inserted in the db
   * @return The loaded or created permission user
   */
  @NotNull
  public abstract PermissionUser loadUser(@NotNull UUID uniqueId, @Nullable String name);

  /**
   * Assigns all default groups to the permission user
   *
   * @param uniqueId The unique if of the user which should receive all default groups
   */
  public abstract void assignDefaultGroups(@NotNull UUID uniqueId);

  /**
   * Assigns all default groups to the permission user
   *
   * @param permissionUser The user which should receive all default groups
   */
  public abstract void assignDefaultGroups(@NotNull PermissionUser permissionUser);

  /**
   * Adds a specific permission to a user
   *
   * @param uniqueId       The uniqueID of the permission user
   * @param permissionNode The permission node which should get added to the user
   */
  public abstract void addUserPermission(@NotNull UUID uniqueId, @NotNull PermissionNode permissionNode);

  /**
   * Removes a specific group from a user
   *
   * @param uniqueId The uniqueID of the permission user
   * @param group    The group which should get removed
   */
  public abstract void removeUserGroup(@NotNull UUID uniqueId, @NotNull String group);

  /**
   * Adds a specific group to a user
   *
   * @param uniqueId The uniqueID of the permission user
   * @param group    The group which should get added
   */
  public abstract void addUserGroup(@NotNull UUID uniqueId, @NotNull NodeGroup group);

  /**
   * Updates a specific permission user
   *
   * @param permissionUser The permission user which should get updated
   */
  public abstract void updateUser(@NotNull PermissionUser permissionUser);

  /**
   * Deletes a specific user irrevocable out of the database
   *
   * @param uniqueId The uniqueID of the user which should get deleted
   */
  public abstract void deleteUser(@NotNull UUID uniqueId);

  // ===================================
  // The following methods are not documented because they are for internal use only
  // ===================================

  @ApiStatus.Internal
  public abstract void handleDisconnect(UUID uniqueId);

  @ApiStatus.Internal
  public abstract void handleInternalPermissionGroupUpdate(PermissionGroup permissionGroup);

  @ApiStatus.Internal
  public abstract void handleInternalPermissionGroupCreate(PermissionGroup permissionGroup);

  @ApiStatus.Internal
  public abstract void handleInternalPermissionGroupDelete(PermissionGroup permissionGroup);

  @ApiStatus.Internal
  public abstract void handleInternalUserUpdate(PermissionUser permissionUser);

  @ApiStatus.Internal
  public abstract void handleInternalUserCreate(PermissionUser permissionUser);

  @ApiStatus.Internal
  public abstract void handleInternalUserDelete(PermissionUser permissionUser);
}
