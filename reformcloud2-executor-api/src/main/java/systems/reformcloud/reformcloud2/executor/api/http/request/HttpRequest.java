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
package systems.reformcloud.reformcloud2.executor.api.http.request;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.http.cookie.CookieHolder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HttpRequest<T extends HttpRequest<T>> extends CookieHolder<T> {

    @NotNull
    HttpRequestSource source();

    @NotNull
    RequestMethod requestMethod();

    @NotNull
    T requestMethod(@NotNull RequestMethod requestMethod);

    @NotNull
    String uriString();

    @NotNull
    URI uri();

    @NotNull
    T uri(@NotNull String uri);

    @NotNull
    String path();

    @NotNull
    T path(@NotNull String path);

    @NotNull
    byte[] body();

    @NotNull
    Map<String, String> pathParameters();

    @NotNull
    Optional<String> pathParameter(@NotNull String name);

    @NotNull
    String pathParameter(@NotNull String name, @NotNull String defaultValue);

    @NotNull
    T setPathParameter(@NotNull String name, @NotNull String value);

    @NotNull
    Map<String, List<String>> queryParameters();

    @NotNull
    Optional<List<String>> queryParameter(@NotNull String name);

    @NotNull
    List<String> queryParameter(@NotNull String name, @NotNull List<String> defaultValue);

    @NotNull
    T setQueryParameter(@NotNull String name, @NotNull String value);

    @NotNull
    T setQueryParameters(@NotNull String name, @NotNull String... value);
}
