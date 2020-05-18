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
package systems.reformcloud.reformcloud2.runner.updater.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.updater.Updater;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;

public final class CloudVersionUpdater implements Updater {

    private final File globalReformScriptFile;
    private boolean versionAvailable = false;

    /**
     * Creates a new cloud version updater instance
     *
     * @param globalReformScriptFile The location of the reform script
     */
    public CloudVersionUpdater(@NotNull File globalReformScriptFile) {
        this.globalReformScriptFile = globalReformScriptFile;
    }

    @Override
    public void collectInformation() {
        String currentVersion = System.getProperty("reformcloud.runner.version");
        if (currentVersion == null) {
            currentVersion = CloudVersionUpdater.class.getPackage().getImplementationVersion();
            this.rewriteGlobalFile(currentVersion);
        }

        Properties properties = RunnerUtils.loadProperties(
                System.getProperty("reformcloud.version.url", "https://internal.reformcloud.systems/version.properties")
        );
        if (properties.containsKey("version")) {
            versionAvailable = !properties.getProperty("version").equals(currentVersion);
        }
    }

    @Override
    public boolean hasNewVersion() {
        return this.versionAvailable;
    }

    @Override
    public void applyUpdates() {
        RunnerUtils.downloadFile("https://internal.reformcloud.systems/executor.jar", RunnerUtils.EXECUTOR_PATH);

        if (Files.exists(RunnerUtils.RUNNER_FILES_FILE)) {
            RunnerUtils.downloadFile("https://internal.reformcloud.systems/runner.jar", RunnerUtils.RUNNER_FILES_FILE);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "cloud";
    }

    private void rewriteGlobalFile(@NotNull String currentVersion) {
        System.setProperty("reformcloud.runner.version", currentVersion);

        RunnerUtils.rewriteFile(this.globalReformScriptFile.toPath(), s -> {
            if (s.startsWith("# VARIABLE reformcloud.runner.version=") || s.startsWith("VARIABLE reformcloud.runner.version=")) {
                s = "VARIABLE reformcloud.runner.version=" + currentVersion;
            }

            return s;
        });
    }
}
