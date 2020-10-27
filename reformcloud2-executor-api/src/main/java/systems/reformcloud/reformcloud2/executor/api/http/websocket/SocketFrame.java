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

public interface SocketFrame<T extends SocketFrame<T>> {

    @NotNull
    static SocketFrame<?> typedFrame(@NotNull SocketFrameType type) {
        return SocketFrameFactory.DEFAULT.get().forType(type);
    }

    @NotNull
    static CloseSocketFrame<?> closeFrame(int status, @NotNull String statusText) {
        return SocketFrameFactory.DEFAULT.get().close(status, statusText);
    }

    @NotNull
    static ContinuationSocketFrame<?> continuationFrame(@NotNull String text) {
        return SocketFrameFactory.DEFAULT.get().continuation(text);
    }

    @NotNull
    static TextSocketFrame<?> textFrame(@NotNull String text) {
        return SocketFrameFactory.DEFAULT.get().text(text);
    }

    @NotNull
    byte[] content();

    @NotNull
    T content(@NotNull byte[] content);

    boolean finalFragment();

    @NotNull
    T finalFragment(boolean finalFragment);

    int rsv();

    @NotNull
    T rsv(int rsv);

    @NotNull
    SocketFrameType type();
}
