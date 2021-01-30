/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.node.runnables;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.group.process.ProcessGroup;
import systems.reformcloud.group.process.startup.AutomaticStartupConfiguration;
import systems.reformcloud.group.process.startup.StartupConfiguration;
import systems.reformcloud.language.TranslationHolder;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.process.ProcessState;
import systems.reformcloud.utility.MoreCollections;
import systems.reformcloud.wrappers.ProcessWrapper;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class OnlinePercentCheckerTask implements Runnable {

  private final Map<String, Long> checkGroups = new ConcurrentHashMap<>();

  private static void startPreparedOfGroup(@NotNull Collection<ProcessInformation> processes, @NotNull ProcessGroup processGroup) {
    ProcessInformation prepared = MoreCollections.filter(processes, e -> e.getCurrentState() == ProcessState.PREPARED);
    if (prepared != null) {
      Optional<ProcessWrapper> processWrapper = ExecutorAPI.getInstance().getProcessProvider()
        .getProcessByUniqueId(prepared.getId().getUniqueId());
      if (processWrapper.isPresent()) {
        processWrapper.get().setRuntimeState(ProcessState.STARTED);
        System.out.println(TranslationHolder.translate("process-start-process", processGroup.getName()));
      } else {
        ExecutorAPI.getInstance().getProcessProvider().createProcess()
          .group(processGroup)
          .prepare()
          .onComplete(wrapper -> {
            if (wrapper != null) {
              wrapper.setRuntimeState(ProcessState.STARTED);
              System.out.println(TranslationHolder.translate("process-start-process", processGroup.getName()));
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
            System.out.println(TranslationHolder.translate("process-start-process", processGroup.getName()));
          }
        });
    }
  }

  @Override
  public void run() {
    for (ProcessGroup processGroup : ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroups()) {
      AutomaticStartupConfiguration configuration = processGroup.getStartupConfiguration().getAutomaticStartupConfiguration();
      if ((!configuration.isAutomaticStartupEnabled() && !configuration.isAutomaticShutdownEnabled())
        || configuration.getCheckIntervalInSeconds() <= 0 || configuration.getMaxPercentOfPlayersToStart() <= 0
      ) {
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
        .filter(process -> process.getCurrentState().isStartedOrOnline())
        .mapToInt(process -> process.getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers())
        .sum();
      int online = processes.stream().mapToInt(ProcessInformation::getOnlineCount).sum();

      final double percent = this.getPercentOf(online, max);
      if (percent < configuration.getMaxPercentOfPlayersToStart() && percent < configuration.getMaxPercentOfPlayersToStop()) {
        this.checkGroups.put(processGroup.getName(), configuration.getCheckIntervalInSeconds());
        continue;
      }

      if (processGroup.getStartupConfiguration().getMaximumProcessAmount() != -1 && processes.size() >= processGroup.getStartupConfiguration().getMaximumProcessAmount()) {
        this.checkGroups.put(processGroup.getName(), configuration.getCheckIntervalInSeconds());
        continue;
      }

      if (percent >= configuration.getMaxPercentOfPlayersToStart() && configuration.isAutomaticStartupEnabled()) {
        startPreparedOfGroup(processes, processGroup);
      } else if (configuration.isAutomaticShutdownEnabled()) {
        for (ProcessInformation process : processes) {
          final double onlinePercentage = this.getPercentOf(process.getOnlineCount(), process.getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers());
          if (onlinePercentage <= configuration.getMaxPercentOfPlayersToStop()) {
            final StartupConfiguration startupConfiguration = process.getProcessGroup().getStartupConfiguration();
            if (processes.size() > startupConfiguration.getAlwaysOnlineProcessAmount()) {
              ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(process.getId().getUniqueId())
                .ifPresent(wrapper -> wrapper.setRuntimeState(ProcessState.STOPPED));
              break;
            }
          }
        }
      }

      this.checkGroups.put(processGroup.getName(), configuration.getCheckIntervalInSeconds());
    }
  }

  private double getPercentOf(double online, double max) {
    return ((online * 100) / max);
  }
}
