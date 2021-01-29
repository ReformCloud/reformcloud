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
package systems.reformcloud.node.http.response;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.http.Headers;
import systems.reformcloud.http.HttpStatusCode;
import systems.reformcloud.http.HttpVersion;
import systems.reformcloud.http.decode.DecodeResult;
import systems.reformcloud.http.reponse.HttpServerResponse;
import systems.reformcloud.node.http.InstanceHolder;
import systems.reformcloud.node.http.cookie.DefaultCookieHolder;

public abstract class DefaultHttpServerResponse<T extends HttpServerResponse<T>> extends DefaultCookieHolder<T> implements HttpServerResponse<T>, InstanceHolder<T> {

  protected HttpStatusCode httpStatusCode = HttpStatusCode.OK;
  protected byte[] result = new byte[0];

  protected DefaultHttpServerResponse(HttpVersion httpVersion, Headers headers, DecodeResult decodeResult) {
    super(httpVersion, headers, decodeResult);
  }

  @Override
  public @NotNull HttpStatusCode status() {
    return this.httpStatusCode;
  }

  @Override
  public @NotNull T status(@NotNull HttpStatusCode httpStatusCode) {
    this.httpStatusCode = httpStatusCode;
    return this.self();
  }

  @Override
  public @NotNull T body(byte[] response) {
    this.result = response;
    return this.self();
  }

  @Override
  public byte[] body() {
    return this.result;
  }
}
