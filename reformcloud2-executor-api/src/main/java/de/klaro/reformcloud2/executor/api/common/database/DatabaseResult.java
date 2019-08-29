package de.klaro.reformcloud2.executor.api.common.database;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public abstract class DatabaseResult<T> extends CompletableFuture<T> {

    public abstract T getFullResult();

    public abstract void acceptAsync(Consumer<T> consumer, Consumer<Void> onComplete);

    public T getUninterruptedly() {
        try {
            return get();
        } catch (final InterruptedException | ExecutionException ex) {
            return null;
        }
    }

    public void awaitUninterruptedly() {
        try {
            get();
        } catch (final InterruptedException | ExecutionException ignored) {
        }
    }
}
