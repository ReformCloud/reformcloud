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

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.AutomaticStartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;

import java.util.Collection;

public class AutoStopRunnable implements Runnable {

    @Override
    public void run() {
        for (ProcessGroup processGroup : ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroups()) {
            AutomaticStartupConfiguration configuration = processGroup.getStartupConfiguration().getAutomaticStartupConfiguration();
            if (configuration.isEnabled() && configuration.getCheckIntervalInSeconds() > 0 && configuration.getMaxPercentOfPlayers() > 0) {
                Collection<ProcessInformation> processes = ExecutorAPI.getInstance().getProcessProvider().getProcessesByProcessGroup(processGroup.getName());

                int onlinePlayers = this.getOnlinePlayerSum(processes);
                int maxPlayers = this.getMaxPlayerSum(processes);

                if (maxPlayers > 0 && onlinePlayers > 0 && ((onlinePlayers * 100) / maxPlayers) > configuration.getMaxPercentOfPlayers()) {
                    int onlineProcesses = Streams.allOf(processes, e -> e.getProcessDetail().getProcessState().isValid()).size();
                    if (processGroup.getStartupConfiguration().getMaxOnlineProcesses() == -1
                            || processGroup.getStartupConfiguration().getMaxOnlineProcesses() > onlineProcesses) {
                        AutoStartRunnable.startPreparedOfGroup(processes, processGroup);
                    }
                }
            }
        }
    }

    private int getOnlinePlayerSum(@NotNull Collection<ProcessInformation> processes) {
        int result = 0;
        for (ProcessInformation process : processes) {
            result += process.getProcessPlayerManager().getOnlineCount();
        }

        return result;
    }

    private int getMaxPlayerSum(@NotNull Collection<ProcessInformation> processes) {
        int result = 0;
        for (ProcessInformation process : processes) {
            result += process.getProcessDetail().getMaxPlayers();
        }

        return result;
    }
}
