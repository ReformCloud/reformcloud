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
package systems.reformcloud.functional;

import org.jetbrains.annotations.NotNull;

/**
 * An object than can be compared to another.
 *
 * @param <T> The type of the object.
 */
public interface Sorted<T> extends Comparable<T> {

  /**
   * Checks if the current instance is less than {@code that}.
   *
   * @param that The object to compare.
   * @return {@code true} if this object is less than {@code that}, else {@code false}
   */
  default boolean lt(@NotNull T that) {
    return this.compareTo(that) < 0;
  }

  /**
   * Checks if the current instance is less than or equal to {@code that}.
   *
   * @param that The object to compare.
   * @return {@code true} if this object is less than or equal to {@code that}, else {@code false}
   */
  default boolean lte(@NotNull T that) {
    return this.compareTo(that) <= 0;
  }

  /**
   * Checks if the current instance is greater than {@code that}.
   *
   * @param that The object to compare.
   * @return {@code true} if this object is greater than {@code that}, else {@code false}
   */
  default boolean gt(@NotNull T that) {
    return this.compareTo(that) > 0;
  }

  /**
   * Checks if the current instance is greater than or equal to {@code that}.
   *
   * @param that The object to compare.
   * @return {@code true} if this object is greater than or equal to {@code that}, else {@code false}
   */
  default boolean gte(@NotNull T that) {
    return this.compareTo(that) >= 0;
  }

  /**
   * Checks if the current instance is equal to {@code that}.
   *
   * @param that The object to compare.
   * @return {@code true} if this object is equal to {@code that}, else {@code false}
   */
  default boolean e(@NotNull T that) {
    return this.compareTo(that) == 0;
  }
}