package de.klaro.reformcloud2.executor.api.common.utility.task;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class Task<V> extends CompletableFuture<V> {

    public abstract void awaitUninterruptedly();

    public abstract void awaitUninterruptedly(TimeUnit timeUnit, long time);

    public abstract V getUninterruptedly();

    public abstract V getUninterruptedly(TimeUnit timeUnit, long time);
}
