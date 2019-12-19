package systems.reformcloud.reformcloud2.executor.api.common.patch;

import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public interface PatchNote extends Nameable {

  /**
   * @return The new version string after the patch
   */
  @Nonnull String newVersion();

  /**
   * @return The update message of the patch
   */
  @Nonnull String updateMessage();
}
