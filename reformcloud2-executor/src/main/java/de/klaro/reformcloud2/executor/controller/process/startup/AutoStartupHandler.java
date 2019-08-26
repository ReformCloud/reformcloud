package de.klaro.reformcloud2.executor.controller.process.startup;

import de.klaro.reformcloud2.executor.api.common.groups.ProcessGroup;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Template;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import de.klaro.reformcloud2.executor.controller.ControllerExecutor;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;

public final class AutoStartupHandler extends AbsoluteThread {

    public AutoStartupHandler() {
        update();
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    public void update() {
        if (perPriorityStartup.size() > 0) {
            perPriorityStartup.clear();
        }

        ControllerExecutor.getInstance().getControllerExecutorConfig().getProcessGroups().forEach(new Consumer<ProcessGroup>() {
            @Override
            public void accept(ProcessGroup processGroup) {
                perPriorityStartup.add(processGroup);
            }
        });
    }

    private SortedSet<ProcessGroup> perPriorityStartup = new TreeSet<>(new Comparator<ProcessGroup>() {
        @Override
        public int compare(ProcessGroup o1, ProcessGroup o2) {
            int o1Priority = o1.getStartupConfiguration().getStartupPriority();
            int o2Priority = o2.getStartupConfiguration().getStartupPriority();

            return Integer.compare(o1Priority, o2Priority);
        }
    });

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            perPriorityStartup.forEach(new Consumer<ProcessGroup>() {
                @Override
                public void accept(ProcessGroup processGroup) {
                    List<Template> started = ControllerExecutor.getInstance().getProcessManager().getOnlineAndWaiting(processGroup.getName());

                    if (started.size() < processGroup.getStartupConfiguration().getMinOnlineProcesses()) {
                        for (int i = started.size(); i >= processGroup.getStartupConfiguration().getMinOnlineProcesses(); i++) {
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
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            });

            AbsoluteThread.sleep(100);
        }
    }
}
