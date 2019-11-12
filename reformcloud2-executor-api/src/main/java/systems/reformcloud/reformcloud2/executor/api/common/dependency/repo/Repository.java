package systems.reformcloud.reformcloud2.executor.api.common.dependency.repo;

import javax.annotation.Nonnull;

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
