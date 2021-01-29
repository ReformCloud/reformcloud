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

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.http.cookie.HttpCookie;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class CookieCoder {

  private CookieCoder() {
    throw new UnsupportedOperationException();
  }

  @NotNull
  public static Set<HttpCookie> decode(@NotNull String header) {
    return ServerCookieDecoder.LAX.decode(header).stream().map(cookie -> HttpCookie.cookie(cookie.name(), cookie.value())
      .httpOnly(cookie.isHttpOnly())
      .domain(cookie.domain())
      .maxAge(cookie.maxAge())
      .path(cookie.path())
      .secure(cookie.isSecure())
      .wrap(cookie.wrap())
    ).collect(Collectors.toSet());
  }

  @NotNull
  public static List<String> encode(@NotNull Collection<HttpCookie> cookies) {
    return ServerCookieEncoder.LAX.encode(cookies.stream().map(cookie -> {
      Cookie nettyCookie = new DefaultCookie(cookie.name(), cookie.value());
      nettyCookie.setDomain(cookie.domain());
      nettyCookie.setHttpOnly(cookie.httpOnly());
      nettyCookie.setMaxAge(cookie.maxAge());
      nettyCookie.setPath(cookie.path());
      nettyCookie.setWrap(cookie.wrap());
      nettyCookie.setSecure(cookie.secure());
      return nettyCookie;
    }).collect(Collectors.toList()));
  }
}
