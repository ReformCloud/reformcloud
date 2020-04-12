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
        ScheduledTask task = tasks.remove(id);
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
        return schedule(runnable, 0, TimeUnit.MILLISECONDS);
    }

    @NotNull
    @Override
    public ScheduledTask schedule(@NotNull Runnable runnable, long delay, @NotNull TimeUnit timeUnit) {
        return schedule(runnable, 0, delay, timeUnit);
    }

    @NotNull
    @Override
    public ScheduledTask schedule(@NotNull Runnable runnable, long delay, long period, @NotNull TimeUnit timeUnit) {
        ScheduledTask scheduledTask = new DefaultTask(atomicInteger.getAndIncrement(), runnable, delay, period, timeUnit);
        tasks.put(scheduledTask.getId(), scheduledTask);
        return scheduledTask;
    }
}
