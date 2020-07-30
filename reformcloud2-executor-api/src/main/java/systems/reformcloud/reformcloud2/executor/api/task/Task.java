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
package systems.reformcloud.reformcloud2.executor.api.task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.task.exception.TaskCompletionException;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Task<V> extends CompletableFuture<V> {

    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    /**
     * Creates a new already completed task
     *
     * @param value The value of the completed task
     * @param <U>   The object parameter of the task
     * @return The completed task
     */
    @NotNull
    public static <U> Task<U> completedTask(@Nullable U value) {
        Task<U> task = new DefaultTask<>();
        task.complete(value);
        return task;
    }

    /**
     * Supplies a task async
     *
     * @param callable The callable which should complete the task
     * @param <U>      The type of the object which should get returned
     * @return The task which gets completed async
     */
    @NotNull
    public static <U> Task<U> supply(@NotNull Callable<U> callable) {
        Task<U> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            try {
                task.complete(callable.call());
            } catch (final Exception ex) {
                task.completeExceptionally(ex);
            }
        });

        return task;
    }

    /**
     * Waits blocking on the current thread for a result until the thread interrupts
     */
    public abstract void awaitUninterruptedly();

    /**
     * Waits blocking on the current thread for a result until a timeout occurred
     *
     * @param timeUnit The time unit in which should get wait
     * @param time     The time until the timeout occurs
     */
    public abstract void awaitUninterruptedly(@NotNull TimeUnit timeUnit, long time);

    /**
     * Waits blocking on the current thread for a result until the thread interrupts
     *
     * @return The return value of the code
     */
    @Nullable
    public abstract V getUninterruptedly();

    /**
     * Waits blocking on the current thread for a result until a timeout occurred
     *
     * @param timeUnit The time unit in which should get wait
     * @param time     The time until the timeout occurs
     * @return The return value of the code
     */
    @Nullable
    public abstract V getUninterruptedly(TimeUnit timeUnit, long time);

    /**
     * This methods get called when the code executes successfully
     *
     * @param consumer The callback which get called with the response of the task
     * @return The current instance of this class
     */
    @NotNull
    public Task<V> onComplete(@NotNull Consumer<V> consumer) {
        this.thenAccept(consumer);
        return this;
    }

    /**
     * This methods get called when the code execute fails
     *
     * @param consumer The callback which will handle the exception which occurred
     * @return The current instance of this class
     */
    @NotNull
    public abstract Task<V> onFailure(@NotNull Consumer<TaskCompletionException> consumer);

    @NotNull
    public abstract <U> Task<U> thenSupply(@NotNull Function<V, U> function);
}
