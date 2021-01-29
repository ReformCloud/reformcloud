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
package systems.reformcloud.node.http.listener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.http.listener.HttpListener;
import systems.reformcloud.http.listener.HttpListenerRegistry;
import systems.reformcloud.http.listener.HttpListenerRegistryEntry;
import systems.reformcloud.http.listener.Priority;
import systems.reformcloud.http.listener.RequestMethods;
import systems.reformcloud.http.request.HttpRequest;
import systems.reformcloud.http.request.RequestMethod;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultHttpListenerRegistry implements HttpListenerRegistry {

  private static final Comparator<HttpListenerRegistryEntry> PRIORITY_COMPARATOR = Comparator.comparingInt(HttpListenerRegistryEntry::priority);
  private final Map<String, List<HttpListenerRegistryEntry>> listeners = new ConcurrentHashMap<>();

  @Override
  public @NotNull HttpListenerRegistry registerListeners(@NotNull String path, @NotNull HttpListener... httpListeners) {
    try {
      for (HttpListener httpListener : httpListeners) {
        Method handle = httpListener.getClass().getDeclaredMethod("handleRequest", HttpRequest.class);

        Priority priority = handle.getAnnotation(Priority.class);
        int handlerPriority = priority == null ? 0 : priority.value();

        RequestMethods requestMethods = handle.getAnnotation(RequestMethods.class);
        RequestMethod[] methods = requestMethods == null ? new RequestMethod[]{RequestMethod.GET} : requestMethods.value();

        HttpListenerRegistryEntry entry = new DefaultHttpListenerRegistryEntry(httpListener, methods, handlerPriority);
        if (!this.listeners.containsKey(path)) {
          this.listeners.put(path, new CopyOnWriteArrayList<>());
        }

        this.listeners.get(path).add(entry);
      }
    } catch (NoSuchMethodException exception) {
      throw new RuntimeException(exception);
    }

    if (this.listeners.containsKey(path)) {
      // if the listeners array was empty no listeners were registered
      this.listeners.get(path).sort(PRIORITY_COMPARATOR);
    }

    return this;
  }

  @Override
  public @NotNull HttpListenerRegistry unregisterListeners(@NotNull HttpListener... httpListener) {
    for (List<HttpListenerRegistryEntry> value : this.listeners.values()) {
      for (HttpListener listener : httpListener) {
        value.removeIf(entry -> entry.getListener().equals(listener));
      }
    }

    return this;
  }

  @Override
  public @NotNull HttpListenerRegistry unregisterListeners(@NotNull String path) {
    this.listeners.remove(path);
    return this;
  }

  @Override
  public @NotNull HttpListenerRegistry clearListeners() {
    this.listeners.clear();
    return this;
  }

  @Override public @NotNull @UnmodifiableView Map<String, List<HttpListenerRegistryEntry>> getListeners() {
    return this.listeners;
  }
}
