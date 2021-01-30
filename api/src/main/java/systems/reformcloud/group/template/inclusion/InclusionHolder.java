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
package systems.reformcloud.group.template.inclusion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.network.data.SerializableObject;

import java.util.Collection;
import java.util.Optional;

/**
 * An holder of {@link Inclusion}s.
 */
public interface InclusionHolder extends SerializableObject {

  /**
   * Get all template inclusions of this holder.
   *
   * @return All template inclusions of this holder.
   */
  @NotNull
  @UnmodifiableView
  Collection<Inclusion> getTemplateInclusions();

  /**
   * Get all template inclusions of this holder with the specified {@code loadType}.
   *
   * @param loadType The load type of the inclusion.
   * @return All template inclusions of this holder with the specified {@code loadType}.
   */
  @NotNull
  @UnmodifiableView
  Collection<Inclusion> getTemplateInclusions(@NotNull Inclusion.InclusionLoadType loadType);

  /**
   * Gets a specific template inclusion.
   *
   * @param template The template name in the format {@code groupName/templateName}.
   * @return The specific inclusion for the provided {@code template}.
   */
  @NotNull
  Optional<Inclusion> getTemplateInclusion(@NotNull String template);

  /**
   * Adds a template inclusion to this holder.
   *
   * @param inclusion The inclusion to add.
   */
  void addTemplateInclusions(@NotNull Inclusion inclusion);

  /**
   * Removes a specific template inclusion from this holder.
   *
   * @param inclusion The inclusion to remove.
   */
  void removeTemplateInclusion(@NotNull Inclusion inclusion);

  /**
   * Removes a specific template inclusion from this holder.
   *
   * @param template The name of the template to remove in the format {@code groupName/templateName}.
   */
  void removeTemplateInclusion(@NotNull String template);

  /**
   * Removes all template inclusions from this holder.
   */
  void removeAllTemplateInclusions();

  /**
   * Get all path inclusions known to this holder.
   *
   * @return All path inclusions known to this holder.
   */
  @NotNull
  @UnmodifiableView
  Collection<Inclusion> getPathInclusions();

  /**
   * Get all path inclusions known to this holder.
   *
   * @param loadType The load type of the inclusion.
   * @return All path inclusions known to this holder.
   */
  @NotNull
  @UnmodifiableView
  Collection<Inclusion> getPathInclusions(@NotNull Inclusion.InclusionLoadType loadType);

  /**
   * Gets a specific path inclusion.
   *
   * @param path The path name in the format {@code path/to/folder}.
   * @return The specific inclusion for the provided {@code path}.
   */
  @NotNull
  Optional<Inclusion> getPathInclusion(@NotNull String path);

  /**
   * Adds a path inclusion to this holder.
   *
   * @param inclusion The path inclusion to add.
   */
  void addPathInclusions(@NotNull Inclusion inclusion);

  /**
   * Removes a specific path inclusion from this holder.
   *
   * @param inclusion The inclusion to remove.
   */
  void removePathInclusion(@NotNull Inclusion inclusion);

  /**
   * Removes a specific path inclusion from this holder.
   *
   * @param path The path name in the format {@code path/to/folder}.
   */
  void removePathInclusion(@NotNull String path);

  /**
   * Removes all path inclusions known to this holder.
   */
  void removeAllPathInclusions();
}
