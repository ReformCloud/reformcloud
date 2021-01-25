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
package systems.reformcloud.reformcloud2.node.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.command.Command;
import systems.reformcloud.reformcloud2.executor.api.command.CommandSender;
import systems.reformcloud.reformcloud2.executor.api.groups.process.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.language.TranslationHolder;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.utility.MoreCollections;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.process.screen.ProcessScreen;
import systems.reformcloud.reformcloud2.node.process.screen.ProcessScreenController;
import systems.reformcloud.reformcloud2.node.protocol.NodeToNodeToggleProcessScreen;
import systems.reformcloud.reformcloud2.shared.StringUtil;
import systems.reformcloud.reformcloud2.shared.parser.Parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import static systems.reformcloud.reformcloud2.shared.Constants.TWO_POINT_THREE_DECIMAL_FORMAT;

public final class CommandProcess implements Command {

  private static final String FORMAT_LIST = " - %s - %d connected - %s - %s";

  public void describeCommandToSender(@NotNull CommandSender source) {
    source.sendMessages((
      "process list                                  | Lists all processes\n" +
        " --group=[group]                              | Lists all processes of the specified group\n" +
        " \n" +
        "process <name | uniqueID> [info]              | Shows information about a process\n" +
        " --full=[full]                                | Shows the full extra data submitted to the process (default: false)\n" +
        " \n" +
        "process <name | uniqueID> [start]             | Starts a process which is prepared\n" +
        "process <name | uniqueID> [stop]              | Stops the process\n" +
        "process <name | uniqueID> [restart]           | Stops and starts the process\n" +
        "process <name | uniqueID> [pause]             | Stops the process but does not delete the files (ready to start again)\n" +
        "process <name | uniqueID> [screen]            | Toggles the screen logging of the process to the console\n" +
        "process <name | uniqueID> [copy]              | Copies the specified process is the currently running template\n" +
        "process <name | uniqueID> [execute] <command> | Sends the specified command to the process"
    ).split("\n"));
  }

  @Override
  public void process(@NotNull CommandSender commandSource, @NotNull String[] strings, @NotNull String fullLine) {
    if (strings.length == 0) {
      this.describeCommandToSender(commandSource);
      return;
    }

    Properties properties = StringUtil.parseProperties(strings, 1);
    if (strings[0].equalsIgnoreCase("list")) {
      if (properties.containsKey("group")) {
        Optional<ProcessGroup> group = ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(properties.getProperty("group"));
        if (!group.isPresent()) {
          commandSource.sendMessage(TranslationHolder.translateDef("command-process-group-unavailable", properties.getProperty("group")));
          return;
        }

        this.showAllProcesses(commandSource, group.get());
      } else {
        for (ProcessGroup processGroup : ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroups()) {
          this.showAllProcesses(commandSource, processGroup);
        }
      }

      return;
    }

    ProcessInformation target = this.getProcess(strings[0]);
    if (target == null) {
      commandSource.sendMessage(TranslationHolder.translateDef("command-process-process-unknown", strings[0]));
      return;
    }

    if (strings.length == 2 && strings[1].equalsIgnoreCase("screen")) {
      if (!target.getCurrentState().isStartedOrOnline()) {
        commandSource.sendMessage(TranslationHolder.translateDef("command-process-screen-process-not-started", strings[0]));
        return;
      }

      ProcessScreenController controller = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ProcessScreenController.class);
      Optional<ProcessScreen> screen = controller.getScreen(target.getId().getUniqueId());
      if (screen.isPresent()) {
        if (screen.get().getListeningNodes().contains(NodeExecutor.getInstance().getSelfName())) {
          screen.get().removeListeningNode(NodeExecutor.getInstance().getSelfName());
          commandSource.sendMessage(TranslationHolder.translateDef("command-process-screen-toggle-disabled", strings[0]));
        } else {
          screen.get().addListeningNode(NodeExecutor.getInstance().getSelfName());
          commandSource.sendMessage(TranslationHolder.translateDef("command-process-screen-toggle-activated", strings[0]));
        }
      } else {
        Optional<NetworkChannel> channel = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class)
          .getChannel(target.getId().getNodeName());
        if (!channel.isPresent()) {
          commandSource.sendMessage(TranslationHolder.translate(
            "command-process-screen-node-not-connected",
            strings[0],
            target.getId().getNodeName()
          ));
          return;
        }

        channel.get().sendPacket(new NodeToNodeToggleProcessScreen(target.getId().getUniqueId()));
        commandSource.sendMessage(TranslationHolder.translate(
          "command-process-screen-toggled-on-node",
          strings[0],
          target.getId().getNodeName()
        ));
      }

