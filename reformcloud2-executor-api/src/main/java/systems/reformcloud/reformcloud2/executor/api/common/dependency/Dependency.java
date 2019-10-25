package systems.reformcloud.reformcloud2.executor.api.common.dependency;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.Repository;

import java.nio.file.Path;

public interface Dependency {

    /**
     * @return The repository of the dependency
     */
    Repository getRepository();

    /**
     * @return The groupID of the dependency
     */
    String getGroupID();

    /**
     * @return The artifactID of the dependency
     */
    String getArtifactID();

    /**
     * @return The version of the dependency
     */
    String getVersion();

    /**
     * @return The path where the dependency is
     *  a) saved
     *  b) should be saved to
     */
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
