package de.klaro.reformcloud2.executor.api.common.scheduler;

import java.util.concurrent.ThreadFactory;

public interface ScheduledTask {

    int getID();

    Runnable getTask();

    boolean isRunning();

    void cancel();

    default ThreadFactory newThreadFactory(int taskSize) {
        return r -> new Thread(r, String.format("Thread-Group-Loop-%o", taskSize));
    }
}
