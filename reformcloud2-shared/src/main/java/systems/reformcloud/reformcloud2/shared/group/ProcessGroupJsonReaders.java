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
import systems.reformcloud.reformcloud2.executor.api.configuration.json.Element;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.JsonReader;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.adapter.JsonAdapter;
import systems.reformcloud.reformcloud2.executor.api.group.process.player.DefaultPlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.player.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.startup.AutomaticStartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.startup.DefaultAutomaticStartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.startup.DefaultStartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.startup.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.template.Template;
import systems.reformcloud.reformcloud2.executor.api.group.template.builder.DefaultTemplate;
import systems.reformcloud.reformcloud2.executor.api.group.template.runtime.DefaultRuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.template.runtime.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.DefaultVersion;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.Version;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.Versions;

import java.lang.reflect.Type;

public class ProcessGroupJsonReaders {

  public static final JsonAdapter ADAPTER = JsonAdapter.builder()
    .registerJsonReader(StartupConfiguration.class, new StartupConfigurationJsonReader())
    .registerJsonReader(AutomaticStartupConfiguration.class, new AutomaticStartupConfigurationJsonReader())
    .registerJsonReader(PlayerAccessConfiguration.class, new PlayerAccessConfigurationJsonReader())
    .registerJsonReader(Template.class, new TemplateJsonReader())
    .registerJsonReader(RuntimeConfiguration.class, new RuntimeConfigurationJsonReader())
    .registerJsonReader(Version.class, new VersionJsonReader())
    // options
    .disableHtmlEscaping()
    .enablePrettyPrinting()
    .enableNullSerialisation()
    .build();

  private ProcessGroupJsonReaders() {
    throw new UnsupportedOperationException();
  }

  private static final class StartupConfigurationJsonReader implements JsonReader<StartupConfiguration> {
    @Override public @NotNull Element serialize(@NotNull StartupConfiguration startupConfiguration, Type typeOfT) {
      return ADAPTER.toTree(startupConfiguration, typeOfT);
    }

    @Override public @NotNull StartupConfiguration deserialize(@NotNull Element element, @NotNull Type typeOfT) {
      return ADAPTER.fromJson(element, DefaultStartupConfiguration.class);
    }

    @Override public @NotNull StartupConfiguration createInstance(@NotNull Type type) {
      return StartupConfiguration.newDefaultConfiguration();
    }
  }

  private static final class AutomaticStartupConfigurationJsonReader implements JsonReader<AutomaticStartupConfiguration> {
    @Override public @NotNull Element serialize(@NotNull AutomaticStartupConfiguration automaticStartupConfiguration, Type typeOfT) {
      return ADAPTER.toTree(automaticStartupConfiguration, typeOfT);
    }

    @Override public @NotNull AutomaticStartupConfiguration deserialize(@NotNull Element element, @NotNull Type typeOfT) {
      return ADAPTER.fromJson(element, DefaultAutomaticStartupConfiguration.class);
    }

    @Override public @NotNull AutomaticStartupConfiguration createInstance(@NotNull Type type) {
      return AutomaticStartupConfiguration.onlyAutomaticStartup();
    }
  }

  private static final class PlayerAccessConfigurationJsonReader implements JsonReader<PlayerAccessConfiguration> {
    @Override public @NotNull Element serialize(@NotNull PlayerAccessConfiguration playerAccessConfiguration, Type typeOfT) {
      return ADAPTER.toTree(playerAccessConfiguration, typeOfT);
    }

    @Override public @NotNull PlayerAccessConfiguration deserialize(@NotNull Element element, @NotNull Type typeOfT) {
      return ADAPTER.fromJson(element, DefaultPlayerAccessConfiguration.class);
    }

    @Override public @NotNull PlayerAccessConfiguration createInstance(@NotNull Type type) {
      return PlayerAccessConfiguration.disabled();
    }
  }

  private static final class TemplateJsonReader implements JsonReader<Template> {
    @Override public @NotNull Element serialize(@NotNull Template template, Type typeOfT) {
      return ADAPTER.toTree(template, typeOfT);
    }

    @Override public @NotNull Template deserialize(@NotNull Element element, @NotNull Type typeOfT) {
      return ADAPTER.fromJson(element, DefaultTemplate.class);
    }

    @Override public @NotNull Template createInstance(@NotNull Type type) {
      return Template.builder("default", Versions.PAPER_1_8_8).build();
    }
  }

  private static final class RuntimeConfigurationJsonReader implements JsonReader<RuntimeConfiguration> {
    @Override public @NotNull Element serialize(@NotNull RuntimeConfiguration runtimeConfiguration, Type typeOfT) {
      return ADAPTER.toTree(runtimeConfiguration, typeOfT);
    }

    @Override public @NotNull RuntimeConfiguration deserialize(@NotNull Element element, @NotNull Type typeOfT) {
      return ADAPTER.fromJson(element, DefaultRuntimeConfiguration.class);
    }

    @Override public @NotNull RuntimeConfiguration createInstance(@NotNull Type type) {
      return RuntimeConfiguration.configuration(256, 512, 0);
    }
  }

  private static final class VersionJsonReader implements JsonReader<Version> {
    @Override public @NotNull Element serialize(@NotNull Version version, Type typeOfT) {
      return ADAPTER.toTree(version, typeOfT);
    }

    @Override public @NotNull Version deserialize(@NotNull Element element, @NotNull Type typeOfT) {
      return ADAPTER.fromJson(element, DefaultVersion.class);
    }

    @Override public @NotNull Version createInstance(@NotNull Type type) {
      return new DefaultVersion();
    }
  }
}
