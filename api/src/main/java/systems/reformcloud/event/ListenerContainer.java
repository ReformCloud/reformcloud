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
package systems.reformcloud.event;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.event.priority.EventPriority;

import java.lang.reflect.InvocationTargetException;

/**
 * A container holding information about a listener registered to an {@link EventManager}.
 */
public interface ListenerContainer {

  /**
   * Get the instance of the wrapped listener.
   *
   * @return The instance of the wrapped listener.
   */
  @NotNull
  Object getListenerInstance();

  /**
   * Get the event class this listener is listening to.
   *
   * @return The event class this listener is listening to.
   */
  @NotNull
  Class<?> getTargetEventClass();

  /**
   * Get the priority of the listener.
   *
   * @return The priority of the listener.
   */
  @NotNull
  EventPriority getPriority();

  /**
   * Posts the {@code event} to the wrapped listener method.
   *
   * @param event The event to post to the method.
   * @throws InvocationTargetException if the underlying method throws an exception.
   * @throws IllegalAccessException    if the wrapped {@code Method} object is enforcing Java language access control and the underlying method is inaccessible.
   */
  void call(@NotNull Event event) throws InvocationTargetException, IllegalAccessException;
}
