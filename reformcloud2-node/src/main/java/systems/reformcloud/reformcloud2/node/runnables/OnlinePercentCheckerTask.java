/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
import systems.reformcloud.reformcloud2.shared.groups.process.DefaultProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.process.startup.AutomaticStartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class OnlinePercentCheckerTask implements Runnable {

    private final Map<String, Long> checkGroups = new ConcurrentHashMap<>();

    private static void startPreparedOfGroup(@NotNull Collection<ProcessInformation> processes, @NotNull DefaultProcessGroup processGroup) {
        ProcessInformation prepared = Streams.filter(processes, e -> e.getProcessDetail().getProcessState() == ProcessState.PREPARED);
        if (prepared != null) {
            Optional<ProcessWrapper> processWrapper = ExecutorAPI.getInstance().getProcessProvider()
                .getProcessByUniqueId(prepared.getProcessDetail().getProcessUniqueID());
            if (processWrapper.isPresent()) {
                processWrapper.get().setRuntimeState(ProcessState.STARTED);
                System.out.println(LanguageManager.get("process-start-process", processGroup.getName()));
            } else {
                ExecutorAPI.getInstance().getProcessProvider().createProcess()
                    .group(processGroup)
                    .prepare()
                    .onComplete(wrapper -> {
                        if (wrapper != null) {
                            wrapper.setRuntimeState(ProcessState.STARTED);
                            System.out.println(LanguageManager.get("process-start-process", processGroup.getName()));
                        }
                    });
            }
        } else {
            ExecutorAPI.getInstance().getProcessProvider().createProcess()
                .group(processGroup)
                .prepare()
                .onComplete(wrapper -> {
                    if (wrapper != null) {
                        wrapper.setRuntimeState(ProcessState.STARTED);
                        System.out.println(LanguageManager.get("process-start-process", processGroup.getName()));
                    }
                });
        }
    }

    @Override
    public void run() {
        for (DefaultProcessGroup processGroup : ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroups()) {
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

            int max = processes.stream()
                .filter(process -> process.getProcessDetail().getProcessState().isStartedOrOnline())
                .mapToInt(process -> process.getProcessDetail().getMaxPlayers()).sum();
            int online = processes.stream().mapToInt(process -> process.getProcessPlayerManager().getOnlineCount()).sum();

            if (this.getPercentOf(online, max) < configuration.getMaxPercentOfPlayers()) {
                this.checkGroups.put(processGroup.getName(), configuration.getCheckIntervalInSeconds());
                continue;
            }

            if (processGroup.getStartupConfiguration().getMaxOnlineProcesses() != -1 && processes.size() >= processGroup.getStartupConfiguration().getMaxOnlineProcesses()) {
                this.checkGroups.put(processGroup.getName(), configuration.getCheckIntervalInSeconds());
                continue;
            }

            startPreparedOfGroup(processes, processGroup);
            this.checkGroups.put(processGroup.getName(), configuration.getCheckIntervalInSeconds());
        }
    }

    private double getPercentOf(double online, double max) {
        return ((online * 100) / max);
    }
}
