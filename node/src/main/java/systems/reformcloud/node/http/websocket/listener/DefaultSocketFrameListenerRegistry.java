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
package systems.reformcloud.node.http.websocket.listener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.http.listener.Priority;
import systems.reformcloud.http.websocket.SocketFrameType;
import systems.reformcloud.http.websocket.listener.FrameTypes;
import systems.reformcloud.http.websocket.listener.SocketFrameListener;
import systems.reformcloud.http.websocket.listener.SocketFrameListenerRegistry;
import systems.reformcloud.http.websocket.listener.SocketFrameListenerRegistryEntry;
import systems.reformcloud.http.websocket.request.RequestFrameHolder;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultSocketFrameListenerRegistry implements SocketFrameListenerRegistry {

  private static final Comparator<SocketFrameListenerRegistryEntry> PRIORITY_COMPARATOR = Comparator.comparingInt(SocketFrameListenerRegistryEntry::priority);
  private final List<SocketFrameListenerRegistryEntry> listeners = new CopyOnWriteArrayList<>();

  @Override
  public @NotNull SocketFrameListenerRegistry registerListeners(@NotNull SocketFrameListener... frameListeners) {
    try {
      for (SocketFrameListener listener : frameListeners) {
        Method handle = listener.getClass().getDeclaredMethod("handleFrame", RequestFrameHolder.class);

        Priority priority = handle.getAnnotation(Priority.class);
        int handlerPriority = priority == null ? 0 : priority.value();

        FrameTypes frameTypes = handle.getAnnotation(FrameTypes.class);
        SocketFrameType[] frames = frameTypes == null ? new SocketFrameType[0] : frameTypes.value();

        SocketFrameListenerRegistryEntry entry = new DefaultSocketFrameListenerRegistryEntry(listener, frames, handlerPriority);
        this.listeners.add(entry);
      }
    } catch (NoSuchMethodException exception) {
      throw new RuntimeException(exception);
    }

    this.listeners.sort(PRIORITY_COMPARATOR);
    return this;
  }

  @Override
  public @NotNull SocketFrameListenerRegistry unregisterListeners(@NotNull SocketFrameListener... frameListeners) {
    for (SocketFrameListener listener : frameListeners) {
      this.listeners.removeIf(entry -> entry.getListener().equals(listener));
    }

    return this;
  }

  @Override
  public @NotNull SocketFrameListenerRegistry unregisterListeners(@NotNull SocketFrameType frameType) {
    this.listeners.removeIf(listener -> Arrays.binarySearch(listener.getHandlingFrameTypes(), frameType) >= 0);
    return this;
  }

  @Override
  public @NotNull SocketFrameListenerRegistry clearListeners() {
    this.listeners.clear();
    return this;
  }

  @Override
  public @NotNull @UnmodifiableView List<SocketFrameListenerRegistryEntry> getListeners() {
    return this.listeners;
  }
}
