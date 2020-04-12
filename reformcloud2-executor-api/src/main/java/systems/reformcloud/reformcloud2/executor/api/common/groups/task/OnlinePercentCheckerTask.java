package systems.reformcloud.reformcloud2.executor.api.common.groups.task;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.scheduler.TaskScheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class OnlinePercentCheckerTask {

    private static final Collection<Integer> TASKS = new ArrayList<>();

    private OnlinePercentCheckerTask() {
        throw new UnsupportedOperationException();
    }

    public static void start() {
        ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroups().forEach(e -> {
            if (e.getStartupConfiguration().getAutomaticStartupConfiguration().isEnabled()
                    && e.getStartupConfiguration().getAutomaticStartupConfiguration().getCheckIntervalInSeconds() > 0
                    && e.getStartupConfiguration().getAutomaticStartupConfiguration().getMaxPercentOfPlayers() > 0) {
                int id = TaskScheduler.INSTANCE.schedule(() -> {
                    List<ProcessInformation> processes = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(e.getName());
                    int max = processes.stream().mapToInt(p -> p.getProcessDetail().getMaxPlayers()).sum();
                    int online = processes.stream().mapToInt(p -> p.getProcessDetail().getMaxPlayers()).sum();

                    if (getPercentOf(online, max) >= e.getStartupConfiguration().getAutomaticStartupConfiguration().getMaxPercentOfPlayers()
                            && (e.getStartupConfiguration().getMaxOnlineProcesses() == -1
                            || processes.size() < e.getStartupConfiguration().getMaxOnlineProcesses())) {
                        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(e.getName());
                    }
                }, 0, e.getStartupConfiguration().getAutomaticStartupConfiguration().getCheckIntervalInSeconds(), TimeUnit.SECONDS).getId();
                TASKS.add(id);
            }
        });
    }

    public static void stop() {
        TASKS.forEach(TaskScheduler.INSTANCE::cancel);
        TASKS.clear();
    }

    private static double getPercentOf(double online, double max) {
        return ((online * 100) / max);
    }
}
