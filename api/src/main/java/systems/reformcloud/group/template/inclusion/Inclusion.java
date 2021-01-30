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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.data.SerializableObject;

/**
 * Represents a loadable inclusion of a template.
 */
public interface Inclusion extends SerializableObject, Cloneable {

  /**
   * Creates an inclusion with the provided information.
   *
   * @param key               The key of the inclusion. This varies for the different types of inclusions.
   *                          A template inclusion is in the format {@code group/template-name}, a path
   *                          inclusion is in the format {@code path/to/folder}.
   * @param backend           The loading backend name of the inclusion.
   * @param inclusionLoadType The load order of the inclusion.
   * @return The created inclusion.
   */
  @NotNull
  @Contract(value = "_, _, _ -> new", pure = true)
  static Inclusion inclusion(@NotNull String key, @NotNull String backend, @NotNull InclusionLoadType inclusionLoadType) {
    return new DefaultInclusion(key, backend, inclusionLoadType);
  }

  /**
   * Get the key of the inclusion. This varies for the different types of inclusions.
   * A template inclusion is in the format {@code group/template-name}, a path
   * inclusion is in the format {@code path/to/folder}.
   *
   * @return the key of the inclusion.
   */
  @NotNull
  String getKey();

  /**
   * Sets the key of the inclusion. This varies for the different types of inclusions.
   * A template inclusion is in the format {@code group/template-name}, a path
   * inclusion is in the format {@code path/to/folder}.
   *
   * @param key the new key of the inclusion.
   */
  void setKey(@NotNull String key);

  /**
   * Gets the backend which loads the inclusion.
   *
   * @return The backend which loads the inclusion.
   */
  @NotNull
  String getBackend();

  /**
   * Sets the backend which loads the inclusion.
   *
   * @param backend The backend which loads the inclusion.
   */
  void setBackend(@NotNull String backend);

  /**
   * Gets the load order of this inclusion.
   *
   * @return The load order of this inclusion.
   */
  @NotNull
  InclusionLoadType getInclusionLoadType();

  /**
   * Sets the load order of this inclusion.
   *
   * @param inclusionLoadType The load order of this inclusion.
   */
  void setInclusionLoadType(@NotNull InclusionLoadType inclusionLoadType);

  /**
   * Creates a clone of this inclusion.
   *
   * @return A clone of this inclusion.
   */
  @NotNull
  Inclusion clone();

  /**
   * The priority for an inclusion.
   */
  enum InclusionLoadType {
    /**
     * The inclusion is loaded before the primary template is loaded, meaning that
     * the template overrides files created by the inclusion.
     */
    PRE,
    /**
     * The inclusion is loaded after the primary template is loaded, meaning that
     * the inclusion overrides files created by the primary template.
     */
    PAST
  }
}
