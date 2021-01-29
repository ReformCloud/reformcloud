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
package systems.reformcloud.http.reponse;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.http.HttpStatusCode;
import systems.reformcloud.http.cookie.CookieHolder;

/**
 * Represents a server HTTP response.
 *
 * @param <T> the type of the implementing api
 * @author derklaro
 * @since 25. October 2020
 */
public interface HttpServerResponse<T extends HttpServerResponse<T>> extends CookieHolder<T> {

  /**
   * Get the response status.
   *
   * @return the response status.
   */
  @NotNull
  HttpStatusCode status();

  /**
   * Sets the response status.
   *
   * @param httpStatusCode the new response status.
   * @return the same instance of this class, for chaining
   */
  @NotNull
  T status(@NotNull HttpStatusCode httpStatusCode);

  /**
   * Get the response body sent to the client.
   *
   * @return the response body sent to the client.
   */
  byte[] body();

  /**
   * Sets the body of the result sent to the client.
   *
   * @param response the body response to send to the client.
   * @return the same instance of this class, for chaining
   */
  @NotNull
  T body(byte[] response);
}
