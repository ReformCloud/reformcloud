package systems.reformcloud.reformcloud2.executor.api.common.scheduler;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadFactory;

public interface ScheduledTask {

    /**
     * @return The id of the task
     */
    int getID();

    /**
     * @return The runnable of the task
     */
    @Nonnull
    Runnable getTask();

    /**
     * @return If the current task is running
     */
    boolean isRunning();

    /**
     * Cancels the execution of the current task
     */
    void cancel();

    @Nonnull
    default ThreadFactory newThreadFactory(int taskSize) {
        return r -> new Thread(r, String.format("Thread-Group-Loop-%d", taskSize));
    }
}
