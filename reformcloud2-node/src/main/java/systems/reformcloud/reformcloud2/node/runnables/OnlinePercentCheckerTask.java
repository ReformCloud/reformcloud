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
package systems.reformcloud.reformcloud2.node.runnables;

import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.AutomaticStartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OnlinePercentCheckerTask implements Runnable {

    private final Map<String, Long> checkGroups = new ConcurrentHashMap<>();

    @Override
    public void run() {
        for (ProcessGroup processGroup : ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroups()) {
            AutomaticStartupConfiguration configuration = processGroup.getStartupConfiguration().getAutomaticStartupConfiguration();
            if (!configuration.isEnabled() || configuration.getCheckIntervalInSeconds() <= 0 || configuration.getMaxPercentOfPlayers() <= 0) {
                continue;
            }

            if (!this.checkGroups.containsKey(processGroup.getName())) {
                this.checkGroups.put(processGroup.getName(), configuration.getCheckIntervalInSeconds());
                continue;
            }

            long time = this.checkGroups.get(processGroup.getName());
            if (time > 0) {
                this.checkGroups.put(processGroup.getName(), --time);
                continue;
            }

            Collection<ProcessInformation> processes = ExecutorAPI.getInstance().getProcessProvider().getProcessesByProcessGroup(processGroup.getName());
            if (processes.isEmpty()) {
                this.checkGroups.put(processGroup.getName(), configuration.getCheckIntervalInSeconds());
                continue;
            }

            int max = processes.stream().mapToInt(process -> process.getProcessDetail().getMaxPlayers()).sum();
            int online = processes.stream().mapToInt(process -> process.getProcessPlayerManager().getOnlineCount()).sum();

            if (this.getPercentOf(online, max) < configuration.getMaxPercentOfPlayers()) {
                this.checkGroups.put(processGroup.getName(), configuration.getCheckIntervalInSeconds());
                continue;
            }

            if (processGroup.getStartupConfiguration().getMaxOnlineProcesses() != -1 && processes.size() >= processGroup.getStartupConfiguration().getMaxOnlineProcesses()) {
                this.checkGroups.put(processGroup.getName(), configuration.getCheckIntervalInSeconds());
                continue;
            }

            AutoStartRunnable.startPreparedOfGroup(processes, processGroup);
            this.checkGroups.put(processGroup.getName(), configuration.getCheckIntervalInSeconds());
        }
    }

    private double getPercentOf(double online, double max) {
        return ((online * 100) / max);
    }
}
