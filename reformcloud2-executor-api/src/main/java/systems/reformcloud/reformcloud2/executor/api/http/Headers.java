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
package systems.reformcloud.reformcloud2.executor.api.http;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Provides access to standard HTTP header methods and works like a map for
 * handling headers (get, add, set, remove, clear operations are supported).
 *
 * @author derklaro
 * @since 25. October 2020
 */
public interface Headers extends Iterable<Map.Entry<String, String>> {

  /**
   * Get the value of the header with the specified {@code name}. If there are
   * more than one values for the header, the first one is returned.
   *
   * @param name the name of the header to get
   * @return the first header or an empty optional if the header is unknown
   */
  @NotNull
  Optional<String> get(@NotNull String name);

  /**
   * Get the value of the header with the specified {@code name}. If there are
   * more than one values for the header, the first one is returned. If there is
   * no header with the specified name {@code defaultValue} is returned.
   *
   * @param name         the name of the header to get
   * @param defaultValue the default value to get if there is no header by the given {@code name}.
   * @return the first header or {@code defaultValue} if the header is unknown
   */
  @NotNull
  String get(@NotNull String name, @NotNull String defaultValue);

  /**
   * Get the int value of the header with the specified {@code name}. If there are
   * more than one values for the header, the first one is returned.
   *
   * @param name the name of the header to get
   * @return the first header or an empty optional if the header is unknown
   */
  @NotNull
  Optional<Integer> getInt(@NotNull String name);

  /**
   * Get the int value of the header with the specified {@code name}. If there are
   * more than one values for the header, the first one is returned. If there is
   * no header with the specified name {@code defaultValue} is returned.
   *
   * @param name         the name of the header to get
   * @param defaultValue the default value to get if there is no header by the given {@code name}.
   * @return the first header or {@code defaultValue} if the header is unknown
   */
  int getInt(@NotNull String name, int defaultValue);

  /**
   * Get the short value of the header with the specified {@code name}. If there are
   * more than one values for the header, the first one is returned.
   *
   * @param name the name of the header to get
   * @return the first header or an empty optional if the header is unknown
   */
  @NotNull
  Optional<Short> getShort(@NotNull String name);

  /**
   * Get the short value of the header with the specified {@code name}. If there are
   * more than one values for the header, the first one is returned. If there is
   * no header with the specified name {@code defaultValue} is returned.
   *
   * @param name         the name of the header to get
   * @param defaultValue the default value to get if there is no header by the given {@code name}.
   * @return the first header or {@code defaultValue} if the header is unknown
   */
  short getShort(@NotNull String name, short defaultValue);

  /**
   * Get the date value of the header with the specified {@code name}. If there are
   * more than one values for the header, the first one is returned.
   *
   * @param name the name of the header to get
   * @return the first header or an empty optional if the header is unknown or not a date
   */
  @NotNull
  Optional<Long> getTimeMillis(@NotNull String name);

  /**
   * Get the date value of the header with the specified {@code name}. If there are
   * more than one values for the header, the first one is returned. If there is
   * no header with the specified name {@code defaultValue} is returned.
   *
   * @param name         the name of the header to get
   * @param defaultValue the default value to get if there is no header by the given {@code name}.
   * @return the first header or {@code defaultValue} if the header is unknown or not a date
   */
  long getTimeMillis(@NotNull String name, long defaultValue);

  /**
   * Get all values of headers with the specified {@code name}.
   *
   * @param name the name of the headers to get.
   * @return a list of the headers which is empty if no value is found
   */
  @NotNull
  List<String> getAll(@NotNull String name);

  /**
   * Get a list that contains all headers in known to this object. Modifying the returned
   * list has no effect to this object. For iterate-only use {@link #iterator()} which has
   * less overhead.
   *
   * @return a list that contains all headers known to this object.
   */
  @NotNull
  List<Map.Entry<String, String>> entries();

  /**
   * Get if there is at least one header with the specified {@code name}.
   *
   * @param name the name of the header to check for.
   * @return if there is at least one header with the specified {@code name}.
   */
  boolean contains(@NotNull String name);

  /**
   * Get if this object does not hold any header.
   *
   * @return if this object does not hold any header.
   */
  boolean isEmpty();

  /**
   * Get the number of headers known to this object.
   *
   * @return the number of headers known to this object.
   */
  int size();

  /**
   * Get a list that contains all name of all headers in known to this object. Modifying the
   * returned list has no effect to this object. For iterate-only use {@link #iterator()} which
   * has less overhead.
   *
   * @return a list that contains all names of all headers known to this object.
   */
  @NotNull
  Set<String> names();

  /**
   * Adds all header entries of the given {@code headers}.
   *
   * @param headers the headers to add into this object.
   * @return the same instance of this class, for chaining
   */
  @NotNull
  Headers add(@NotNull Headers headers);

  /**
   * Adds a new header with the given name ond value.
   *
   * @param name  the name of the header
   * @param value the value of the header
   * @return the same instance of this class, for chaining
   */
  @NotNull
  Headers add(@NotNull String name, @NotNull Object value);

  /**
   * Adds a new header with the given name ond int value.
   *
   * @param name  the name of the header
   * @param value the value of the header
   * @return the same instance of this class, for chaining
   */
  @NotNull
  Headers addInt(@NotNull String name, int value);

  /**
   * Adds a new header with the given name ond short value.
   *
   * @param name  the name of the header
   * @param value the value of the header
   * @return the same instance of this class, for chaining
   */
  @NotNull
  Headers addShort(@NotNull String name, short value);

  /**
   * Sets all headers from the given headers into this.
   *
   * @param headers the headers to set.
   * @return the same instance of this class, for chaining
   */
  @NotNull
  Headers setAll(@NotNull Headers headers);

  /**
   * Sets a new header with the given name ond value overriding the existing one.
   *
   * @param name  the name of the header
   * @param value the value of the header
   * @return the same instance of this class, for chaining
   */
  @NotNull
  Headers set(@NotNull String name, @NotNull Object value);

  /**
   * Sets a new header with the given name ond int value overriding the existing one.
   *
   * @param name  the name of the header
   * @param value the value of the header
   * @return the same instance of this class, for chaining
   */
  @NotNull
  Headers setInt(@NotNull String name, int value);

  /**
   * Sets a new header with the given name ond short value overriding the existing one.
   *
   * @param name  the name of the header
   * @param value the value of the header
   * @return the same instance of this class, for chaining
   */
  @NotNull
  Headers setShort(@NotNull String name, short value);

  /**
   * Removes the header by the specified {@code name}.
   *
   * @param name the name of the header to remove
   * @return the same instance of this class, for chaining
   */
  @NotNull
  Headers remove(@NotNull String name);

  /**
   * Clears all headers known to this object
   *
   * @return the same instance of this class, for chaining
   */
  @NotNull
  Headers clear();
}
