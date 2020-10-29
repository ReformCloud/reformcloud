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
import systems.reformcloud.reformcloud2.executor.api.http.decode.DecodeResultHolder;

/**
 * Represents some information about the HTTP request.
 *
 * @param <T> the type of the implementing api
 * @author derklaro
 * @since 25. October 2020
 */
public interface HttpInformation<T extends HttpInformation<T>> extends DecodeResultHolder<T> {

    /**
     * Get the http version of this information.
     *
     * @return the http version of this information.
     */
    @NotNull
    HttpVersion httpVersion();

    /**
     * Sets the http version of this information.
     *
     * @param httpVersion the http version of this information.
     * @return the same instance of this class, for chaining
     */
    @NotNull
    T httpVersion(@NotNull HttpVersion httpVersion);

    /**
     * Get the http headers of this information.
     *
     * @return the http headers of this information.
     */
    @NotNull
    Headers headers();
}
