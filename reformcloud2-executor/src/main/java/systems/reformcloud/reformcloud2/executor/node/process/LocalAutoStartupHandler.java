package systems.reformcloud.reformcloud2.executor.node.process;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.SortedSet;
import java.util.TreeSet;

public class LocalAutoStartupHandler extends AbsoluteThread {

    public void doStart() {
        if (!isInterrupted()) {
            return;
        }

        updatePriority(Thread.MIN_PRIORITY).enableDaemon().start();
    }

    public void update() {
        perPriorityStartup.clear();
        System.gc();

        perPriorityStartup.addAll(NodeExecutor.getInstance().getClusterSyncManager().getProcessGroups());
    }

    private SortedSet<ProcessGroup> perPriorityStartup = new TreeSet<>((o1, o2) -> {
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
            if (!NodeExecutor.getInstance().getClusterSyncManager().isConnectedAndSyncWithCluster()) {
                AbsoluteThread.sleep(500);
                continue;
            }

            if (!NodeExecutor.getInstance().getNodeNetworkManager().getCluster().isSelfNodeHead()) {
                AbsoluteThread.sleep(2000);
                continue;
            }

            try {
                for (ProcessGroup processGroup : perPriorityStartup) {
                    int online = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getClusterManager()
                            .getOnlineAndWaiting(processGroup.getName());
                    if (processGroup.getStartupConfiguration().getMaxOnlineProcesses() != -1
                            && processGroup.getStartupConfiguration().getMaxOnlineProcesses() <= online) {
                        continue;
                    }

                    if (processGroup.getStartupConfiguration().getMinOnlineProcesses() < 0
                            || processGroup.getStartupConfiguration().getMinOnlineProcesses() <= online) {
                        continue;
                    }

                    ExecutorAPI.getInstance().startProcess(processGroup.getName());
                }
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
