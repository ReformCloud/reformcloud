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
package systems.reformcloud.reformcloud2.node.http.request;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.http.Headers;
import systems.reformcloud.reformcloud2.executor.api.http.HttpVersion;
import systems.reformcloud.reformcloud2.executor.api.http.decode.DecodeResult;
import systems.reformcloud.reformcloud2.executor.api.http.request.HttpRequest;
import systems.reformcloud.reformcloud2.executor.api.http.request.HttpRequestSource;
import systems.reformcloud.reformcloud2.executor.api.http.request.RequestMethod;
import systems.reformcloud.reformcloud2.node.http.cookie.CookieCoder;
import systems.reformcloud.reformcloud2.node.http.cookie.DefaultCookieHolder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DefaultHttpRequest extends DefaultCookieHolder<DefaultHttpRequest> implements HttpRequest<DefaultHttpRequest> {

  private final byte[] body;
  private final HttpRequestSource source;
  private final Map<String, String> pathParameters;
  private final Map<String, List<String>> queryParameters;

  private RequestMethod requestMethod;
  private String uriString;
  private String path;

  public DefaultHttpRequest(byte[] body, HttpVersion httpVersion, Headers headers, DecodeResult decodeResult, HttpRequestSource source,
                            RequestMethod requestMethod, String uriString, String path, Map<String, String> pathParameters) {
    super(httpVersion, headers, decodeResult);
    this.body = body;
    this.source = source;
    this.requestMethod = requestMethod;
    this.uriString = uriString;
    this.path = path;
    this.pathParameters = pathParameters;
    this.queryParameters = new QueryStringDecoder(this.uri()).parameters();

    headers.get(HttpHeaderNames.COOKIE.toString()).ifPresent(header -> this.cookies(CookieCoder.decode(header)));
  }

  @Override
  public @NotNull HttpRequestSource source() {
    return this.source;
  }

  @Override
  public @NotNull RequestMethod requestMethod() {
    return this.requestMethod;
  }

  @Override
  public @NotNull DefaultHttpRequest requestMethod(@NotNull RequestMethod requestMethod) {
    this.requestMethod = requestMethod;
    return this;
  }

  @Override
  public @NotNull String uriString() {
    return this.uriString;
  }

  @Override
  public @NotNull URI uri() {
    return URI.create(this.uriString());
  }

  @Override
  public @NotNull DefaultHttpRequest uri(@NotNull String uri) {
    this.uriString = uri;
    return this;
  }

  @Override
  public @NotNull String path() {
    return this.path;
  }

  @Override
  public @NotNull DefaultHttpRequest path(@NotNull String path) {
    this.path = path;
    return this;
  }

  @Override
  public byte[] body() {
    return this.body;
  }

  @Override
  public @NotNull Map<String, String> pathParameters() {
    return this.pathParameters;
  }

  @Override
  public @NotNull Optional<String> pathParameter(@NotNull String name) {
    return Optional.ofNullable(this.pathParameters.get(name));
  }

  @Override
  public @NotNull String pathParameter(@NotNull String name, @NotNull String defaultValue) {
    return this.pathParameters.getOrDefault(name, defaultValue);
  }

  @Override
  public @NotNull DefaultHttpRequest setPathParameter(@NotNull String name, @NotNull String value) {
    this.pathParameters.put(name, value);
    return this;
  }

  @Override
  public @NotNull Map<String, List<String>> queryParameters() {
    return this.queryParameters;
  }

  @Override
  public @NotNull Optional<List<String>> queryParameter(@NotNull String name) {
    return Optional.ofNullable(this.queryParameters.get(name));
  }

  @Override
  public @NotNull List<String> queryParameter(@NotNull String name, @NotNull List<String> defaultValue) {
    return this.queryParameters.getOrDefault(name, defaultValue);
  }

  @Override
  public @NotNull DefaultHttpRequest setQueryParameter(@NotNull String name, @NotNull String value) {
    this.queryParameters.put(name, new ArrayList<>(Collections.singletonList(value)));
    return this;
  }

  @Override
  public @NotNull DefaultHttpRequest setQueryParameters(@NotNull String name, @NotNull String... value) {
    this.queryParameters.put(name, new ArrayList<>(Arrays.asList(value)));
    return this;
  }

  @Override
  public @NotNull DefaultHttpRequest self() {
    return this;
  }
}
