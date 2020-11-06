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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an updater for the applications
 */
public final class ApplicationsUpdater implements Updater {

    /**
     * The folder in which the installed applications of the cloud versions are located
     */
    private static final Path APP_FOLDER = Paths.get("reformcloud/applications");

    /**
     * The pattern to find a file update
     */
    private static final Pattern PATTERN = Pattern.compile("(.*)-(.*)\\.jar");
    private final Path applicationUpdatesPath;
    private final Collection<Map.Entry<Path, Path>> oldToNewUpdates;

    /**
     * Creates a new instance of an applications updater
     *
     * @param applicationUpdatesPath The path where the update files of the update files are located
     */
    public ApplicationsUpdater(@NotNull Path applicationUpdatesPath) {
        this.applicationUpdatesPath = applicationUpdatesPath;
        this.oldToNewUpdates = new ArrayList<>();
    }

    @Override
    public void collectInformation() {
        if (Files.notExists(this.applicationUpdatesPath)) {
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.applicationUpdatesPath, new JarFileDirectoryStreamFilter())) {
            for (Path entry : stream) {
                Matcher matcher = PATTERN.matcher(entry.getFileName().toString());
                if (!matcher.find()) {
                    System.err.println("Unable to find match for \"file-name-version.jar\" " +
                        "is the requested format: " + entry.toString());
                    continue;
                }

                String oldName = matcher.group(1);
                Path old = RunnerUtils.findFile(
                    APP_FOLDER,
                    path -> path.getFileName().toString().startsWith(oldName),
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
        return !this.oldToNewUpdates.isEmpty();
    }

    @Override
    public void applyUpdates() {
        for (Map.Entry<Path, Path> oldToNewUpdate : this.oldToNewUpdates) {
            RunnerUtils.deleteFileIfExists(oldToNewUpdate.getKey());

            RunnerUtils.copy(oldToNewUpdate.getValue(), Paths.get(
                APP_FOLDER + "/" + oldToNewUpdate.getValue().getFileName()
            ));
            RunnerUtils.deleteFileIfExists(oldToNewUpdate.getValue());
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "application";
    }
}
