/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.executor.api.dependency;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.dependency.repo.Repository;
import systems.reformcloud.reformcloud2.executor.api.io.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class DefaultDependency implements Dependency {

    private static final Path LIB_ROOT_DIR = Paths.get("reformcloud/.bin/libs");
    private static final String DOWNLOAD_URL_TEMPLATE = "%s%s/%s/%s/%s-%s.jar";

    private final Repository repository;
    private final String groupID;
    private final String artifactID;
    private final String version;
    private String downloadUrl;

    @Deprecated
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
        return LIB_ROOT_DIR.resolve(this.getArtifactID() + "-" + this.getVersion() + ".jar");
    }

    @Override
    public void prepareIfUpdate() {
        File[] files = LIB_ROOT_DIR.toFile().listFiles(file -> file.getName().startsWith(this.artifactID)
            && !file.getName().replace(this.artifactID + "-", "").contains("-")
            && file.getName().endsWith(".jar"));
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
                    IOUtils.deleteFile(file);
                }
            }
        } else {
            File dependency = files[0];
            String version = dependency.getName()
                .replaceFirst(this.getArtifactID(), "") // Replace the name of the dependency (netty-all-4.1.42.Final.jar -> -4.1.42.Final.jar)
                .replaceFirst("-", "") // Replaces the first name (-4.1.42.Final.jar -> 4.1.42.Final.jar)
                .replace(".jar", ""); // Replaces the .jar (4.1.42.Final.jar -> 4.1.42.Final)
            if (!version.equals(this.getVersion())) {
                IOUtils.deleteFile(dependency);
            }
        }
    }

    @Override
    public void download() {
        if (this.downloadUrl == null) {
            this.downloadUrl = String.format(
                DefaultDependency.DOWNLOAD_URL_TEMPLATE,
                this.repository.getURL(), this.groupID.replace(".", "/"), this.artifactID, this.version, this.artifactID, this.version
            );
        }

        DownloadHelper.downloadAndDisconnect(this.downloadUrl, this.getPath().toString());
    }
}
