package de.klaro.reformcloud2.executor.api.common.scheduler;

public interface ScheduledTask extends Runnable {

    int getID();

    Runnable getTask();

    void cancel();
}
