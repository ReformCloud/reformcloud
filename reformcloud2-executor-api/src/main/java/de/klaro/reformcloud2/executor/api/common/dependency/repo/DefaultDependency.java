package de.klaro.reformcloud2.executor.api.common.dependency.repo;

import de.klaro.reformcloud2.executor.api.common.dependency.Dependency;
import de.klaro.reformcloud2.executor.api.common.utility.DownloadHelper;
import de.klaro.reformcloud2.executor.api.common.utility.SystemHelper;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Pattern;

public final class DefaultDependency implements Dependency {

    private static final Pattern PATTERN = Pattern.compile("(.*)-(.*).jar");

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
        File[] files = new File("reformcloud/.bin/libs/").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith(getArtifactID()) && pathname.getName().endsWith(".jar");
            }
        });
        if (files == null || files.length == 0) {
            return;
        }

        if (files.length > 1) {
            for (File file : files) {
                SystemHelper.deleteFile(file);
            }
        } else {
            File dependency = files[0];
            String version = PATTERN.matcher(dependency.getName()).group(2);
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
