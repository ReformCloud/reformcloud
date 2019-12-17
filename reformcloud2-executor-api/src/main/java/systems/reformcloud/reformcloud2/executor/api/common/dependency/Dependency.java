package systems.reformcloud.reformcloud2.executor.api.common.dependency;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.Repository;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * Represents an dependency of the cloud system
 *
 * @see DependencyLoader#loadDependency(Dependency)
 */
public interface Dependency {

    /**
     * @return The repository of the dependency
     */
    @Nonnull
    Repository getRepository();

    /**
     * @return The groupID of the dependency
     */
    @Nonnull
    String getGroupID();

    /**
     * @return The artifactID of the dependency
     */
    @Nonnull
    String getArtifactID();

    /**
     * @return The version of the dependency
     */
    @Nonnull
    String getVersion();

    /**
     * @return The path where the dependency is
     *  a) saved
     *  b) should be saved to
     */
    @Nonnull
    Path getPath();

    /**
     * Prepares the dependency if an update is available
     */
    void prepareIfUpdate();

    /**
     * Downloads the dependency
     */
    void download();
}
