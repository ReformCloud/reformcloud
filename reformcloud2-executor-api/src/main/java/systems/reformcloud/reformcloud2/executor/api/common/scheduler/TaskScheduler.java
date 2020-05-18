/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

    /**
     * Shutdown the current scheduler and cancel all running tasks in it
     */
    void shutdown();
}
