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
package systems.reformcloud.group.template;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.configuration.data.JsonDataHolder;
import systems.reformcloud.functional.Sorted;
import systems.reformcloud.group.template.builder.TemplateBuilder;
import systems.reformcloud.group.template.inclusion.InclusionHolder;
import systems.reformcloud.group.template.runtime.RuntimeConfiguration;
import systems.reformcloud.group.template.version.Version;
import systems.reformcloud.network.data.SerializableObject;
import systems.reformcloud.utility.name.ReNameable;

/**
 * A template which is the base of a process file structure.
 */
public interface Template extends ReNameable, JsonDataHolder<Template>, InclusionHolder, SerializableObject, Sorted<Template>, Cloneable {

  /**
   * Creates a new template builder.
   *
   * @param name    The name of the template to create.
   * @param version The version of the template to create.
   * @return A new builder for a template with default values applied.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  static TemplateBuilder builder(@NotNull String name, @NotNull Version version) {
    return TemplateBuilder.newBuilder(name, version);
  }

  /**
   * Get the priority of this template.
   *
   * @return The priority of this template.
   */
  int getPriority();

  /**
   * Sets the priority of this template.
   *
   * @param priority The priority of this template.
   */
  void setPriority(int priority);

  /**
   * Gets if this template is a global template.
   *
   * @return If this template is a global template.
   */
  boolean isGlobal();

  /**
   * Sets if this template is a global template.
   *
   * @param global If this template is a global template.
   */
  void setGlobal(boolean global);

  /**
   * Gets if a process gets copied automatically to this template when it closes.
   *
   * @return If a process gets copied automatically to this template when it closes.
   */
  boolean isAutoCopyOnClose();

  /**
   * Sets if a process gets copied automatically to this template when it closes.
   *
   * @param autoCopyOnClose If a process gets copied automatically to this template when it closes.
   */
  void setAutoCopyOnClose(boolean autoCopyOnClose);

  /**
   * Get the backend of this template.
   *
   * @return The backend of this template.
   */
  @NotNull
  String getBackend();

  /**
   * Sets the backend of this template.
   *
   * @param backend The backend to use for this template.
   */
  void setBackend(@NotNull String backend);

  /**
   * Gets the server name splitter of this template.
   *
   * @return The server name splitter of this template.
   */
  @Nullable
  String getServerNameSplitter();

  /**
   * Sets the server name splitter of this template.
   *
   * @param serverNameSplitter The server name splitter to use.
   */
  void setServerNameSplitter(@Nullable String serverNameSplitter);

  /**
   * Gets the runtime configuration of this template.
   *
   * @return The runtime configuration of this template.
   */
  @NotNull
  RuntimeConfiguration getRuntimeConfiguration();

  /**
   * Sets the runtime configuration of this template.
   *
   * @param runtimeConfiguration The runtime configuration to use.
   */
  void setRuntimeConfiguration(@NotNull RuntimeConfiguration runtimeConfiguration);

  /**
   * Gets the version of this template.
   *
   * @return The version of this template.
   */
  @NotNull
  Version getVersion();

  /**
   * Sets the version of this template.
   *
   * @param version The version to use.
   */
  void setVersion(@NotNull Version version);

  /**
   * Creates a clone of this template.
   *
   * @return A clone of this template.
   */
  @NotNull
  Template clone();
}
