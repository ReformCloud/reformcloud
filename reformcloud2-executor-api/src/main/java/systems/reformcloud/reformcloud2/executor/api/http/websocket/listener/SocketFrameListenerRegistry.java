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
package systems.reformcloud.reformcloud2.executor.api.http.websocket.listener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.SocketFrameType;

import java.util.List;

/**
 * Represents a registry of {@link SocketFrameListener}s which are known.
 *
 * @author derklaro
 * @since 27. October 2020
 */
public interface SocketFrameListenerRegistry {

    /**
     * Registers all given listeners.
     *
     * @param frameListeners the listeners to register.
     * @return the same instance of this class, for chaining
     */
    @NotNull
    SocketFrameListenerRegistry registerListeners(@NotNull SocketFrameListener... frameListeners);

    /**
     * Unregisters all given listeners.
     *
     * @param frameListeners the listeners to unregister.
     * @return the same instance of this class, for chaining
     */
    @NotNull
    SocketFrameListenerRegistry unregisterListeners(@NotNull SocketFrameListener... frameListeners);

    /**
     * Unregisters all listeners listening to the given {@code frameType}.
     *
     * @param frameType the frame type of the listeners to unregister
     * @return the same instance of this class, for chaining
     */
    @NotNull
    SocketFrameListenerRegistry unregisterListeners(@NotNull SocketFrameType frameType);

    /**
     * Unregisters all known listeners.
     *
     * @return the same instance of this class, for chaining
     */
    @NotNull
    SocketFrameListenerRegistry clearListeners();

    /**
     * Get all known listeners to this registry
     *
     * @return all known listeners to this registry
     */
    @NotNull
    @UnmodifiableView
    List<SocketFrameListenerRegistryEntry> getListeners();
}
