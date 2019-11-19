package systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults;

import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.excepetion.TaskCompletionException;

import javax.annotation.Nonnull;
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
            return super.get(timeout, unit);
        } catch (final InterruptedException | ExecutionException | TimeoutException ex) {
            handleFailure(ex);
        }

        handleFailure(new NullPointerException("A task returned null as response"));
        return null;
    }

    @Override
    public V get() {
        try {
            return super.get();
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
    public void awaitUninterruptedly(@Nonnull TimeUnit timeUnit, long time) {
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
        Task.EXECUTOR.execute(this::awaitUninterruptedly);
    }

    @Nonnull
    @Override
    public Task<V> onFailure(@Nonnull Consumer<TaskCompletionException> consumer) {
        this.failureConsumer = Objects.requireNonNull(consumer);
        return this;
    }

    private void handleFailure(Throwable throwable) {
        if (failureConsumer != null) {
            failureConsumer.accept(new TaskCompletionException("A task raised an exception", throwable));
        }
    }
}
