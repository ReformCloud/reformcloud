package de.klaro.reformcloud2.executor.api.common.utility.task.defaults;

import de.klaro.reformcloud2.executor.api.common.utility.task.Task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class DefaultTask<V> extends Task<V> {

    @Override
    public void awaitUninterruptedly() {
        try {
            this.get();
        } catch (final InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void awaitUninterruptedly(TimeUnit timeUnit, long time) {
        try {
            this.get(time, timeUnit);
        } catch (final InterruptedException | ExecutionException | TimeoutException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public V getUninterruptedly() {
        try {
            return this.get();
        } catch (final InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public V getUninterruptedly(TimeUnit timeUnit, long time) {
        try {
            return this.get(time, timeUnit);
        } catch (final InterruptedException | ExecutionException | TimeoutException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
