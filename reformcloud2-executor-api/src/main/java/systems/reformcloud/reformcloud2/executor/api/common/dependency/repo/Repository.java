package systems.reformcloud.reformcloud2.executor.api.common.dependency.repo;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.Dependency;

import javax.annotation.Nonnull;

/**
 * Represents an dependency remote repository
 *
 * @see Dependency#getRepository()
 */
public interface Repository {

    /**
     * @return The name of the repository
     */
    @Nonnull
    String getName();

    /**
     * @return The url of the repository
     */
    @Nonnull
    String getURL();
}
