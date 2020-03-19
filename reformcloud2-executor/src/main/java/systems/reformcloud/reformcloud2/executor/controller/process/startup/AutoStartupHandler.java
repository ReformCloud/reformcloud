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

    public AutoStartupHandler() {
        update();
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    public void update() {
        if (perPriorityStartup.size() > 0) {
            perPriorityStartup.clear();
        }

        perPriorityStartup.addAll(ControllerExecutor.getInstance().getControllerExecutorConfig().getProcessGroups());
    }

    private final SortedSet<ProcessGroup> perPriorityStartup = new TreeSet<>((o1, o2) -> {
        int o1Priority = o1.getStartupConfiguration().getStartupPriority();
        int o2Priority = o2.getStartupConfiguration().getStartupPriority();

        if (o1Priority >= o2Priority) {
            return -1;
        }

        return 1;
    });

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Streams.copySortedSet(perPriorityStartup).forEach(processGroup -> {
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
                                            prepared.getName()
                                    ));
                                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(prepared);
                                } else {
                                    System.out.println(LanguageManager.get("process-start-creating-new-process", processGroup.getName()));
                                    ControllerExecutor.getInstance().getProcessManager().startProcess(processGroup.getName());
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
                e -> e.getProcessState().equals(ProcessState.PREPARED)
        );
    }
}
