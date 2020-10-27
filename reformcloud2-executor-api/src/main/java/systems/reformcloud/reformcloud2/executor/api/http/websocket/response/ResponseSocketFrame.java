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
package systems.reformcloud.reformcloud2.executor.api.http.websocket.response;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.SocketFrame;

/**
 * Represents a response to a frame sent to the server holding the actual
 * response and some more information how to interact with the next listeners
 * and the network connection to the client.
 *
 * @param <T> the type of the implementing api
 * @author derklaro
 * @since 27. October 2020
 */
public interface ResponseSocketFrame<T extends ResponseSocketFrame<T>> {

    /**
     * Creates a new response which uses as message the given socket frame.
     *
     * @param responseFrame the response frame which should be sent to the client.
     * @return the created response frame.
     */
    @Contract(value = "_ -> new", pure = true)
    static @NotNull ResponseSocketFrame<?> response(@NotNull SocketFrame<?> responseFrame) {
        return ResponseSocketFrameFactory.DEFAULT.get().forFrame(responseFrame);
    }

    /**
     * Get the response which should be sent to the client.
     *
     * @return the response which should be sent to the client.
     */
    @NotNull
    SocketFrame<?> response();

    /**
     * Get if this response is the last handled and this response is directly sent.
     *
     * @return if this response is the last handled.
     */
    boolean lastHandler();

    /**
     * Sets if this response is the last handled and this response is directly sent.
     *
     * @param lastHandler if the handler is the last handled.
     * @return the same instance of this class, for chaining
     */
    @NotNull
    T lastHandler(boolean lastHandler);

    /**
     * Get if the connection to the client should be closed after the send to the client.
     *
     * @return if the connection to the client should be closed after the send to the client.
     */
    boolean closeAfterSent();

    /**
     * Sets if the connection to the client should be closed after the send to the client.
     *
     * @param close if the connection to the client should be closed after the send to the client.
     * @return the same instance of this class, for chaining
     */
    @NotNull
    T closeAfterSent(boolean close);
}
