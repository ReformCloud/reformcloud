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
package systems.reformcloud.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Objects;

public final class Conditions {

  /**
   * Assumes that the given argument is true
   *
   * @param test    The argument which should be checked
   * @param message The message which will be printed if the argument is {@code false}
   */
  public static void isTrue(boolean test, @Nullable Object message) {
    if (!test) {
      throw new IllegalArgumentException(String.valueOf(message));
    }
  }

  /**
   * Assumes that the given argument is true
   *
   * @param test The argument which should be checked
   */
  public static void isTrue(boolean test) {
    isTrue(test, null);
  }

  /**
   * Assumes that the given argument is true
   *
   * @param test    The argument which should be checked
   * @param message The message which will be printed if the argument is {@code false}
   * @param args    The arguments which should be filled in in the message
   */
  public static void isTrue(boolean test, @NotNull String message, @Nullable Object... args) {
    if (!test) {
      throw new IllegalStateException(MessageFormat.format(message, args));
    }
  }

  /**
   * Checks if the given object is non-null
   *
   * @param obj The object which should get checked
   */
  public static void nonNull(@Nullable Object obj) {
    nonNull(obj, null);
  }

  /**
   * Checks if the given object is non-null
   *
   * @param obj     The object which should get checked
   * @param message The message which will be the stacktrace or {@code null}
   */
  public static void nonNull(@Nullable Object obj, @Nullable Object message) {
    Objects.requireNonNull(obj, String.valueOf(message));
  }
}
