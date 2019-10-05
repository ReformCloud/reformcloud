package systems.reformcloud.reformcloud2.executor.controller.process.startup;

import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
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
            perPriorityStartup.forEach(processGroup -> {
                int started = ControllerExecutor.getInstance().getProcessManager().getOnlineAndWaitingProcessCount(processGroup.getName());

                if (started < processGroup.getStartupConfiguration().getMinOnlineProcesses()) {
                    for (int i = started; i < processGroup.getStartupConfiguration().getMinOnlineProcesses(); i++) {
                        if (i >= 1024) {
                            //Do not allow more than 1024 process per group
                            break;
                        }

                        List<ProcessInformation> all = ControllerExecutor.getInstance().getProcessManager().getAllProcesses();
                        if (ControllerExecutor.getInstance().getControllerConfig().getMaxProcesses() == -1
                                || ControllerExecutor.getInstance().getControllerConfig().getMaxProcesses() > all.size()) {
                            if (processGroup.getStartupConfiguration().getMaxOnlineProcesses() == -1
                                    || processGroup.getStartupConfiguration().getMaxOnlineProcesses() > i) {
                                ControllerExecutor.getInstance().getProcessManager().startProcess(processGroup.getName());
                                AbsoluteThread.sleep(100);
                            }
                        } else {
                            break;
                        }
                    }
                }
            });

            AbsoluteThread.sleep(100);
        }
    }
}
