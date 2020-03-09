package systems.reformcloud.reformcloud2.executor.api.common.dependency.util;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;

public class MavenCentralDependency extends DefaultDependency {

    public MavenCentralDependency(String groupID, String artifactID, String version) {
        super(DefaultRepositories.MAVEN_CENTRAL, groupID, artifactID, version);
    }
}
