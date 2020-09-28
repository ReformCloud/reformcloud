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
package systems.reformcloud.reformcloud2.executor.api.task.defaults;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.task.exception.TaskCompletionException;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

public final class DefaultTask<V> extends Task<V> {

    private Consumer<TaskCompletionException> failureConsumer;

    @Override
    public V get(long timeout, TimeUnit unit) {
        try {
            return super.get(timeout, unit);
        } catch (final InterruptedException | ExecutionException | TimeoutException ex) {
            this.handleFailure(ex);
        }

        this.handleFailure(new NullPointerException("A task returned null as response"));
        return null;
    }

    @Override
    public V get() {
        try {
            return super.get();
        } catch (final InterruptedException | ExecutionException ex) {
            this.handleFailure(ex);
        }

        this.handleFailure(new NullPointerException("A task returned null as response"));
        return null;
    }

    @Override
    public void awaitUninterruptedly() {
        this.get();
    }

    @Override
    public void awaitUninterruptedly(@NotNull TimeUnit timeUnit, long time) {
        this.get(time, timeUnit);
    }

    @Override
    public V getUninterruptedly() {
        return this.get();
    }

    @Override
    public V getUninterruptedly(TimeUnit timeUnit, long time) {
        return this.get(time, timeUnit);
    }

    @NotNull
    @Override
    public Task<V> onFailure(@NotNull Consumer<TaskCompletionException> consumer) {
        this.failureConsumer = Objects.requireNonNull(consumer);
        return this;
    }

    @Override
    public @NotNull <U> Task<U> thenSupply(@NotNull Function<V, U> function) {
        Task<U> task = new DefaultTask<>();
        this.thenAccept(result -> task.complete(function.apply(result)));
        return task;
    }

    @Override
    public boolean completeExceptionally(Throwable ex) {
        boolean result = super.completeExceptionally(ex);
        this.handleFailure(ex);
        return result;
    }

    private void handleFailure(Throwable throwable) {
        if (this.failureConsumer != null) {
            this.failureConsumer.accept(new TaskCompletionException("A task raised an exception", throwable));
        }
    }
}
