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
package systems.reformcloud.reformcloud2.executor.api.http.websocket.request;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.http.request.HttpRequestSource;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.SocketFrame;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.listener.SocketFrameListenerRegistry;

import java.util.concurrent.Future;

/**
 * Represents an extended {@link HttpRequestSource} which has some more util methods
 * to work with web socket frames.
 *
 * @author derklaro
 * @since 27. October 2020
 */
public interface SocketFrameSource extends HttpRequestSource {

  /**
   * Sends a web socket frame to this source.
   *
   * @param socketFrame the frame to send.
   * @return a future completed when the I/O thread performed the write action.
   */
  @NotNull
  Future<Void> sendFrame(@NotNull SocketFrame<?> socketFrame);

  /**
   * Get the listener registry of the web frame listeners which are handled if sent by the client.
   *
   * @return the listener registry of the web frame listeners.
   */
  @NotNull
  SocketFrameListenerRegistry listenerRegistry();

  /**
   * Closes the connection to the client by sending a close message before
   * actually closing the channel.
   *
   * @param statusCode the close status reason code.
   * @param statusText the close status reason text or empty.
   */
  void close(int statusCode, @NotNull String statusText);

  /**
   * Closes the connection by sending a default {@code NORMAL_CLOSE} with the
   * status {@code 1000} and the message {@code bye}.
   */
  @Override void close();
}
