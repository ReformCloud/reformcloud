package de.klaro.reformcloud2.executor.api.common.dependency;

import de.klaro.reformcloud2.executor.api.common.dependency.repo.Repository;
import de.klaro.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import de.klaro.reformcloud2.executor.api.common.utility.system.SystemHelper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class DefaultDependency implements Dependency {

    public DefaultDependency(Repository repository, String groupID, String artifactID, Properties properties) {
        this.repository = repository;
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.version = properties.getProperty(artifactID);
    }

    private final Repository repository;

    private final String groupID;

    private final String artifactID;

    private final String version;

    private String url;

    @Override
    public Repository getRepository() {
        return repository;
    }

    @Override
    public String getGroupID() {
        return groupID;
    }

    @Override
    public String getArtifactID() {
        return artifactID;
    }

    @Override
    public String getVersion() {
        return version;
    }

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
                SystemHelper.deleteFile(file);
            }
        } else {
            File dependency = files[0];
            String[] version = dependency.getName().split("-");
            if (!version[version.length -1].equals(getVersion())) {
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
