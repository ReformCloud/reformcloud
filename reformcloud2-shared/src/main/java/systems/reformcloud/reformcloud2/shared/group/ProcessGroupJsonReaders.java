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
package systems.reformcloud.reformcloud2.shared.group;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.JsonInstanceCreator;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.adapter.JsonAdapter;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.adapter.JsonAdapterBuilder;
import systems.reformcloud.reformcloud2.executor.api.group.process.player.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.startup.AutomaticStartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.startup.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.template.Template;
import systems.reformcloud.reformcloud2.executor.api.group.template.runtime.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.DefaultVersion;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.Version;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.Versions;

import java.lang.reflect.Type;

public class ProcessGroupJsonReaders {

  private static final JsonAdapterBuilder ADAPTER = JsonAdapter.builder()
    .registerInstanceCreator(StartupConfiguration.class, new StartupConfigurationInstanceCreator())
    .registerInstanceCreator(AutomaticStartupConfiguration.class, new AutomaticStartupConfigurationInstanceCreator())
    .registerInstanceCreator(PlayerAccessConfiguration.class, new PlayerAccessConfigurationInstanceCreator())
    .registerInstanceCreator(Template.class, new TemplateInstanceCreator())
    .registerInstanceCreator(RuntimeConfiguration.class, new RuntimeConfigurationInstanceCreator())
    .registerInstanceCreator(Version.class, new VersionInstanceCreator());

  private ProcessGroupJsonReaders() {
    throw new UnsupportedOperationException();
  }

  @NotNull
  public static JsonAdapterBuilder builder() {
    return ADAPTER.clone();
  }

  private static final class StartupConfigurationInstanceCreator implements JsonInstanceCreator<StartupConfiguration> {
    @Override public @NotNull StartupConfiguration createInstance(@NotNull Type type) {
      return StartupConfiguration.newDefaultConfiguration();
    }
  }

  private static final class AutomaticStartupConfigurationInstanceCreator implements JsonInstanceCreator<AutomaticStartupConfiguration> {
    @Override public @NotNull AutomaticStartupConfiguration createInstance(@NotNull Type type) {
      return AutomaticStartupConfiguration.onlyAutomaticStartup();
    }
  }

  private static final class PlayerAccessConfigurationInstanceCreator implements JsonInstanceCreator<PlayerAccessConfiguration> {
    @Override public @NotNull PlayerAccessConfiguration createInstance(@NotNull Type type) {
      return PlayerAccessConfiguration.disabled();
    }
  }

  private static final class TemplateInstanceCreator implements JsonInstanceCreator<Template> {
    @Override public @NotNull Template createInstance(@NotNull Type type) {
      return Template.builder("default", Versions.PAPER_1_8_8).build();
    }
  }

  private static final class RuntimeConfigurationInstanceCreator implements JsonInstanceCreator<RuntimeConfiguration> {
    @Override public @NotNull RuntimeConfiguration createInstance(@NotNull Type type) {
      return RuntimeConfiguration.configuration(512, 512, 0);
    }
  }

  private static final class VersionInstanceCreator implements JsonInstanceCreator<Version> {
    @Override public @NotNull Version createInstance(@NotNull Type type) {
      return new DefaultVersion();
    }
  }
}
