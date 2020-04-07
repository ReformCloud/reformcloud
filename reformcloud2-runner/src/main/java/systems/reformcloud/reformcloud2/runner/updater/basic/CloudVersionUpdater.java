package systems.reformcloud.reformcloud2.runner.updater.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.updater.Updater;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;

public final class CloudVersionUpdater implements Updater {

    /**
     * Creates a new cloud version updater instance
     *
     * @param globalReformScriptFile The location of the reform script
     */
    public CloudVersionUpdater(@NotNull File globalReformScriptFile) {
        this.globalReformScriptFile = globalReformScriptFile;
    }

    private final File globalReformScriptFile;

    private boolean versionAvailable = false;

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
