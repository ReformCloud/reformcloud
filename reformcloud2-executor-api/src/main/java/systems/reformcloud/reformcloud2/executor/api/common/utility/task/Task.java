package systems.reformcloud.reformcloud2.executor.api.common.utility.task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.excepetion.TaskCompletionException;

import java.util.concurrent.*;
import java.util.function.Consumer;

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
        thenAccept(consumer);
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
}
