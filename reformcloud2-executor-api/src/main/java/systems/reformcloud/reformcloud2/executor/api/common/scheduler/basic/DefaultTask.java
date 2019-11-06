package systems.reformcloud.reformcloud2.executor.api.common.scheduler.basic;

import systems.reformcloud.reformcloud2.executor.api.common.scheduler.ScheduledTask;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DefaultTask implements ScheduledTask {

    public DefaultTask(int id, Runnable run, long delay, long period, TimeUnit timeUnit) {
        this.id = id;
        this.task = run;
        this.executorService = Executors.newSingleThreadScheduledExecutor(newThreadFactory(id));

        this.executorService.scheduleAtFixedRate(this.task, delay, period, timeUnit);
    }

    private final AtomicBoolean running = new AtomicBoolean(true);

    private final ScheduledExecutorService executorService;

    private final int id;

    private final Runnable task;

    @Override
    public int getID() {
        return id;
    }

    @Nonnull
    @Override
    public Runnable getTask() {
        return task;
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void cancel() {
        if (executorService.isTerminated() || executorService.isShutdown()) {
            return;
        }

        executorService.shutdownNow();
    }
}
