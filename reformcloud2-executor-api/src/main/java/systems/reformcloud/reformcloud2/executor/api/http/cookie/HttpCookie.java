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
package systems.reformcloud.reformcloud2.executor.api.http.cookie;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a cookie which can be sent by/to the client.
 *
 * @author derklaro
 * @since 25. October 2020
 */
public interface HttpCookie {
  /**
   * Constant for an undefined maximum age of a cookie. By default this means that the cookie
   * gets removed after the end of the browser session.
   */
  long UNDEFINED_MAX_AGE = Long.MIN_VALUE;

  /**
   * Creates a new default implementation of a cookie.
   *
   * @param name  the name of the new cookie.
   * @param value the value of the new cookie.
   * @return a new cookie with the given name.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  static HttpCookie cookie(@NotNull String name, @NotNull String value) {
    return new DefaultHttpCookie(name).value(value);
  }

  /**
   * The name of this cookie. It's the only unmodifiable value of a cookie.
   *
   * @return the name of this cookie.
   */
  @NotNull
  String name();

  /**
   * The value of this cookie.
   *
   * @return the value of this cookie.
   */
  @NotNull
  String value();

  /**
   * Sets the value of this cookie.
   *
   * @param value the new value of this cookie.
   * @return the same instance of this class, for chaining
   */
  @NotNull
  HttpCookie value(@NotNull String value);

  /**
   * Returns {@code true} if the original value of this cookie was wrapped with
   * double quotes in the original {@code Set-Cookie} header.
   *
   * @return if the value of this cookie is to be wrapped.
   */
  boolean wrap();

  /**
   * Sets if the value of this cookie should be wrapped with double quotes in the
   * final {@code Set-Cookie} header.
   *
   * @param wrap if the value should be wrapped
   * @return the same instance of this class, for chaining
   */
  @NotNull
  HttpCookie wrap(boolean wrap);

  /**
   * Get the domain of this cookie.
   *
   * @return the domain of this cookie.
   */
  @Nullable
  String domain();

  /**
   * Sets the domain of this cookie.
   *
   * @param domain the domain to use
   * @return the same instance of this class, for chaining
   */
  @NotNull
  HttpCookie domain(@Nullable String domain);

  /**
   * Get the path of this cookie.
   *
   * @return the path of this cookie.
   */
  @Nullable
  String path();

  /**
   * Sets the path of this cookie.
   *
   * @param path the path to use.
   * @return the same instance of this class, for chaining
   */
  @NotNull
  HttpCookie path(@Nullable String path);

  /**
   * Get the maximum age of this cookie or {@link HttpCookie#UNDEFINED_MAX_AGE} if unspecified.
   *
   * @return the maximum age of this cookie.
   */
  long maxAge();

  /**
   * Sets the maximum age of this cookie in seconds. If a max age of {@link HttpCookie#UNDEFINED_MAX_AGE} is
   * specified the cookie will be removed if the browser gets closed. If a max age of {@code 0} is specified
   * the cookie will be removed immediately from the browser.
   *
   * @param maxAge the maximum age of this cookie in seconds.
   * @return the same instance of this class, for chaining
   */
  @NotNull
  HttpCookie maxAge(long maxAge);

  /**
   * Get the security status of this cookie.
   *
   * @return the security status of this cookie.
   */
  boolean secure();

  /**
   * Sets the security status of this cookie.
   *
   * @param secure if this cookie is secure.
   * @return the same instance of this class, for chaining
   */
  @NotNull
  HttpCookie secure(boolean secure);

  /**
   * Get if this cookie is only accessible via HTTP. If this is {@code true} the cookie
   * cannot be access by for example a client side script (only if the browser supports it).
   *
   * @return if this cookie can only be accessed via HTTP.
   */
  boolean httpOnly();

  /**
   * Sets if this cookie is only accessible via HTTP. If this is {@code true} the cookie
   * cannot be access by for example a client side script (only if the browser supports it).
   *
   * @param httpOnly if this cookie can only be accessed via HTTP.
   * @return the same instance of this class, for chaining
   */
  @NotNull
  HttpCookie httpOnly(boolean httpOnly);
}
