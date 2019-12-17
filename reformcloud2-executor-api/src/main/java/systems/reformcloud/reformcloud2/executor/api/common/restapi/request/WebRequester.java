package systems.reformcloud.reformcloud2.executor.api.common.restapi.request;

import io.netty.channel.Channel;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public interface WebRequester extends Nameable {

  /**
   * @return The http channel of the web requester
   */
  @Nonnull Channel channel();

  /**
   * @return If the requester is still connected
   */
  boolean isConnected();

  /**
   * Checks if the specified permission is set
   *
   * @param perm The permission which should get checked
   * @return If the permission is set
   */
  @Nonnull PermissionResult hasPermissionValue(@Nonnull String perm);

  /**
   * Checks if the specified permission is set
   *
   * @see #hasPermissionValue(String)
   * @param perm The permission which should get checked
   * @return If the permission is set
   */
  default boolean hasPermission(@Nonnull String perm) {
    return hasPermissionValue(perm).isAllowed();
  }
}
