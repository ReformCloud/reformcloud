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

/**
 * Represents a http request sent by a client to the HTTP server.
 *
 * @param <T> the type of the implementing api
 * @author derklaro
 * @since 25. October 2020
 */
public interface HttpRequest<T extends HttpRequest<T>> extends CookieHolder<T> {

    /**
     * Get the source of the server connection.
     *
     * @return the source of the server connection.
     */
    @NotNull
    HttpRequestSource source();

    /**
     * Get the request method the client is using.
     *
     * @return the request method the client is using.
     */
    @NotNull
    RequestMethod requestMethod();

    /**
     * Sets the request method the client is using
     *
     * @param requestMethod the request method to use
     * @return the same instance of this class, for chaining
     */
    @NotNull
    T requestMethod(@NotNull RequestMethod requestMethod);

    /**
     * Get the requested uri (or alternatively the path)
     *
     * @return the requester uri as string
     */
    @NotNull
    String uriString();

    /**
     * Parses the request uri to an uri object.
     *
     * @return the request uri as an object.
     * @throws IllegalArgumentException if the request uri violates RFC 2396
     */
    @NotNull
    URI uri();

    /**
     * Sets the request uri of the request without validating it (or alternatively the path)
     *
     * @param uri the new uri to use
     * @return the same instance of this class, for chaining
     */
    @NotNull
    T uri(@NotNull String uri);

    /**
     * Get the formatted request path
     *
     * @return the formatted request path
     */
    @NotNull
    String path();

    /**
     * Sets the request path. This doesn't have any impact when choosing the listeners for a path.
     *
     * @param path the new path to use.
     * @return the same instance of this class, for chaining
     */
    @NotNull
    T path(@NotNull String path);

    /**
     * Get the request body.
     *
     * @return the request body.
     */
    byte[] body();

    /**
     * Get the path parameters which were parsed based on the listener path and the connection uri.
     * For more information see {@link systems.reformcloud.reformcloud2.executor.api.http.listener.HttpListener}'s
     * documentation.
     *
     * @return the path parameters parsed from the request uri.
     */
    @NotNull
    Map<String, String> pathParameters();

    /**
     * Get a specific path parameter by it's name. This is case-sensitive.
     *
     * @param name the name of the parameter to get.
     * @return the parameter associated with the given {@code name} or an empty optional if the name isn't associated with a parameter.
     */
    @NotNull
    Optional<String> pathParameter(@NotNull String name);

    /**
     * Get a path parameter by returning the default value if the name is unknown.
     *
     * @param name         the name of the parameter to get.
     * @param defaultValue the default value returned when there is no parameter
     *                     associated with the given {@code name}.
     * @return the path parameter associated with the given {@code name} or the {@code defaultValue}
     */
    @NotNull
    String pathParameter(@NotNull String name, @NotNull String defaultValue);

    /**
     * Sets a path parameter, overriding the existing value if any exists.
     *
     * @param name  the name of the path parameter to set.
     * @param value the value of the path parameter to set.
     * @return the same instance of this class, for chaining
     */
    @NotNull
    T setPathParameter(@NotNull String name, @NotNull String value);

    /**
     * Get the query parameters sent by the client.
     *
     * @return the query parameters sent by the client.
     */
    @NotNull
    Map<String, List<String>> queryParameters();

    /**
     * Get all query parameters associated with the given {@code name}.
     *
     * @param name the name of the query parameters to get.
     * @return the query parameters associated with the given {@code name} or an empty optional
     */
    @NotNull
    Optional<List<String>> queryParameter(@NotNull String name);

    /**
     * Get all query parameters associated with the given {@code name}.
     *
     * @param name         the name of the query parameter to get.
     * @param defaultValue the query parameters if there are no parameter associated
     *                     with the given {@code name}.
     * @return the query parameters associated with the given {@code name} or {@code defaultValue}
     */
    @NotNull
    List<String> queryParameter(@NotNull String name, @NotNull List<String> defaultValue);

    /**
     * Sets the given query parameter in association with the given name.
     *
     * @param name  the name of the query parameter to set.
     * @param value the new value of the query parameter.
     * @return the same instance of this class, for chaining
     */
    @NotNull
    T setQueryParameter(@NotNull String name, @NotNull String value);

    /**
     * Sets the given query parameters in association with the given name.
     *
     * @param name  the name of the query parameters to set.
     * @param value the new value of the query parameter.
     * @return the same instance of this class, for chaining
     */
    @NotNull
    T setQueryParameters(@NotNull String name, @NotNull String... value);
}
