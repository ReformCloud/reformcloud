/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

    private final Repository repository;
    private final String groupID;
    private final String artifactID;
    private final String version;
    private String url;

    public DefaultDependency(Repository repository, String groupID, String artifactID, Properties properties) {
        this(repository, groupID, artifactID, properties.getProperty(artifactID));
    }

    public DefaultDependency(Repository repository, String groupID, String artifactID, String version) {
        this.repository = repository;
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.version = version;
    }

    @NotNull
    @Override
    public Repository getRepository() {
        return this.repository;
    }

    @NotNull
    @Override
    public String getGroupID() {
        return this.groupID;
    }

    @NotNull
    @Override
    public String getArtifactID() {
        return this.artifactID;
    }

    @NotNull
    @Override
    public String getVersion() {
        return this.version;
    }

    @NotNull
    @Override
    public Path getPath() {
        return Paths.get("reformcloud/.bin/libs/" + this.getArtifactID() + "-" + this.getVersion() + ".jar");
    }

    @Override
    public void prepareIfUpdate() {
        File[] files = new File("reformcloud/.bin/libs/").listFiles(pathname -> pathname.getName().startsWith(this.getArtifactID()) && pathname.getName().endsWith(".jar"));
        if (files == null || files.length == 0) {
            return;
        }

        if (files.length > 1) {
            for (File file : files) {
                String version = file.getName()
                        .replaceFirst(this.getArtifactID(), "") // Replace the name of the dependency (netty-all-4.1.42.Final.jar -> -4.1.42.Final.jar)
                        .replaceFirst("-", "") // Replaces the first name (-4.1.42.Final.jar -> 4.1.42.Final.jar)
                        .replace(".jar", ""); // Replaces the .jar (4.1.42.Final.jar -> 4.1.42.Final)
                if (!version.equals(this.getVersion())) {
                    SystemHelper.deleteFile(file);
                }
            }
        } else {
            File dependency = files[0];
            String version = dependency.getName()
                    .replaceFirst(this.getArtifactID(), "") // Replace the name of the dependency (netty-all-4.1.42.Final.jar -> -4.1.42.Final.jar)
                    .replaceFirst("-", "") // Replaces the first name (-4.1.42.Final.jar -> 4.1.42.Final.jar)
                    .replace(".jar", ""); // Replaces the .jar (4.1.42.Final.jar -> 4.1.42.Final)
            if (!version.equals(this.getVersion())) {
                SystemHelper.deleteFile(dependency);
            }
        }
    }

    @Override
    public void download() {
        if (this.url == null) {
            this.recalculateDownloadURL();
        }

        DownloadHelper.downloadAndDisconnect(this.url, "reformcloud/.bin/libs/" + this.getArtifactID() + "-" + this.getVersion() + ".jar");
    }

    private void recalculateDownloadURL() {
        this.url = this.repository.getURL() + this.getGroupID().replace(".", "/")
                + "/" + this.getArtifactID() + "/" + this.getVersion() + "/" + this.getArtifactID() + "-" + this.getVersion() + ".jar";
    }
}
