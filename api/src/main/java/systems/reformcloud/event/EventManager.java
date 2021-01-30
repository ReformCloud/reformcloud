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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.task.Task;

import java.util.List;

/**
 * The event manager handling listeners and event executions.
 */
public interface EventManager {

  /**
   * Calls the given event to this event manager, constructing a new instance of it
   * using the provided NoArgsConstructor of the class. If no NoArgsConstructor is
   * present in the event class, this method will just return {@code null}.
   *
   * @param event The class of the event to call.
   * @param <T>   The type of the event.
   * @return The event or {@code null} if no NoArgsConstructor is present in the event class.
   */
  @Nullable <T extends Event> T callEvent(@NotNull Class<? extends T> event);

  /**
   * Calls the given event to this event manager.
   *
   * @param event The event to call.
   * @param <T>   The type of the event.
   * @return The same event as used for calling the method, for chaining.
   */
  @NotNull <T extends Event> T callEvent(@NotNull T event);

  /**
   * Calls the given event asynchronously to this event manager, constructing a new instance of
   * it using the provided NoArgsConstructor of the class. If no NoArgsConstructor is
   * present in the event class, this method will just return a task completed with {@code null}.
   *
   * @param event The class of the event to call.
   * @param <T>   The type of the event.
   * @return The event or a Task completed with {@code null} if no NoArgsConstructor is present in the event class.
   */
  @NotNull <T extends Event> Task<T> callEventAsync(@NotNull Class<? extends T> event);

  /**
   * Calls the given event asynchronously to this event manager.
   *
   * @param event The event to call.
   * @param <T>   The type of the event.
   * @return A task completed with the same event as used for calling the method, for chaining.
   */
  @NotNull <T extends Event> Task<T> callEventAsync(@NotNull T event);

  /**
   * Registers all methods annotated with {@code @Listener} to this event manager, the first and
   * only argument of the annotated method should be a non-abstract event class.
   *
   * @param listener The listener to register into this event manager.
   */
  void registerListener(@NotNull Object listener);

  /**
   * Registers the given class to this event manager using the provided NoArgsConstructor provided
   * by the {@code listener}. If there is no NoArgsConstructor a warning is printed and the method
   * does nothing. If the class was instantiated successfully the manager registers all methods
   * annotated with {@code @Listener}, the first and only argument of the annotated method
   * should be a non-abstract event class.
   *
   * @param listener The class of the listener to register.
   * @see #registerListener(Object)
   */
  void registerListener(@NotNull Class<?> listener);

  /**
   * Unregisters the specific {@code listener} from this event manager.
   *
   * @param listener The listener to unregister.
   */
  void unregisterListener(@NotNull Object listener);

  /**
   * Unregisters all listeners from this event manager instance.
   */
  void unregisterAll();

  /**
   * Get all listeners known to this instance.
   *
   * @return All listeners known to this instance.
   */
  @NotNull
  @UnmodifiableView
  List<ListenerContainer> getListeners();
}
