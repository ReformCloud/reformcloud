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
package systems.reformcloud.node.template.installers;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.group.template.version.Version;
import systems.reformcloud.group.template.version.VersionInstaller;
import systems.reformcloud.shared.io.DownloadHelper;
import systems.reformcloud.shared.io.IOUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class SpongeVersionInstaller implements VersionInstaller {

  @Override
  public boolean installVersion(@NotNull Version version) {
    final Path targetPath = Paths.get("reformcloud/files", version.getName().toLowerCase(Locale.ROOT));
    if (Files.notExists(targetPath)) {
      final Path tempDownloadPath = Paths.get("reformcloud/files", "temp_" + version.getName().toLowerCase(Locale.ROOT));

      DownloadHelper.download(version.getDownloadUrl(), tempDownloadPath);
      IOUtils.unzip(tempDownloadPath, targetPath);
      IOUtils.deleteFile(tempDownloadPath);
    }
    return true;
  }

  @Override
  public String getName() {
    return VersionInstaller.SPONGE;
  }
}
