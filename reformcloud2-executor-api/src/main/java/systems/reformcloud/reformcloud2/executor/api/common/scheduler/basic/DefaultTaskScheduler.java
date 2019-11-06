package systems.reformcloud.reformcloud2.executor.api.common.scheduler.basic;

import systems.reformcloud.reformcloud2.executor.api.common.scheduler.ScheduledTask;
import systems.reformcloud.reformcloud2.executor.api.common.scheduler.TaskScheduler;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class DefaultTaskScheduler implements TaskScheduler {

    private final AtomicInteger atomicInteger = new AtomicInteger();

    private final Map<Integer, ScheduledTask> tasks = new HashMap<>();

    private final Object object = new Object();

    @Override
    public void cancel(int id) {
        ScheduledTask task = tasks.remove(id);
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public void cancel(@Nonnull ScheduledTask scheduledTask) {
        scheduledTask.cancel();
    }

    @Nonnull
    @Override
    public ScheduledTask runAsync(@Nonnull Runnable runnable) {
        return schedule(runnable, 0, TimeUnit.MILLISECONDS);
    }

    @Nonnull
    @Override
    public ScheduledTask schedule(@Nonnull Runnable runnable, long delay, @Nonnull TimeUnit timeUnit) {
        return schedule(runnable, 0, delay, timeUnit);
    }

    @Nonnull
    @Override
    public ScheduledTask schedule(@Nonnull Runnable runnable, long delay, long period, @Nonnull TimeUnit timeUnit) {
        ScheduledTask scheduledTask = new DefaultTask(atomicInteger.getAndIncrement(), runnable, delay, period, timeUnit);
        synchronized (object) {
            tasks.put(scheduledTask.getID(), scheduledTask);
        }

        return scheduledTask;
    }
}
