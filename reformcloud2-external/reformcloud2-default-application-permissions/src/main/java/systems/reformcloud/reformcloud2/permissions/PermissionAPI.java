package systems.reformcloud.reformcloud2.permissions;

import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.permissions.util.PermissionUtil;
import systems.reformcloud.reformcloud2.permissions.util.basic.DefaultPermissionUtil;

public final class PermissionAPI {

  private static PermissionAPI instance;

  private final PermissionUtil permissionUtil;

  // ====

  private PermissionAPI() {
    this.permissionUtil = DefaultPermissionUtil.hello();
  }

  @Nonnull
  public PermissionUtil getPermissionUtil() {
    return permissionUtil;
  }

  @Nonnull
  public static PermissionAPI getInstance() {
    return instance;
  }

  public static void handshake() {
    if (instance != null) {
      return;
    }

    instance = new PermissionAPI();
  }
}
