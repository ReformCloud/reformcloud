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
package systems.reformcloud.reformcloud2.node.template;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.VersionInstaller;
import systems.reformcloud.reformcloud2.executor.api.utility.MoreCollections;
import systems.reformcloud.reformcloud2.node.template.installers.DownloadingVersionInstaller;
import systems.reformcloud.reformcloud2.node.template.installers.SpongeVersionInstaller;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class VersionInstallerRegistry {

  private static final Map<String, VersionInstaller> INSTALLERS = new ConcurrentHashMap<>();

  private VersionInstallerRegistry() {
    throw new UnsupportedOperationException();
  }

  public static void registerInstaller(@NotNull VersionInstaller installer) {
    INSTALLERS.putIfAbsent(installer.getName().toLowerCase(Locale.ROOT), installer);
  }

  public static Optional<VersionInstaller> getInstaller(@NotNull String name) {
    return MoreCollections.findFirst(INSTALLERS, n -> n.equalsIgnoreCase(name));
  }

  public static void unregister(@NotNull String name) {
    INSTALLERS.remove(name.toLowerCase(Locale.ROOT));
  }

  public static void registerDefaults() {
    registerInstaller(new SpongeVersionInstaller());
    registerInstaller(new DownloadingVersionInstaller());
  }
}
