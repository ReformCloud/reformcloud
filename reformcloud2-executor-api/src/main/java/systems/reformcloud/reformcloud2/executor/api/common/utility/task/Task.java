package systems.reformcloud.reformcloud2.executor.api.common.utility.task;

import systems.reformcloud.reformcloud2.executor.api.common.utility.task.excepetion.TaskCompletionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class Task<V> extends CompletableFuture<V> {

    public static final Executor EXECUTOR = Executors.newCachedThreadPool();

    /**
     * Waits blocking on the current thread for a result until the thread interrupts
     */
    public abstract void awaitUninterruptedly();

    /**
     * Waits blocking on the current thread for a result until a timeout occurred
     *
     * @param timeUnit The time unit in which should get wait
     * @param time The time until the timeout occurs
     */
    public abstract void awaitUninterruptedly(@Nonnull TimeUnit timeUnit, long time);

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
     * @param time The time until the timeout occurs
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
    @Nonnull
    public Task<V> onComplete(@Nonnull Consumer<V> consumer) {
        thenAccept(consumer);
        return this;
    }

    /**
     * This methods get called when the code execute fails
     *
     * @param consumer The callback which will handle the exception which occurred
     * @return The current instance of this class
     */
    @Nonnull
    public abstract Task<V> onFailure(@Nonnull Consumer<TaskCompletionException> consumer);
}
