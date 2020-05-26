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
package systems.reformcloud.reformcloud2.executor.api.groups.task;

import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.scheduler.TaskScheduler;

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
                    && e.getStartupConfiguration().getAutomaticStartupConfiguration().getMaxPercentOfPlayers() > 0
                    && e.getPlayerAccessConfiguration().isUseCloudPlayerLimit()) {
                int id = TaskScheduler.INSTANCE.schedule(() -> {
                    List<ProcessInformation> processes = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(e.getName());
                    int max = processes.stream().mapToInt(p -> p.getProcessDetail().getMaxPlayers()).sum();
                    int online = processes.stream().mapToInt(p -> p.getProcessPlayerManager().getOnlineCount()).sum();

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
