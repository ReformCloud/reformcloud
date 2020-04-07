package systems.reformcloud.reformcloud2.executor.api.common.scheduler;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.scheduler.basic.DefaultTaskScheduler;

import java.util.concurrent.TimeUnit;

public interface TaskScheduler {

    @NotNull
    TaskScheduler INSTANCE = new DefaultTaskScheduler();

    /**
     * Cancels a specific task
     *
     * @param id The id of the task
     */
    void cancel(int id);

    /**
     * Cancels a specific {@link ScheduledTask}
     *
     * @param scheduledTask The task which should get cancelled
     */
    void cancel(@NotNull ScheduledTask scheduledTask);

    /**
     * Runs a task async
     *
     * @param runnable The runnable which should get run
     * @return The created task
     */
    @NotNull
    ScheduledTask runAsync(@NotNull Runnable runnable);

    /**
     * Schedules a task
     *
     * @param runnable The runnable of the task
     * @param delay    The delay between the executions
     * @param timeUnit The {@link TimeUnit} of the delay
     * @return The created task
     */
    @NotNull
    ScheduledTask schedule(@NotNull Runnable runnable, long delay, @NotNull TimeUnit timeUnit);

    /**
     * Schedules a task
     *
     * @param runnable The runnable of the task
     * @param delay    The delay between the executions
     * @param period   The delay before the first execution
     * @param timeUnit The {@link TimeUnit} of the delay
     * @return The created task
     */
    @NotNull
    ScheduledTask schedule(@NotNull Runnable runnable, long delay, long period, @NotNull TimeUnit timeUnit);
}
