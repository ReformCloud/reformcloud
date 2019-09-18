package de.klaro.reformcloud2.executor.api.common.scheduler;

import java.util.concurrent.TimeUnit;

public interface TaskScheduler {

    void cancel(int id);

    void cancel(ScheduledTask scheduledTask);

    ScheduledTask runAsync(Runnable runnable);

    ScheduledTask schedule(Runnable runnable, long delay, TimeUnit timeUnit);

    ScheduledTask schedule(Runnable runnable, long delay, long period, TimeUnit timeUnit);
}