      return;
    }

    if (strings.length == 2 && strings[1].equalsIgnoreCase("copy")) {
      Optional<ProcessWrapper> wrapper = ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(target.getId().getUniqueId());
      if (!wrapper.isPresent()) {
        commandSource.sendMessage(TranslationHolder.translateDef("command-process-process-unknown", strings[0]));
        return;
      }

      wrapper.get().copy(target.getPrimaryTemplate());
      commandSource.sendMessage(TranslationHolder.translate(
        "command-process-process-copied",
        strings[0],
        target.getPrimaryTemplate().getName(),
        target.getPrimaryTemplate().getBackend())
      );
      return;
    }

    if (strings.length >= 2 && strings[1].equalsIgnoreCase("info")) {
      if (properties.containsKey("full")) {
        Boolean full = Parsers.BOOLEAN.parse(properties.getProperty("full"));
        if (full == null) {
          commandSource.sendMessage(TranslationHolder.translateDef("command-required-boolean", properties.getProperty("full")));
          return;
        }

        this.describeProcessToSender(commandSource, target, full);
        return;
      }

      this.describeProcessToSender(commandSource, target, false);
      return;
    }

    if (strings.length == 2 && (strings[1].equalsIgnoreCase("stop") || strings[1].equalsIgnoreCase("kill"))) {
      Optional<ProcessWrapper> wrapper = ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(target.getId().getUniqueId());
      if (!wrapper.isPresent()) {
        commandSource.sendMessage(TranslationHolder.translateDef("command-process-process-unknown", strings[0]));
        return;
      }

      wrapper.get().setRuntimeStateAsync(ProcessState.STOPPED);
      commandSource.sendMessage(TranslationHolder.translateDef("command-process-stop-proceed", strings[0]));
      return;
    }

    if (strings.length == 2 && strings[1].equalsIgnoreCase("start")) {
      Optional<ProcessWrapper> wrapper = ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(target.getId().getUniqueId());
      if (!wrapper.isPresent()) {
        commandSource.sendMessage(TranslationHolder.translateDef("command-process-process-unknown", strings[0]));
        return;
      }

      wrapper.get().setRuntimeStateAsync(ProcessState.STARTED);
      commandSource.sendMessage(TranslationHolder.translateDef("command-process-starting-prepared", strings[0]));
      return;
    }

    if (strings.length == 2 && strings[1].equalsIgnoreCase("restart")) {
      Optional<ProcessWrapper> wrapper = ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(target.getId().getUniqueId());
      if (!wrapper.isPresent()) {
        commandSource.sendMessage(TranslationHolder.translateDef("command-process-process-unknown", strings[0]));
        return;
      }

      wrapper.get().setRuntimeStateAsync(ProcessState.RESTARTING);
      commandSource.sendMessage(TranslationHolder.translateDef("command-process-restarting", strings[0]));
      return;
    }

    if (strings.length == 2 && strings[1].equalsIgnoreCase("pause")) {
      Optional<ProcessWrapper> wrapper = ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(target.getId().getUniqueId());
      if (!wrapper.isPresent()) {
        commandSource.sendMessage(TranslationHolder.translateDef("command-process-process-unknown", strings[0]));
        return;
      }

      wrapper.get().setRuntimeStateAsync(ProcessState.PAUSED);
      commandSource.sendMessage(TranslationHolder.translateDef("command-process-pausing", strings[0]));
      return;
    }

    if (strings.length > 2 && (strings[1].equalsIgnoreCase("command")
      || strings[1].equalsIgnoreCase("cmd")
      || strings[1].equalsIgnoreCase("execute"))) {
      Optional<ProcessWrapper> wrapper = ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(target.getId().getUniqueId());
      if (!wrapper.isPresent()) {
        commandSource.sendMessage(TranslationHolder.translateDef("command-process-process-unknown", strings[0]));
        return;
      }

      String command = String.join(" ", Arrays.copyOfRange(strings, 2, strings.length));
      wrapper.get().sendCommand(command);
      commandSource.sendMessage(TranslationHolder.translateDef("command-process-command-execute", command, strings[0]));
      return;
    }

    this.describeCommandToSender(commandSource);
  }

  @Override
  public @NotNull List<String> suggest(@NotNull CommandSender commandSender, String[] strings, int bufferIndex, @NotNull String commandLine) {
    List<String> result = new ArrayList<>();
    if (bufferIndex == 0) {
      result.add("list");
      result.addAll(MoreCollections.map(ExecutorAPI.getInstance().getProcessProvider().getProcesses(), info -> info.getName()));
    } else if (bufferIndex == 1) {
      if (strings[0].equalsIgnoreCase("list")) {
        result.add("--group=");
      } else {
        result.addAll(Arrays.asList("info", "start", "stop", "restart", "pause", "screen", "copy", "execute"));
      }
    } else if (bufferIndex == 2 && strings[1].equalsIgnoreCase("info")) {
      result.add("--full=true");
    }

    return result;
  }

  private void describeProcessToSender(CommandSender source, ProcessInformation information, boolean full) {
    StringBuilder builder = new StringBuilder();

    builder.append(" > Name         - ").append(information.getName()).append("\n");
    builder.append(" > Display      - ").append(information.getId().getDisplayName()).append("\n");
    builder.append(" > Parent       - ").append(information.getId().getNodeName()).append("\n");
    builder.append(" > Unique-ID    - ").append(information.getId().getUniqueId().toString()).append("\n");
    builder.append(" > Group        - ").append(information.getProcessGroup().getName()).append("\n");
    builder.append(" > Template     - ").append(information.getPrimaryTemplate().getName()).append("/")
      .append(information.getPrimaryTemplate().getBackend()).append("\n");
    builder.append("\n");
    builder.append(" > Ready        - ").append(information.getCurrentState().isOnline() ? "&ayes&r" : "&cno&r").append("\n");
    builder.append(" > State        - ").append(information.getCurrentState().name()).append("\n");
    builder.append(" > Address      - ").append(information.getHost().getHost())
      .append(":").append(information.getHost().getPort()).append("\n");

    builder.append(" ").append("\n");
    builder.append(" > Inclusions").append("\n");

    if (!information.getPrimaryTemplate().getTemplateInclusions().isEmpty()) {
      for (Inclusion templateInclusion : information.getPrimaryTemplate().getTemplateInclusions()) {
        builder.append("   > ").append(templateInclusion.getKey()).append("/").append(templateInclusion.getBackend()).append("\n");
      }

      builder.append(" ").append("\n");
    }

    if (!information.getPrimaryTemplate().getPathInclusions().isEmpty()) {
      for (Inclusion pathInclusion : information.getPrimaryTemplate().getPathInclusions()) {
        builder.append("   > ").append(pathInclusion.getKey()).append(" FROM ").append(pathInclusion.getBackend()).append("\n");
      }
    }

    builder.append(" ").append("\n");

    builder.append(" > Runtime").append("\n");
    builder.append("  > OS           - ").append(information.getRuntimeInformation().getOsVersion()).append("\n");
    builder.append("  > OS-Arch      - ").append(information.getRuntimeInformation().getSystemArchitecture()).append("\n");
    builder.append("  > Java         - ").append(information.getRuntimeInformation().getJavaVersion()).append("\n");
    builder.append("  > CPU          - ").append(TWO_POINT_THREE_DECIMAL_FORMAT.format(information.getRuntimeInformation().getCpuUsageInternal())).append("%").append("\n");
    builder.append("  > Memory       - ").append(information.getRuntimeInformation().getMemoryUsageInternal()).append("MB").append("\n");
    builder.append("  > Non-Heap     - ").append(information.getRuntimeInformation().getNonHeapMemoryUsage()).append("MB").append("\n");
    builder.append("  > Dead Threads - ").append(information.getRuntimeInformation().getDeadLockedThreads().length);

    if (full) {
      builder.append(" ").append("\n");
      builder.append(" > Properties").append("\n");
      builder.append("  ").append(information.getData().toPrettyString()).append("\n");
    }

    source.sendMessages(builder.toString().split("\n"));
  }

  @Nullable
  private ProcessInformation getProcess(String s) {
    UUID process = Parsers.UNIQUE_ID.parse(s);
    if (process != null) {
      return ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(process)
        .map(ProcessWrapper::getProcessInformation)
        .orElse(null);
    }

    return ExecutorAPI.getInstance().getProcessProvider().getProcessByName(s)
      .map(ProcessWrapper::getProcessInformation)
      .orElse(null);
  }

  private void showAllProcesses(@NotNull CommandSender source, @NotNull ProcessGroup group) {
    Set<ProcessInformation> all = this.sort(ExecutorAPI.getInstance().getProcessProvider().getProcessesByProcessGroup(group.getName()));
    all.forEach(
      e -> source.sendMessage(String.format(
        FORMAT_LIST,
        e.getName(),
        e.getOnlineCount(),
        e.getCurrentState().name(),
        e.getId().getUniqueId().toString()
      ))
    );
  }

  @NotNull
  private Set<ProcessInformation> sort(@NotNull Collection<ProcessInformation> all) {
    SortedSet<ProcessInformation> out = new TreeSet<>(Comparator.comparingInt(e -> e.getId().getId()));
    out.addAll(all);
    return out;
  }
}
