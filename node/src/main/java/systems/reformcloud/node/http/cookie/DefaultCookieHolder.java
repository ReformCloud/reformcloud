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
package systems.reformcloud.node.http.cookie;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.http.Headers;
import systems.reformcloud.http.HttpVersion;
import systems.reformcloud.http.cookie.CookieHolder;
import systems.reformcloud.http.cookie.HttpCookie;
import systems.reformcloud.http.decode.DecodeResult;
import systems.reformcloud.utility.MoreCollections;
import systems.reformcloud.node.http.DefaultHttpInformation;
import systems.reformcloud.node.http.InstanceHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class DefaultCookieHolder<T extends CookieHolder<T>> extends DefaultHttpInformation<T> implements CookieHolder<T>, InstanceHolder<T> {

  private final Collection<HttpCookie> cookies;

  protected DefaultCookieHolder(HttpVersion httpVersion, Headers headers, DecodeResult decodeResult) {
    super(httpVersion, headers, decodeResult);
    this.cookies = headers.get(HttpHeaderNames.COOKIE.toString()).map(CookieCoder::decode).orElseGet(() -> new CopyOnWriteArraySet<>());
  }

  @Override
  public @NotNull Optional<HttpCookie> cookie(@NotNull String name) {
    return Optional.ofNullable(MoreCollections.filter(this.cookies, cookie -> cookie.name().equals(name)));
  }

  @Override
  public @NotNull Collection<HttpCookie> cookies() {
    return this.cookies;
  }

  @Override
  public @NotNull T cookie(@NotNull HttpCookie cookie) {
    this.cookies.add(cookie);
    return this.self();
  }

  @Override
  public @NotNull T cookies(@NonNls HttpCookie... cookies) {
    this.cookies.addAll(Arrays.asList(cookies));
    return this.self();
  }

  @Override
  public @NotNull T cookies(@NotNull Collection<HttpCookie> cookies) {
    this.cookies.addAll(cookies);
    return this.self();
  }
}
