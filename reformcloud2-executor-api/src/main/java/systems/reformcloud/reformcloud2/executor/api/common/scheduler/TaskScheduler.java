package systems.reformcloud.reformcloud2.executor.api.common.scheduler;

import systems.reformcloud.reformcloud2.executor.api.common.scheduler.basic.DefaultTaskScheduler;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public interface TaskScheduler {

    @Nonnull
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
    void cancel(@Nonnull ScheduledTask scheduledTask);

    /**
     * Runs a task async
     *
     * @param runnable The runnable which should get run
     * @return The created task
     */
    @Nonnull
    ScheduledTask runAsync(@Nonnull Runnable runnable);

    /**
     * Schedules a task
     *
     * @param runnable The runnable of the task
     * @param delay The delay between the executions
     * @param timeUnit The {@link TimeUnit} of the delay
     * @return The created task
     */
    @Nonnull
    ScheduledTask schedule(@Nonnull Runnable runnable, long delay, @Nonnull TimeUnit timeUnit);

    /**
     * Schedules a task
     *
     * @param runnable The runnable of the task
     * @param delay The delay between the executions
     * @param period The delay before the first execution
     * @param timeUnit The {@link TimeUnit} of the delay
     * @return The created task
     */
    @Nonnull
    ScheduledTask schedule(@Nonnull Runnable runnable, long delay, long period, @Nonnull TimeUnit timeUnit);
}
