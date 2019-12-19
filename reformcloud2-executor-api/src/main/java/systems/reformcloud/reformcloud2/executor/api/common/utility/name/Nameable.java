package systems.reformcloud.reformcloud2.executor.api.common.utility.name;

import javax.annotation.Nonnull;

/**
 * Represents every object which can have a special name
 *
 * @see ReNameable
 */
public interface Nameable {

  /**
   * @return The name of the current instance
   */
  @Nonnull String getName();
}
