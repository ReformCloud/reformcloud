package de.klaro.reformcloud2.executor.api.common.scheduler;

import java.util.concurrent.ThreadFactory;

public interface ScheduledTask extends Runnable {

    int getID();

    Runnable getTask();

    boolean isRunning();

    void cancel();

    default ThreadFactory newThreadFactory(int taskSize) {
        return r -> new Thread(new ThreadGroup(String.format("Task-%o", taskSize)), r);
    }
}
