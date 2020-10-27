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
package systems.reformcloud.reformcloud2.node.http.websocket;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.CloseSocketFrame;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.ContinuationSocketFrame;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.SocketFrame;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.SocketFrameFactory;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.SocketFrameType;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.TextSocketFrame;

public final class DefaultSocketFrameFactory extends SocketFrameFactory {

    public static void init() {
        SocketFrameFactory.DEFAULT.set(new DefaultSocketFrameFactory());
    }

    private DefaultSocketFrameFactory() {
    }

    @Override
    public @NotNull SocketFrame<?> forType(@NotNull SocketFrameType frameType) {
        return new TypedSocketFrame(frameType, 0, true, new byte[0]);
    }

    @Override
    public @NotNull CloseSocketFrame<?> close(int status, @NotNull String statusText) {
        return new DefaultCloseSocketFrame(0, true, status, statusText);
    }

    @Override
    public @NotNull ContinuationSocketFrame<?> continuation(@NotNull String text) {
        return new DefaultContinuationSocketFrame(0, true, text);
    }

    @Override
    public @NotNull TextSocketFrame<?> text(@NotNull String text) {
        return new DefaultTextSocketFrame(0, true, text);
    }
}
