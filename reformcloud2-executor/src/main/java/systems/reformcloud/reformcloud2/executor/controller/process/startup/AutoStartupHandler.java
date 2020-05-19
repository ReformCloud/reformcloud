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
package systems.reformcloud.reformcloud2.executor.controller.process.startup;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public final class AutoStartupHandler extends AbsoluteThread {

    private final SortedSet<ProcessGroup> perPriorityStartup = new TreeSet<>((o1, o2) -> {
        int o1Priority = o1.getStartupConfiguration().getStartupPriority();
        int o2Priority = o2.getStartupConfiguration().getStartupPriority();

        if (o1Priority >= o2Priority) {
            return -1;
        }

        return 1;
    });

    public AutoStartupHandler() {
        this.update();
        this.enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    public void update() {
        if (this.perPriorityStartup.size() > 0) {
            this.perPriorityStartup.clear();
        }

        this.perPriorityStartup.addAll(ControllerExecutor.getInstance().getControllerExecutorConfig().getProcessGroups());
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Streams.copySortedSet(this.perPriorityStartup).forEach(processGroup -> {
                long started = ControllerExecutor.getInstance().getProcessManager().getOnlineAndWaitingProcessCount(processGroup.getName());
                int waiting = ControllerExecutor.getInstance().getProcessManager().getWaitingProcesses(processGroup.getName());

                List<ProcessInformation> preparedProcesses = this.getPreparedProcesses(processGroup.getName());

                if (started < processGroup.getStartupConfiguration().getMinOnlineProcesses()) {
                    for (long i = started; i < processGroup.getStartupConfiguration().getMinOnlineProcesses(); i++) {
                        List<ProcessInformation> all = ControllerExecutor.getInstance().getProcessManager().getAllProcesses();
                        if (ControllerExecutor.getInstance().getControllerConfig().getMaxProcesses() == -1
                                || ControllerExecutor.getInstance().getControllerConfig().getMaxProcesses() > all.size()) {
                            if (processGroup.getStartupConfiguration().getMaxOnlineProcesses() == -1
                                    || processGroup.getStartupConfiguration().getMaxOnlineProcesses() > i) {
                                if (!preparedProcesses.isEmpty()) {
                                    ProcessInformation prepared = preparedProcesses.get(0);
                                    preparedProcesses.remove(prepared);
                                    System.out.println(LanguageManager.get(
                                            "process-start-already-prepared-process",
                                            processGroup.getName(),
                                            prepared.getProcessDetail().getName()
                                    ));
                                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(prepared);
                                } else {
                                    System.out.println(LanguageManager.get("process-start-creating-new-process", processGroup.getName()));
                                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(processGroup.getName());
                                    AbsoluteThread.sleep(100);
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }

                if (preparedProcesses.size() + waiting < processGroup.getStartupConfiguration().getAlwaysPreparedProcesses()) {
                    for (int i = preparedProcesses.size() + waiting; i < processGroup.getStartupConfiguration().getAlwaysPreparedProcesses(); i++) {
                        System.out.println(LanguageManager.get("process-preparing-new-process", processGroup.getName()));
                        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().prepareProcess(processGroup.getName());
                        AbsoluteThread.sleep(100);
                    }
                }
            });

            AbsoluteThread.sleep(100);
        }
    }

    private List<ProcessInformation> getPreparedProcesses(String group) {
        return Streams.list(ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(group),
                e -> e.getProcessDetail().getProcessState().equals(ProcessState.PREPARED)
        );
    }
}
