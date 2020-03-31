package systems.reformcloud.reformcloud2.executor.node.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class LocalAutoStartupHandler extends AbsoluteThread {

    public void doStart() {
        updatePriority(Thread.MIN_PRIORITY).enableDaemon().start();
    }

    public void update() {
        perPriorityStartup.clear();
        perPriorityStartup.addAll(NodeExecutor.getInstance().getClusterSyncManager().getProcessGroups());
    }

    private final SortedSet<ProcessGroup> perPriorityStartup = new ConcurrentSkipListSet<>((o1, o2) -> {
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

            handleProcessStarts();
            handleProcessPrepare();
            AbsoluteThread.sleep(50);
        }
    }

    private void handleProcessPrepare() {
        for (ProcessGroup processGroup : Streams.copySortedSet(perPriorityStartup)) {
            List<ProcessInformation> preparedProcesses = this.getPreparedProcesses(processGroup.getName());

            int total = preparedProcesses.size() + NodeExecutor
                    .getInstance()
                    .getNodeNetworkManager()
                    .getRegisteredProcesses()
                    .size(processGroup.getName());

            if (total < processGroup.getStartupConfiguration().getAlwaysPreparedProcesses()) {
                System.out.println(LanguageManager.get("process-preparing-new-process", processGroup.getName()));
                ExecutorAPI.getInstance()
                        .getAsyncAPI()
                        .getProcessAsyncAPI()
                        .prepareProcessAsync(processGroup.getName())
                        .awaitUninterruptedly();
            }
        }
    }

    private void handleProcessStarts() {
        try {
            for (ProcessGroup processGroup : Streams.copySortedSet(perPriorityStartup)) {
                int online = NodeExecutor
                        .getInstance()
                        .getNodeNetworkManager()
                        .getCluster()
                        .getClusterManager()
                        .getOnlineAndWaiting(processGroup.getName());
                online += NodeExecutor
                        .getInstance()
                        .getNodeNetworkManager()
                        .getRegisteredProcesses()
                        .size(processGroup.getName());

                List<ProcessInformation> preparedProcesses = this.getPreparedProcesses(processGroup.getName());

                if (processGroup.getStartupConfiguration().getMaxOnlineProcesses() != -1
                        && processGroup.getStartupConfiguration().getMaxOnlineProcesses() <= online) {
                    continue;
                }

                if (processGroup.getStartupConfiguration().getMinOnlineProcesses() < 0
                        || processGroup.getStartupConfiguration().getMinOnlineProcesses() <= online) {
                    continue;
                }

                if (!preparedProcesses.isEmpty()) {
                    System.out.println(LanguageManager.get(
                            "process-start-already-prepared-process",
                            processGroup.getName(),
                            preparedProcesses.get(0).getProcessDetail().getName()
                    ));
                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(preparedProcesses.get(0));
                    continue;
                }

                System.out.println(LanguageManager.get("process-start-creating-new-process", processGroup.getName()));
                ExecutorAPI.getInstance()
                        .getAsyncAPI()
                        .getProcessAsyncAPI()
                        .startProcessAsync(processGroup.getName())
                        .awaitUninterruptedly();
            }
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @NotNull
    private List<ProcessInformation> getPreparedProcesses(@NotNull String group) {
        return Streams.list(
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(group),
                e -> e.getProcessDetail().getProcessState().equals(ProcessState.PREPARED)
        );
    }
}
