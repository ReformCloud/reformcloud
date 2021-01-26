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
package systems.reformcloud.reformcloud2.node.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.group.main.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.group.process.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.group.process.player.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.startup.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.setup.GroupSetupVersion;
import systems.reformcloud.reformcloud2.executor.api.group.template.Template;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.Version;
import systems.reformcloud.reformcloud2.shared.group.DefaultMainGroup;
import systems.reformcloud.reformcloud2.shared.group.DefaultProcessGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.function.Consumer;

final class VersionedGroupSetupVersion implements GroupSetupVersion {

  private final String mainGroupName;
  private final String processGroupName;

  private final Version version;

  public VersionedGroupSetupVersion(String mainGroupName, String processGroupName, Version version) {
    this.mainGroupName = mainGroupName;
    this.processGroupName = processGroupName;
    this.version = version;
  }

  @Override
  public void install(@NotNull Consumer<ProcessGroup> processGroupInstaller, @NotNull Consumer<MainGroup> mainGroupInstaller) {
    processGroupInstaller.accept(this.createProcessGroup());
    mainGroupInstaller.accept(new DefaultMainGroup(new ArrayList<>(Collections.singleton(this.processGroupName)), this.mainGroupName));
  }

  private ProcessGroup createProcessGroup() {
    return new DefaultProcessGroup(
      StartupConfiguration.newDefaultConfiguration(),
      PlayerAccessConfiguration.disabled(),
      true,
      false,
      this.version.getVersionType().isServer(),
      new ArrayList<>(Collections.singletonList(Template.builder("default", this.version).build())),
      this.processGroupName
    );
  }

  @Override
  public String getName() {
    return this.version.getName().toLowerCase(Locale.ROOT).replace('_', ' ');
  }
}
