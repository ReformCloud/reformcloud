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
package systems.reformcloud.reformcloud2.executor.api.common.scheduler.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.scheduler.ScheduledTask;
import systems.reformcloud.reformcloud2.executor.api.common.scheduler.TaskScheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class DefaultTaskScheduler implements TaskScheduler {

    private final AtomicInteger atomicInteger = new AtomicInteger();

    private final Map<Integer, ScheduledTask> tasks = new ConcurrentHashMap<>();

    @Override
    public void cancel(int id) {
        ScheduledTask task = this.tasks.remove(id);
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public void cancel(@NotNull ScheduledTask scheduledTask) {
        scheduledTask.cancel();
    }

    @NotNull
    @Override
    public ScheduledTask runAsync(@NotNull Runnable runnable) {
        return this.schedule(runnable, 0, TimeUnit.MILLISECONDS);
    }

    @NotNull
    @Override
    public ScheduledTask schedule(@NotNull Runnable runnable, long delay, @NotNull TimeUnit timeUnit) {
        return this.schedule(runnable, 0, delay, timeUnit);
    }

    @NotNull
    @Override
    public ScheduledTask schedule(@NotNull Runnable runnable, long delay, long period, @NotNull TimeUnit timeUnit) {
        ScheduledTask scheduledTask = new DefaultTask(this.atomicInteger.getAndIncrement(), runnable, delay, period, timeUnit);
        this.tasks.put(scheduledTask.getId(), scheduledTask);
        return scheduledTask;
    }

    @Override
    public void shutdown() {
        for (ScheduledTask value : this.tasks.values()) {
            value.cancel();
        }

        this.tasks.clear();
    }
}
