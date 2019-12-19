package systems.reformcloud.reformcloud2.executor.api.common.utility.thread;

import java.util.concurrent.TimeUnit;

public abstract class AbsoluteThread extends Thread {

  public AbsoluteThread enableDaemon() {
    setDaemon(true);
    return this;
  }

  public AbsoluteThread updatePriority(int priority) {
    setPriority(priority);
    return this;
  }

  public AbsoluteThread setExceptionHandler(UncaughtExceptionHandler handler) {
    setUncaughtExceptionHandler(handler);
    return this;
  }

  public AbsoluteThread updateClassLoader(ClassLoader classLoader) {
    setContextClassLoader(classLoader);
    return this;
  }

  public static void sleep(TimeUnit timeUnit, long time) {
    try {
      timeUnit.sleep(time);
    } catch (final InterruptedException ignored) {
    }
  }

  public static void sleep(long time) { sleep(TimeUnit.MILLISECONDS, time); }

  private volatile boolean interrupted = false;

  @Override
  public void interrupt() {
    interrupted = true;
    super.interrupt();
  }

  @Override
  public boolean isInterrupted() {
    return interrupted;
  }

  @Override public abstract void run();
}
