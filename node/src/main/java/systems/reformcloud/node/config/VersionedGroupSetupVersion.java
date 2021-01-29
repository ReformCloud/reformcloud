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
package systems.reformcloud.node.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.group.main.MainGroup;
import systems.reformcloud.group.process.ProcessGroup;
import systems.reformcloud.group.process.player.PlayerAccessConfiguration;
import systems.reformcloud.group.process.startup.StartupConfiguration;
import systems.reformcloud.group.setup.GroupSetupVersion;
import systems.reformcloud.group.template.Template;
import systems.reformcloud.group.template.version.Version;
import systems.reformcloud.shared.StringUtil;
import systems.reformcloud.shared.group.DefaultMainGroup;
import systems.reformcloud.shared.group.DefaultProcessGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.function.Consumer;

final class VersionedGroupSetupVersion implements GroupSetupVersion {

  private final String mainGroupName;
  private final String processGroupName;

  private final Version version;
  private final String displayName;

  public VersionedGroupSetupVersion(String mainGroupName, String processGroupName, Version version) {
    this.mainGroupName = mainGroupName;
    this.processGroupName = processGroupName;
    this.version = version;
    this.displayName = this.formatName();
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
    return this.displayName;
  }

  private String formatName() {
    final int index = this.version.getName().indexOf('_');
    if (index == -1) {
      return this.version.getName().toLowerCase(Locale.ROOT);
    }
    String lower = this.version.getName().toLowerCase(Locale.ROOT).substring(0, index) + ' ' + this.version.getInfo().toString();
    if (lower.endsWith(".0")) {
      lower = StringUtil.replaceLastEmpty(lower, "\\.0");
    }
    return lower;
  }
}
