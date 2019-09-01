package de.klaro.reformcloud2.executor.api.common.utility.task;

import de.klaro.reformcloud2.executor.api.common.utility.task.excpetion.TaskCompletionException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class Task<V> extends CompletableFuture<V> {

    public static final Executor EXECUTOR = Executors.newCachedThreadPool();

    public abstract void awaitUninterruptedly();

    public abstract void awaitUninterruptedly(TimeUnit timeUnit, long time);

    public abstract V getUninterruptedly();

    public abstract V getUninterruptedly(TimeUnit timeUnit, long time);

    public abstract void exec();

    public Task<V> onComplete(Consumer<V> consumer) {
        thenAccept(consumer);
        return this;
    }

    public abstract Task<V> onFailure(Consumer<TaskCompletionException> consumer);
}
