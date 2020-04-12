package systems.reformcloud.reformcloud2.executor.api.common.dependency;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.Repository;

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
    @NotNull
    Repository getRepository();

    /**
     * @return The groupID of the dependency
     */
    @NotNull
    String getGroupID();

    /**
     * @return The artifactID of the dependency
     */
    @NotNull
    String getArtifactID();

    /**
     * @return The version of the dependency
     */
    @NotNull
    String getVersion();

    /**
     * @return The path where the dependency is
     * a) saved
     * b) should be saved to
     */
    @NotNull
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
