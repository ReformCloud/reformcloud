/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.node.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

public final class LocalAutoStartupHandler implements Runnable {

    private final SortedSet<ProcessGroup> perPriorityStartup = new ConcurrentSkipListSet<>((o1, o2) -> {
        int o1Priority = o1.getStartupConfiguration().getStartupPriority();
        int o2Priority = o2.getStartupConfiguration().getStartupPriority();

        if (o1Priority <= o2Priority) {
            return -1;
        }

        return 1;
    });

    public void update() {
        this.perPriorityStartup.clear();
        this.perPriorityStartup.addAll(NodeExecutor.getInstance().getClusterSyncManager().getProcessGroups());
    }

    @Override
    public void run() {
        if (!NodeExecutor.getInstance().getClusterSyncManager().isConnectedAndSyncWithCluster()) {
            return;
        }

        if (!NodeExecutor.getInstance().getNodeNetworkManager().getCluster().isSelfNodeHead()) {
            return;
        }

        this.handleProcessStarts();
        this.handleProcessPrepare();
    }

    private void handleProcessPrepare() {
        for (ProcessGroup processGroup : Streams.copySortedSet(this.perPriorityStartup)) {
            List<ProcessInformation> preparedProcesses = this.getPreparedProcesses(processGroup.getName());

            int total = preparedProcesses.size() + NodeExecutor
                    .getInstance()
                    .getNodeNetworkManager()
                    .getRegisteredProcesses()
                    .size(processGroup.getName());

            if (total >= processGroup.getStartupConfiguration().getAlwaysPreparedProcesses()) {
                continue;
            }

            System.out.println(LanguageManager.get("process-preparing-new-process", processGroup.getName()));
            ExecutorAPI.getInstance()
                    .getAsyncAPI()
                    .getProcessAsyncAPI()
                    .prepareProcessAsync(processGroup.getName())
                    .awaitUninterruptedly();
        }
    }

    private void handleProcessStarts() {
        try {
            for (ProcessGroup processGroup : Streams.copySortedSet(this.perPriorityStartup)) {
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
