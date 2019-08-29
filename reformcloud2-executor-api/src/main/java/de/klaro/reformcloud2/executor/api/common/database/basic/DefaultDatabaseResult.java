package de.klaro.reformcloud2.executor.api.common.database.basic;

import de.klaro.reformcloud2.executor.api.common.database.DatabaseResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class DefaultDatabaseResult<T> extends DatabaseResult<T> {

    @Override
    public T getFullResult() {
        return getUninterruptedly();
    }

    @Override
    public void acceptAsync(Consumer<T> consumer, Consumer<Void> onComplete) {
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                consumer.accept(getUninterruptedly());
            }
        }).thenAccept(onComplete);
    }
}
