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
package systems.reformcloud.http.listener;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.http.reponse.ListeningHttpServerResponse;
import systems.reformcloud.http.request.HttpRequest;

/**
 * A listener which gets called when a http message was received. The priority this listener has
 * can be modified by adding a {@link Priority} annotation to the {@link #handleRequest(HttpRequest)}
 * method. By default the priority is {@code 0}. You may also specify which request methods are handled
 * by the handler by adding a {@link RequestMethods} annotation to the {@link #handleRequest(HttpRequest)}
 * method. By default only the {@code GET} requests are posted to a listener. A path of a listener can also
 * look like {@code /user/{id}/delete/{work_id}} which are then handled as path parameters. This means that
 * when a request is received the handler will decode the sent arguments. For example if the handler is
 * registered using {@code /user/{id}/delete/{work_id}} as path and the client tries to connect to
 * {@code /user/5/delete/hfG5} then the path parameter {@code id} will be {@code 5} and the parameter
 * {@code work_id} will be {@code hfG5}. These are accessible via {@link HttpRequest#pathParameter(String)}.
 *
 * @author derklaro
 * @see HttpListenerRegistry#registerListeners(String, HttpListener...)
 * @since 25. October 2020
 */
public interface HttpListener {

  /**
   * Handles the request and returns the answer to the request. The answer can directly be build
   * by invoking the {@link ListeningHttpServerResponse#response(HttpRequest)} method by using the
   * given {@code request} to build a response to the request. You may ensure that you are the last
   * listener handling the request by calling the {@link ListeningHttpServerResponse#lastHandler(boolean)}
   * method which will cancel the handle of all other listeners and sends the request returned by the
   * method. Note that you have to use {@link ListeningHttpServerResponse#closeAfterSent(boolean)} if you
   * are the last handling which provides the response, as the client waits for the remote connection to close
   * before handling the received result.
   *
   * @param request the request sent by the client.
   * @return the response to the request of the client, never {@code null}.
   */
  @NotNull
  ListeningHttpServerResponse<?> handleRequest(@NotNull HttpRequest<?> request);
}
