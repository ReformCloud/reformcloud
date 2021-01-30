/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.runner.updater.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.runner.updater.Updater;
import systems.reformcloud.runner.util.JarFileDirectoryStreamFilter;
import systems.reformcloud.runner.util.KeyValueHolder;
import systems.reformcloud.runner.util.RunnerUtils;

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
  private static final Path APP_FOLDER = Paths.get(System.getProperty("systems.reformcloud.application-directory", "reformcloud/applications"));

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
    if (Files.exists(this.applicationUpdatesPath)) {
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.applicationUpdatesPath, new JarFileDirectoryStreamFilter())) {
        for (Path entry : stream) {
          final String fileName = entry.getFileName().toString();
          final Path oldFile = RunnerUtils.findFile(APP_FOLDER, path -> path.getFileName().toString().equals(fileName), new JarFileDirectoryStreamFilter());

          this.oldToNewUpdates.add(new KeyValueHolder<>(oldFile, entry));
        }
      } catch (final IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  @Override
  public boolean hasNewVersion() {
    return !this.oldToNewUpdates.isEmpty();
  }

  @Override
  public void applyUpdates() {
    for (Map.Entry<Path, Path> oldToNewUpdate : this.oldToNewUpdates) {
      if (oldToNewUpdate.getKey() != null) {
        RunnerUtils.deleteFileIfExists(oldToNewUpdate.getKey());
      }

      RunnerUtils.copy(oldToNewUpdate.getValue(), APP_FOLDER.resolve(oldToNewUpdate.getValue().getFileName()));
      RunnerUtils.deleteFileIfExists(oldToNewUpdate.getValue());
    }
  }

  @NotNull
  @Override
  public String getName() {
    return "application";
  }
}
