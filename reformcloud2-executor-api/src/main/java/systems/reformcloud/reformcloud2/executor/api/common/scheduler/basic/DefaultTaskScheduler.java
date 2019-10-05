package systems.reformcloud.reformcloud2.executor.api.common.scheduler.basic;

import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.scheduler.ScheduledTask;
import systems.reformcloud.reformcloud2.executor.api.common.scheduler.TaskScheduler;

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
    public void cancel(ScheduledTask scheduledTask) {
        scheduledTask.cancel();
    }

    @Override
    public ScheduledTask runAsync(Runnable runnable) {
        return schedule(runnable, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledTask schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        return schedule(runnable, delay, 0, timeUnit);
    }

    @Override
    public ScheduledTask schedule(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        Conditions.isTrue(runnable != null);
        Conditions.isTrue(timeUnit != null);

        ScheduledTask scheduledTask = new DefaultTask(atomicInteger.getAndIncrement(), runnable, delay, period, timeUnit);
        synchronized (object) {
            tasks.put(scheduledTask.getID(), scheduledTask);
        }

        return scheduledTask;
    }
}
