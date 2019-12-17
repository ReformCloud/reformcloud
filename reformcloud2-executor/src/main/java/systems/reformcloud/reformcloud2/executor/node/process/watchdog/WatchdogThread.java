package systems.reformcloud.reformcloud2.executor.node.process.watchdog;

import java.util.concurrent.TimeUnit;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

public final class WatchdogThread extends AbsoluteThread {

  public WatchdogThread() {
    updatePriority(Thread.MIN_PRIORITY).enableDaemon().start();
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      Links.newList(LocalProcessManager.getNodeProcesses())
          .forEach(runningProcess -> {
            if (!runningProcess.running() &&
                runningProcess.getStartupTime() != -1 &&
                runningProcess.getStartupTime() +
                        TimeUnit.SECONDS.toMillis(30) <
                    System.currentTimeMillis()) {
              runningProcess.shutdown();
            }
          });

      AbsoluteThread.sleep(TimeUnit.SECONDS, 1);
    }
  }
}
