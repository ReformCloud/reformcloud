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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class DefaultHttpCookie implements HttpCookie {

  private final String name;
  private String value;
  private boolean wrap;
  private String domain;
  private String path;
  private long maxAge = UNDEFINED_MAX_AGE;
  private boolean secure;
  private boolean httpOnly;

  DefaultHttpCookie(String name) {
    this.name = name;
  }

  @Override
  public @NotNull String name() {
    return this.name;
  }

  @Override
  public @NotNull String value() {
    return this.value;
  }

  @Override
  public @NotNull HttpCookie value(@NotNull String value) {
    this.value = value;
    return this;
  }

  @Override
  public boolean wrap() {
    return this.wrap;
  }

  @Override
  public @NotNull HttpCookie wrap(boolean wrap) {
    this.wrap = wrap;
    return this;
  }

  @Override
  public @Nullable String domain() {
    return this.domain;
  }

  @Override
  public @NotNull HttpCookie domain(@Nullable String domain) {
    this.domain = domain;
    return this;
  }

  @Override
  public @Nullable String path() {
    return this.path;
  }

  @Override
  public @NotNull HttpCookie path(@Nullable String path) {
    this.path = path;
    return this;
  }

  @Override
  public long maxAge() {
    return this.maxAge;
  }

  @Override
  public @NotNull HttpCookie maxAge(long maxAge) {
    this.maxAge = maxAge;
    return this;
  }

  @Override
  public boolean secure() {
    return this.secure;
  }

  @Override
  public @NotNull HttpCookie secure(boolean secure) {
    this.secure = secure;
    return this;
  }

  @Override
  public boolean httpOnly() {
    return this.httpOnly;
  }

  @Override
  public @NotNull HttpCookie httpOnly(boolean httpOnly) {
    this.httpOnly = httpOnly;
    return this;
  }
}
