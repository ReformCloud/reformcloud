package de.klaro.reformcloud2.executor.api.common.scheduler.basic;

import de.klaro.reformcloud2.executor.api.common.scheduler.ScheduledTask;
import de.klaro.reformcloud2.executor.api.common.scheduler.TaskScheduler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DefaultTask implements ScheduledTask {

    public DefaultTask(TaskScheduler parent, int id, Runnable run, long delay, long period, TimeUnit timeUnit) {
        this.parent = parent;
        this.id = id;
        this.task = run;
        this.delay = timeUnit.toMillis(delay);
        this.period = timeUnit.toMillis(period);
    }

    private final AtomicBoolean running = new AtomicBoolean(true);

    private final TaskScheduler parent;

    private final int id;

    private final Runnable task;

    private final long delay;

    private final long period;

    @Override
    public int getID() {
        return id;
    }

    @Override
    public Runnable getTask() {
        return task;
    }

    @Override
    public void cancel() {
        boolean currentStatus = running.getAndSet(false);
        if (currentStatus) {
            parent.cancel0(this);
        }
    }

    @Override
    public void run() {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        while (running.get()) {
            try {
                task.run();
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }

            if (period <= 0) {
                break;
            }

            try {
                Thread.sleep(period);
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        cancel();
    }
}
