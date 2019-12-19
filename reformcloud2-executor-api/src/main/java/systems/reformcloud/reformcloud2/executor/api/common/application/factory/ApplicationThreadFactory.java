package systems.reformcloud.reformcloud2.executor.api.common.application.factory;

import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnull;

public final class ApplicationThreadFactory implements ThreadFactory {

  public ApplicationThreadFactory(ThreadGroup threadGroup) {
    this.threadGroup = threadGroup;
  }

  private final ThreadGroup threadGroup;

  @Override
  public Thread newThread(@Nonnull Runnable r) {
    return new Thread(threadGroup, r);
  }
}
