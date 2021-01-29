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
import systems.reformcloud.language.TranslationHolder;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.process.ProcessState;
import systems.reformcloud.utility.MoreCollections;
import systems.reformcloud.wrappers.ProcessWrapper;
import systems.reformcloud.node.NodeExecutor;
import systems.reformcloud.node.cluster.ClusterManager;

import java.util.Collection;
import java.util.Optional;

public class AutoStartRunnable implements Runnable {

  private static void startPreparedOfGroup(@NotNull Collection<ProcessInformation> processes, @NotNull ProcessGroup processGroup) {
    ProcessInformation prepared = MoreCollections.filter(processes, e -> e.getCurrentState() == ProcessState.PREPARED);
    if (prepared != null) {
      Optional<ProcessWrapper> processWrapper = ExecutorAPI.getInstance().getProcessProvider()
        .getProcessByUniqueId(prepared.getId().getUniqueId());
      if (processWrapper.isPresent()) {
        processWrapper.get().setRuntimeState(ProcessState.STARTED);
        System.out.println(TranslationHolder.translate("process-start-process", processGroup.getName()));
      } else {
        ProcessWrapper wrapper = ExecutorAPI.getInstance().getProcessProvider().createProcess()
          .group(processGroup)
          .prepare()
          .getNow(null);
        if (wrapper != null) {
          wrapper.setRuntimeState(ProcessState.STARTED);
          System.out.println(TranslationHolder.translate("process-start-process", processGroup.getName()));
        }
      }
    } else {
      ProcessWrapper wrapper = ExecutorAPI.getInstance().getProcessProvider().createProcess()
        .group(processGroup)
        .prepare()
        .getNow(null);
      if (wrapper != null) {
        wrapper.setRuntimeState(ProcessState.STARTED);
        System.out.println(TranslationHolder.translate("process-start-process", processGroup.getName()));
      }
    }
  }

  @Override
  public void run() {
    if (!NodeExecutor.getInstance().isReady()
      || !ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).isHeadNode()) {
      return;
    }

    for (ProcessGroup processGroup : ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroups()) {
      if (processGroup.getTemplates().isEmpty()) {
        continue;
      }

      Collection<ProcessInformation> processes = ExecutorAPI.getInstance().getProcessProvider().getProcessesByProcessGroup(processGroup.getName());
      int runningProcesses = MoreCollections.allOf(processes, e -> e.getCurrentState().isStartedOrOnline()).size();
      if (processGroup.getStartupConfiguration().getAlwaysOnlineProcessAmount() > runningProcesses
        && (processGroup.getStartupConfiguration().getMaximumProcessAmount() == -1
        || processGroup.getStartupConfiguration().getMaximumProcessAmount() > runningProcesses)) {
        startPreparedOfGroup(processes, processGroup);
      }

      int prepared = MoreCollections.allOf(processes, e -> e.getCurrentState() == ProcessState.PREPARED).size();
      if (processGroup.getStartupConfiguration().getAlwaysPreparedProcessAmount() > prepared) {
        ExecutorAPI.getInstance().getProcessProvider().createProcess().group(processGroup).prepare();
        System.out.println(TranslationHolder.translate("process-preparing-new-process", processGroup.getName()));
      }
    }
  }
}
