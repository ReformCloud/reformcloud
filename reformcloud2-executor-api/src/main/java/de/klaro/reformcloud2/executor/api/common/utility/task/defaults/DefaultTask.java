package de.klaro.reformcloud2.executor.api.common.utility.task.defaults;

import de.klaro.reformcloud2.executor.api.common.utility.task.Task;
import de.klaro.reformcloud2.executor.api.common.utility.task.excpetion.TaskCompletionException;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public final class DefaultTask<V> extends Task<V> {

    private Consumer<TaskCompletionException> failureConsumer;

    /* ======================== */

    @Override
    public V get(long timeout, TimeUnit unit) {
        try {
            V result = super.get(timeout, unit);
            if (result == null) {
                handleFailure(new NullPointerException("A task returned null as response"));
            }

            return result;
        } catch (final InterruptedException | ExecutionException | TimeoutException ex) {
            handleFailure(ex);
        }

        handleFailure(new NullPointerException("A task returned null as response"));
        return null;
    }

    @Override
    public V get() {
        try {
            V result = super.get();
            if (result == null) {
                handleFailure(new NullPointerException("A task returned null as response"));
            }

            return result;
        } catch (final InterruptedException | ExecutionException ex) {
            handleFailure(ex);
        }

        handleFailure(new NullPointerException("A task returned null as response"));
        return null;
    }

    @Override
    public void awaitUninterruptedly() {
        this.get();
    }

    @Override
    public void awaitUninterruptedly(TimeUnit timeUnit, long time) {
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

    @Override
    public void exec() {
        Task.EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                awaitUninterruptedly();
            }
        });
    }

    @Override
    public Task<V> onFailure(Consumer<TaskCompletionException> consumer) {
        this.failureConsumer = Objects.requireNonNull(consumer);
        return this;
    }

    private void handleFailure(Throwable throwable) {
        if (failureConsumer != null) {
            failureConsumer.accept(new TaskCompletionException("A task raised an exception", throwable));
        }
    }
}
