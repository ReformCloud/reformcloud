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
package systems.reformcloud.http.websocket.listener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.http.listener.Priority;
import systems.reformcloud.http.websocket.SocketFrame;
import systems.reformcloud.http.websocket.request.RequestFrameHolder;
import systems.reformcloud.http.websocket.response.ResponseFrameHolder;

/**
 * A listener which handles socket frames sent by a client to the server. The priority
 * this listener has compared to other can be set by adding a {@link Priority}
 * annotation to the {@link #handleFrame(RequestFrameHolder)} method. By default this
 * priority will be {@code 0}. You may also specify the frame types this handler
 * handles by adding a {@link FrameTypes} annotation to the {@link #handleFrame(RequestFrameHolder)}
 * method. By default the handler handles all frame types sent by the client.
 *
 * @author derklaro
 * @see SocketFrameListenerRegistry#registerListeners(SocketFrameListener...)
 * @since 27. October 2020
 */
public interface SocketFrameListener {

  /**
   * Handles a web socket frame and can be casted to the correct type after receiving. The answer
   * (if there is one) to the message can be build by using {@link ResponseFrameHolder#response(SocketFrame)}
   * which creates a new {@link ResponseFrameHolder} holding the socket frame of the response for example
   * created by {@link SocketFrame#textFrame(String)} and some listener information which can change the
   * way how the next listeners are called/the connection is handled. The invocation of {@link ResponseFrameHolder#closeAfterSent(boolean)}
   * causes the connection to be closed after sending the response to the message. The invocation of
   * {@link ResponseFrameHolder#lastHandler(boolean)} causes that the handler is the last handler and afterwards
   * no more listeners are called.
   *
   * @param frame the received frame from the client.
   * @return the response to the received frame or {@code null} if there is no response.
   */
  @Nullable
  ResponseFrameHolder<?> handleFrame(@NotNull RequestFrameHolder frame);
}
