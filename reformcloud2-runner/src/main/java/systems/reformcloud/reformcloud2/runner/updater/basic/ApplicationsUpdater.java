package systems.reformcloud.reformcloud2.runner.updater.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.updater.Updater;
import systems.reformcloud.reformcloud2.runner.util.JarFileDirectoryStreamFilter;
import systems.reformcloud.reformcloud2.runner.util.KeyValueHolder;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Represents an updater for the applications
 */
public final class ApplicationsUpdater implements Updater {

    /**
     * The folder in which the installed applications of the cloud versions are located
     */
    private static final Path APP_FOLDER = Paths.get("reformcloud/applications");

    /**
     * Creates a new instance of an applications updater
     *
     * @param applicationUpdatesPath The path where the update files of the update files are located
     */
    public ApplicationsUpdater(@NotNull Path applicationUpdatesPath) {
        this.applicationUpdatesPath = applicationUpdatesPath;
        this.oldToNewUpdates = new ArrayList<>();
    }

    private final Path applicationUpdatesPath;

    private final Collection<Map.Entry<Path, Path>> oldToNewUpdates;

    @Override
    public void collectInformation() {
        if (Files.notExists(this.applicationUpdatesPath)) {
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.applicationUpdatesPath, new JarFileDirectoryStreamFilter())) {
            for (Path entry : stream) {
                Path old = RunnerUtils.findFile(
                        APP_FOLDER,
                        path -> path.getFileName().toString().startsWith(entry.getFileName().toString()),
                        new JarFileDirectoryStreamFilter()
                );
                if (old != null) {
                    this.oldToNewUpdates.add(new KeyValueHolder<>(old, entry));
                }
            }
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean hasNewVersion() {
        return oldToNewUpdates.size() > 0;
    }

    @Override
    public void applyUpdates() {
        for (Map.Entry<Path, Path> oldToNewUpdate : this.oldToNewUpdates) {
            RunnerUtils.deleteFileIfExists(oldToNewUpdate.getKey());

            RunnerUtils.copy(oldToNewUpdate.getValue(), APP_FOLDER);
            RunnerUtils.deleteFileIfExists(oldToNewUpdate.getValue());
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "application";
    }
}
