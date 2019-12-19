package systems.reformcloud.reformcloud2.executor.api.common.commands.permission;

import javax.annotation.Nonnull;

public interface PermissionCheck {

  /**
   * Checks if a user has an specific permission
   *
   * @param permissionHolder The permission holder which should be checked
   * @param permission The permission which should be checked
   * @return The {@link PermissionResult} of the permission check
   */
  @Nonnull
  PermissionResult checkPermission(PermissionHolder permissionHolder,
                                   Permission permission);

  /**
   * Checks if a user has an specific permission
   *
   * @param permissionHolder The permission holder which should be checked
   * @param permission The permission which should be checked
   * @return The {@link PermissionResult} of the permission check
   */
  @Nonnull
  PermissionResult checkPermission(PermissionHolder permissionHolder,
                                   String permission);
}
