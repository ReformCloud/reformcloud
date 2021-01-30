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
package systems.reformcloud.group.template.builder;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.group.template.Template;
import systems.reformcloud.group.template.inclusion.Inclusion;
import systems.reformcloud.group.template.runtime.RuntimeConfiguration;
import systems.reformcloud.group.template.version.Version;

import java.util.Collection;

/**
 * A builder for a {@link Template}.
 */
public interface TemplateBuilder {
  /**
   * The default backend for a template is the local I/O file backend.
   */
  String FILE_BACKEND = "FILE";
  /**
   * The default separator for server names: {@code GroupName}{@code separator}{@code id}
   */
  String DEFAULT_SERVER_NAME_SPLITTER = "-";
  /**
   * The default, very minimalistic runtime configuration for a template.
   */
  RuntimeConfiguration DEFAULT_RUNTIME_CONFIGURATION = RuntimeConfiguration.configuration(256, 512, 0);

  /**
   * Creates a new builder. This method is for internal usage, use {@link Template#builder(String, Version)} instead.
   *
   * @param name    The name of the template to create.
   * @param version The version of the template to create.
   * @return A new builder for a template with default values applied.
   */
  @NotNull
  @ApiStatus.Internal
  @Contract(value = "_, _ -> new", pure = true)
  static TemplateBuilder newBuilder(@NotNull String name, @NotNull Version version) {
    return new DefaultTemplateBuilder(name, version);
  }

  /**
   * Sets the name of the new template.
   *
   * @param name The name of the new template.
   * @return The same instance of this class, for chaining.
   */
  @NotNull
  TemplateBuilder name(@NotNull String name);

  /**
   * Get the name of the new template.
   *
   * @return The name of the new template.
   */
  @NotNull
  String name();

  /**
   * Sets the priority of the new template. Defaults to {@code 0}.
   *
   * @param priority The priority of the template.
   * @return The same instance of this class, for chaining.
   */
  @NotNull
  TemplateBuilder priority(int priority);

  /**
   * Get the priority of the new template.
   *
   * @return the priority of the new template.
   */
  int priority();

  /**
   * Sets this template to a global template. When a process starts all global templates
   * will be copied to the process along of the primary template defined when starting the
   * process. Defaults to {@code false}.
   *
   * @param global If this template should be a global template.
   * @return The same instance of this class, for chaining.
   */
  @NotNull
  TemplateBuilder global(boolean global);

  /**
   * Get if this template should be a global template.
   *
   * @return If this template should be a global template.
   */
  boolean global();

  /**
   * Gets if this template should be copied to the backend after a process with
   * this template as primary template (so this has no effect if it is a global
   * template) was stopped. This might be extremely powerful for example when you
   * want to configure a mini game and want to not lose your data after the process
   * shutdown. Defaults to {@code false}.
   *
   * @param autoCopyOnClose If the template should get copied automatically to the
   *                        backend after the process shutdown.
   * @return The same instance of this class, for chaining.
   */
  @NotNull
  TemplateBuilder autoCopyOnClose(boolean autoCopyOnClose);

  /**
   * Get if the template should get copied automatically to the backend after the process shutdown.
   *
   * @return if the template should get copied automatically to the backend after the process shutdown.
   */
  boolean autoCopyOnClose();

  /**
   * Sets the backend of this template. The backend is responsible for loading the template
   * when a process gets prepared. Defaults to {@link #FILE_BACKEND}.
   *
   * @param backend The backend of this template.
   * @return The same instance of this class, for chaining.
   */
  @NotNull
  TemplateBuilder backend(@NotNull String backend);

  /**
   * Get the backend of this template.
   *
   * @return the backend of this template.
   */
  @NotNull
  String backend();

  /**
   * Sets the server name splitter of this template. The name of a process will
   * be formatted as {@code GroupName}{@code separator}{@code id}. Defaults to {@link #DEFAULT_SERVER_NAME_SPLITTER}
   *
   * @param serverNameSplitter The server name splitter to use.
   * @return The same instance of this class, for chaining.
   */
  @NotNull
  TemplateBuilder serverNameSplitter(@Nullable String serverNameSplitter);

  /**
   * Get the server name splitter for this template.
   *
   * @return the server name splitter for this template.
   */
  @NotNull
  String serverNameSplitter();

  /**
   * Sets the runtime configuration for this template. Defaults to {@link #DEFAULT_RUNTIME_CONFIGURATION}
   *
   * @param runtimeConfiguration The runtime configuration to use.
   * @return The same instance of this class, for chaining.
   */
  @NotNull
  TemplateBuilder runtimeConfiguration(@NotNull RuntimeConfiguration runtimeConfiguration);

  /**
   * Get the runtime configuration to use for this template.
   *
   * @return The runtime configuration to use for this template.
   */
  @NotNull
  RuntimeConfiguration runtimeConfiguration();

  /**
   * Sets the version of this template.
   *
   * @param version The version to use for this template.
   * @return The same instance of this class, for chaining.
   * @see systems.reformcloud.group.template.version.Versions
   */
  @NotNull
  TemplateBuilder version(@NotNull Version version);

  /**
   * Get the version to use for this template.
   *
   * @return the version to use for this template.
   */
  @NotNull
  Version version();

  /**
   * Sets the template inclusions for this template. Inclusions will be loaded during
   * the startup of a process when this template gets loaded. Defaults to an empty list.
   *
   * @param templateInclusions The template inclusions to load during the startup.
   * @return The same instance of this class, for chaining.
   */
  @NotNull
  TemplateBuilder templateInclusions(@NotNull Collection<Inclusion> templateInclusions);

  /**
   * Get the template inclusions for this template.
   *
   * @return The template inclusions for this template.
   */
  @NotNull
  Collection<Inclusion> templateInclusions();

  /**
   * Sets the path inclusions for this template. Inclusions will be loaded during
   * the startup of a process when this template gets loaded. Defaults to an empty list.
   *
   * @param pathInclusions The path inclusions to load during the startup.
   * @return The same instance of this class, for chaining.
   */
  @NotNull
  TemplateBuilder pathInclusions(@NotNull Collection<Inclusion> pathInclusions);

  /**
   * Get the path inclusions to load during the startup.
   *
   * @return The path inclusions to load during the startup.
   */
  @NotNull
  Collection<Inclusion> pathInclusions();

  /**
   * Sets the extra data of this template. This data is persistent and can store useful
   * information for a process. Defaults to an empty {@link JsonConfiguration}.
   *
   * @param data The extra data of this template.
   * @return The same instance of this class, for chaining.
   */
  @NotNull
  TemplateBuilder jsonData(@NotNull JsonConfiguration data);

  /**
   * Get the json data of this template.
   *
   * @return the json data of this template.
   */
  @NotNull
  JsonConfiguration jsonData();

  /**
   * Builds the template from the information provided in this builder.
   *
   * @return The created template.
   */
  @NotNull
  Template build();
}
