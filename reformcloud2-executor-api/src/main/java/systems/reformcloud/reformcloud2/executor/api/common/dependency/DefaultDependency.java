package systems.reformcloud.reformcloud2.executor.api.common.dependency;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.Repository;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class DefaultDependency implements Dependency {

    public DefaultDependency(Repository repository, String groupID, String artifactID, Properties properties) {
        this(repository, groupID, artifactID, properties.getProperty(artifactID));
    }

    public DefaultDependency(Repository repository, String groupID, String artifactID, String version) {
        this.repository = repository;
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.version = version;
    }

    private final Repository repository;

    private final String groupID;

    private final String artifactID;

    private final String version;

    private String url;

    @NotNull
    @Override
    public Repository getRepository() {
        return repository;
    }

    @NotNull
    @Override
    public String getGroupID() {
        return groupID;
    }

    @NotNull
    @Override
    public String getArtifactID() {
        return artifactID;
    }

    @NotNull
    @Override
    public String getVersion() {
        return version;
    }

    @NotNull
    @Override
    public Path getPath() {
        return Paths.get("reformcloud/.bin/libs/" + getArtifactID() + "-" + getVersion() + ".jar");
    }

    @Override
    public void prepareIfUpdate() {
        File[] files = new File("reformcloud/.bin/libs/").listFiles(pathname -> pathname.getName().startsWith(getArtifactID()) && pathname.getName().endsWith(".jar"));
        if (files == null || files.length == 0) {
            return;
        }

        if (files.length > 1) {
            for (File file : files) {
                String version = file.getName()
                        .replaceFirst(getArtifactID(), "") // Replace the name of the dependency (netty-all-4.1.42.Final.jar -> -4.1.42.Final.jar)
                        .replaceFirst("-", "") // Replaces the first name (-4.1.42.Final.jar -> 4.1.42.Final.jar)
                        .replace(".jar", ""); // Replaces the .jar (4.1.42.Final.jar -> 4.1.42.Final)
                if (!version.equals(getVersion())) {
                    SystemHelper.deleteFile(file);
                }
            }
        } else {
            File dependency = files[0];
            String version = dependency.getName()
                    .replaceFirst(getArtifactID(), "") // Replace the name of the dependency (netty-all-4.1.42.Final.jar -> -4.1.42.Final.jar)
                    .replaceFirst("-", "") // Replaces the first name (-4.1.42.Final.jar -> 4.1.42.Final.jar)
                    .replace(".jar", ""); // Replaces the .jar (4.1.42.Final.jar -> 4.1.42.Final)
            if (!version.equals(getVersion())) {
                SystemHelper.deleteFile(dependency);
            }
        }
    }

    @Override
    public void download() {
        if (url == null) {
            recalculateDownloadURL();
        }

        DownloadHelper.downloadAndDisconnect(url, "reformcloud/.bin/libs/" + getArtifactID() + "-" + getVersion() + ".jar");
    }

    private void recalculateDownloadURL() {
        this.url = repository.getURL() + getGroupID().replace(".", "/")
                + "/" + getArtifactID() + "/" + getVersion() + "/" + getArtifactID() + "-" + getVersion() + ".jar";
    }
}
