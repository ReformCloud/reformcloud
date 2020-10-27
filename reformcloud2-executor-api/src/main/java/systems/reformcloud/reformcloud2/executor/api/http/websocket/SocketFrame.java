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
package systems.reformcloud.reformcloud2.executor.api.http.websocket;

import org.jetbrains.annotations.NotNull;

/**
 * The super class of all web socket frames. There are (listed below) implementations of
 * a web socket frame which holds more data than a normal frame.
 *
 * @param <T> the type of the implementing api
 * @author derklaro
 * @see CloseSocketFrame
 * @see ContinuationSocketFrame
 * @see TextSocketFrame
 * @since 27. October 2020
 */
public interface SocketFrame<T extends SocketFrame<T>> {

    /**
     * Creates a new socket frame for the given type. The returned implementation
     * is not a sub type of for example a {@link CloseSocketFrame} even if as the
     * {@code type} {@link SocketFrameType#CLOSE} is provided. For specific
     * implementation use {@link #closeFrame(int, String)}, {@link #continuationFrame(String)}
     * and {@link #textFrame(String)}.
     *
     * @param type the type of socket frame to create.
     * @return the created socket frame
     */
    @NotNull
    static SocketFrame<?> typedFrame(@NotNull SocketFrameType type) {
        return SocketFrameFactory.DEFAULT.get().forType(type);
    }

    /**
     * Creates a new close socket frame.
     *
     * @param status     the status code why the connection was closed.
     * @param statusText the reason text why the connection was closed or empty.
     * @return the created close socket frame.
     */
    @NotNull
    static CloseSocketFrame<?> closeFrame(int status, @NotNull String statusText) {
        return SocketFrameFactory.DEFAULT.get().close(status, statusText);
    }

    /**
     * Creates a new continuation frame.
     *
     * @param text the text of the frame or empty if a binary continuation frame.
     * @return the created continuation frame.
     */
    @NotNull
    static ContinuationSocketFrame<?> continuationFrame(@NotNull String text) {
        return SocketFrameFactory.DEFAULT.get().continuation(text);
    }

    /**
     * Creates a new text frame.
     *
     * @param text the text data of the frame
     * @return the created text socket frame
     */
    @NotNull
    static TextSocketFrame<?> textFrame(@NotNull String text) {
        return SocketFrameFactory.DEFAULT.get().text(text);
    }

    /**
     * Get the content of the web socket frame.
     *
     * @return the content of the web socket frame.
     */
    @NotNull
    byte[] content();

    /**
     * Sets the content of the web socket frame.
     *
     * @param content the content of the web socket frame.
     * @return the same instance of this class, for chaining
     */
    @NotNull
    T content(@NotNull byte[] content);

    /**
     * Get if this frame is the last fragment of a web socket framed message. The first
     * fragment may also be the last fragment.
     *
     * @return if the fragment is the last fragment of a framed message.
     */
    boolean finalFragment();

    /**
     * Set if this frame is the last fragment of a web socket framed message. The first
     * fragment may also be the last fragment.
     *
     * @param finalFragment if this frame is the last fragment of the framed message.
     * @return the same instance of this class, for chaining
     */
    @NotNull
    T finalFragment(boolean finalFragment);

    /**
     * Must be 0 unless an extension is negotiated that defines meanings
     * for non-zero values. If a nonzero value is received and none of
     * the negotiated extensions defines the meaning of such a nonzero
     * value, the receiving endpoint must fail the WebSocket
     * connection.
     *
     * @return the bits used for extensions to the standard.
     */
    int rsv();

    /**
     * Sets the bits used for extensions to the standard.
     *
     * @param rsv the bits used for extensions to the standard.
     * @return the same instance of the class, for chaining
     */
    @NotNull
    T rsv(int rsv);

    /**
     * Get the type of the frame sent by the server.
     *
     * @return the type of the frame sent by the server.
     */
    @NotNull
    SocketFrameType type();
}
