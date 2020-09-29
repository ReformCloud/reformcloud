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
package systems.reformcloud.reformcloud2.executor.api.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.util.List;

public interface EventManager {

    @Nullable
    <T extends Event> T callEvent(@NotNull Class<? extends T> event);

    @NotNull
    <T extends Event> T callEvent(@NotNull T event);

    @NotNull
    <T extends Event> Task<T> callEventAsync(@NotNull Class<? extends T> event);

    @NotNull
    <T extends Event> Task<T> callEventAsync(@NotNull T event);

    void registerListener(@NotNull Object listener);

    void registerListener(@NotNull Class<?> listener);

    void unregisterListener(@NotNull Object listener);

    void unregisterAll();

    @NotNull
    @UnmodifiableView
    List<ListenerContainer> getListeners();
}
