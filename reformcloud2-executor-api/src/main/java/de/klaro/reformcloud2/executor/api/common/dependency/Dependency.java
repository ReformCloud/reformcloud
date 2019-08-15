package de.klaro.reformcloud2.executor.api.common.dependency;

import de.klaro.reformcloud2.executor.api.common.dependency.repo.Repository;

import java.nio.file.Path;

public interface Dependency {

    Repository getRepository();

    String getGroupID();

    String getArtifactID();

    String getVersion();

    Path getPath();

    void prepareIfUpdate();

    void download();
}
