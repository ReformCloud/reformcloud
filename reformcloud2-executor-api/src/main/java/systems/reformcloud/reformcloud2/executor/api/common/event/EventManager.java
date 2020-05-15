/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.executor.api.common.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.List;

public interface EventManager {

    /**
     * Calls an event
     *
     * @param event The class of the event which get instantiated and then called
     * @see #callEvent(Event)
     */
    @Nullable
    <T extends Event> T callEvent(@NotNull Class<? extends T> event);

    /**
     * Calls an event
     *
     * @param event The event which should be called
     */
    @NotNull
    <T extends Event> T callEvent(@NotNull T event);

    /**
     * Calls an event async
     *
     * @param event The class of the event which get instantiated and then called
     * @see #callEventAsync(Event)
     */
    @NotNull
    <T extends Event> Task<T> callEventAsync(@NotNull Class<? extends T> event);

    /**
     * Calls an event async
     *
     * @param event The event which should be called
     */
    @NotNull
    <T extends Event> Task<T> callEventAsync(@NotNull T event);

    /**
     * Registers a event listener
     *
     * @param listener The listener which should get registered
     */
    void registerListener(@NotNull Object listener);

    /**
     * Registers a listener
     *
     * @param listener The listener class which will get instantiated and then registered
     * @see #registerListener(Object)
     */
    void registerListener(@NotNull Class<?> listener);

    /**
     * Unregisters a specific listener
     *
     * @param listener The listener which should get unregistered
     */
    void unregisterListener(@NotNull Object listener);

    /**
     * Unregisters all listeners
     */
    void unregisterAll();

    /**
     * @return All registered listeners
     */
    @NotNull
    @UnmodifiableView
    List<ListenerContainer> getListeners();
}
