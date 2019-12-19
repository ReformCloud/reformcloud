package systems.reformcloud.reformcloud2.executor.node.process;

import java.util.SortedSet;
import java.util.TreeSet;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

public class LocalAutoStartupHandler extends AbsoluteThread {

  public void doStart() {
    updatePriority(Thread.MIN_PRIORITY).enableDaemon().start();
  }

  public void update() {
    perPriorityStartup.clear();
    perPriorityStartup.addAll(
        NodeExecutor.getInstance().getClusterSyncManager().getProcessGroups());
  }

  private final SortedSet<ProcessGroup> perPriorityStartup =
      new TreeSet<>((o1, o2) -> {
        int o1Priority = o1.getStartupConfiguration().getStartupPriority();
        int o2Priority = o2.getStartupConfiguration().getStartupPriority();

        if (o1Priority <= o2Priority) {
          return -1;
        }

        return 1;
      });

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      if (!NodeExecutor.getInstance()
               .getClusterSyncManager()
               .isConnectedAndSyncWithCluster()) {
        AbsoluteThread.sleep(500);
        continue;
      }

      if (!NodeExecutor.getInstance()
               .getNodeNetworkManager()
               .getCluster()
               .isSelfNodeHead()) {
        AbsoluteThread.sleep(2000);
        continue;
      }

      try {
        for (ProcessGroup processGroup :
             Links.copySortedSet(perPriorityStartup)) {
          int online = NodeExecutor.getInstance()
                           .getNodeNetworkManager()
                           .getCluster()
                           .getClusterManager()
                           .getOnlineAndWaiting(processGroup.getName());
          if (processGroup.getStartupConfiguration().getMaxOnlineProcesses() !=
                  -1 &&
              processGroup.getStartupConfiguration().getMaxOnlineProcesses() <=
                  online) {
            continue;
          }

          if (processGroup.getStartupConfiguration().getMinOnlineProcesses() <
                  0 ||
              processGroup.getStartupConfiguration().getMinOnlineProcesses() <=
                  online) {
            continue;
          }

          ExecutorAPI.getInstance()
              .getSyncAPI()
              .getProcessSyncAPI()
              .startProcess(processGroup.getName());
          AbsoluteThread.sleep(100);
        }
      } catch (final Throwable throwable) {
        throwable.printStackTrace();
      }

      AbsoluteThread.sleep(50);
    }
  }
}
