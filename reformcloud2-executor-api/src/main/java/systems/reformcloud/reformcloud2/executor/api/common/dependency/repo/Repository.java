package systems.reformcloud.reformcloud2.executor.api.common.dependency.repo;

import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.Dependency;

/**
 * Represents an dependency remote repository
 *
 * @see Dependency#getRepository()
 */
public interface Repository {

  /**
   * @return The name of the repository
   */
  @Nonnull String getName();

  /**
   * @return The url of the repository
   */
  @Nonnull String getURL();
}
