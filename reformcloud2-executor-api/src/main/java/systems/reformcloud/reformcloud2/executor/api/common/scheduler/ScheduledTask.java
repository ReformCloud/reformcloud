package systems.reformcloud.reformcloud2.executor.api.common.scheduler;

import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnull;

/**
 * Represents any task scheduled by an {@link TaskScheduler} instance
 */
public interface ScheduledTask {

  /**
   * @return The id of the task
   */
  int getID();

  /**
   * @return The runnable of the task
   */
  @Nonnull Runnable getTask();

  /**
   * @return If the current task is running
   */
  boolean isRunning();

  /**
   * Cancels the execution of the current task
   */
  void cancel();

  /**
   * Creates a new thread factory
   *
   * @param id The id of the new thread
   * @return A new thread factory
   */
  @Nonnull
  default ThreadFactory newThreadFactory(int id) {
    return r -> new Thread(r, String.format("Thread-Group-Loop-%d", id));
  }
}
