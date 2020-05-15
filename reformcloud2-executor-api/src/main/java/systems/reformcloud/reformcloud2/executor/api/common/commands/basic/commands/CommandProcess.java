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
package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;

import java.util.*;
import java.util.function.Function;

import static systems.reformcloud.reformcloud2.executor.api.common.CommonHelper.DECIMAL_FORMAT;

public final class CommandProcess extends GlobalCommand {

    private static final String FORMAT_LIST = " - %s - %d/%d - %s - %s";
    private final Function<ProcessInformation, Boolean> screenToggle;

    public CommandProcess(@NotNull Function<ProcessInformation, Boolean> screenToggle) {
        super("process", "reformcloud.command.process", "The process management command", "p", "processes");
        this.screenToggle = screenToggle;
    }

    @Override
    public void describeCommandToSender(@NotNull CommandSource source) {
        source.sendMessages((
                "process list                                  | Lists all processes\n" +
                        " --group=[group]                              | Lists all processes of the specified group\n" +
                        " \n" +
                        "process <name | uniqueID> [info]              | Shows information about a process\n" +
                        " --full=[full]                                | Shows the full extra data submitted to the process (default: false)\n" +
                        " \n" +
                        "process <name | uniqueID> [start]             | Starts a process which is prepared\n" +
                        "process <name | uniqueID> [stop]              | Stops the process\n" +
                        "process <name | uniqueID> [screen]            | Toggles the screen logging of the process to the console\n" +
                        "process <name | uniqueID> [copy]              | Copies the specified process is the currently running template\n" +
                        "process <name | uniqueID> [execute] <command> | Sends the specified command to the process"
        ).split("\n"));
    }

    @Override
    public boolean handleCommand(@NotNull CommandSource commandSource, @NotNull String[] strings) {
        if (strings.length == 0) {
            this.describeCommandToSender(commandSource);
            return true;
        }

        Properties properties = StringUtil.calcProperties(strings, 1);
        if (strings[0].equalsIgnoreCase("list")) {
            if (properties.containsKey("group")) {
                ProcessGroup group = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(properties.getProperty("group"));
                if (group == null) {
                    commandSource.sendMessage(LanguageManager.get("command-process-group-unavailable", properties.getProperty("group")));
                    return true;
                }

                this.showAllProcesses(commandSource, group);
            } else {
                for (ProcessGroup processGroup : ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroups()) {
                    this.showAllProcesses(commandSource, processGroup);
                }
            }

            return true;
        }

        ProcessInformation target = this.getProcess(strings[0]);
        if (target == null) {
            commandSource.sendMessage(LanguageManager.get("command-process-process-unknown", strings[0]));
            return true;
        }

        if (strings.length == 2 && strings[1].equalsIgnoreCase("screen")) {
            if (screenToggle.apply(target)) {
                commandSource.sendMessage(LanguageManager.get("command-process-screen-toggle-activated", strings[0]));
            } else {
                commandSource.sendMessage(LanguageManager.get("command-process-screen-toggle-disabled", strings[0]));
            }

            return true;
        }

        if (strings.length == 2 && strings[1].equalsIgnoreCase("copy")) {
            commandSource.sendMessage(LanguageManager.get(
                    "command-process-process-copied",
                    strings[0],
                    target.getProcessDetail().getTemplate().getName(),
                    target.getProcessDetail().getTemplate().getBackend())
            );
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(target);
            return true;
        }

        if (strings.length >= 2 && strings[1].equalsIgnoreCase("info")) {
            if (properties.containsKey("full")) {
                Boolean full = CommonHelper.booleanFromString(properties.getProperty("full"));
                if (full == null) {
                    commandSource.sendMessage(LanguageManager.get("command-required-boolean", properties.getProperty("full")));
                    return true;
                }

                this.describeProcessToSender(commandSource, target, full);
                return true;
            }

            this.describeProcessToSender(commandSource, target, false);
            return true;
        }

        if (strings.length == 2 && (strings[1].equalsIgnoreCase("stop") || strings[1].equalsIgnoreCase("kill"))) {
            ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().stopProcessAsync(target.getProcessDetail().getProcessUniqueID()).onComplete(info -> {
            });
            commandSource.sendMessage(LanguageManager.get("command-process-stop-proceed", strings[0]));
            return true;
        }

        if (strings.length == 2 && strings[1].equalsIgnoreCase("start")) {
            if (!target.getProcessDetail().getProcessState().equals(ProcessState.PREPARED)) {
                commandSource.sendMessage(LanguageManager.get("command-process-process-not-prepared", strings[0]));
                return true;
            }

            ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().startProcessAsync(target).onComplete(e -> {
            });
            commandSource.sendMessage(LanguageManager.get("command-process-starting-prepared", strings[0]));
            return true;
        }

        if (strings.length > 2 && (strings[1].equalsIgnoreCase("command")
                || strings[1].equalsIgnoreCase("cmd")
                || strings[1].equalsIgnoreCase("execute"))) {
            String command = String.join(" ", Arrays.copyOfRange(strings, 2, strings.length));
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().executeProcessCommand(target.getProcessDetail().getName(), command);
            commandSource.sendMessage(LanguageManager.get("command-process-command-execute", command, strings[0]));
            return true;
        }

        this.describeCommandToSender(commandSource);
        return true;
    }

    private void describeProcessToSender(CommandSource source, ProcessInformation information, boolean full) {
        StringBuilder builder = new StringBuilder();

        builder.append(" > Name         - ").append(information.getProcessDetail().getName()).append("\n");
        builder.append(" > Display      - ").append(information.getProcessDetail().getDisplayName()).append("\n");
        builder.append(" > Parent       - ").append(information.getProcessDetail().getParentName()).append("\n");
        builder.append(" > Unique-ID    - ").append(information.getProcessDetail().getProcessUniqueID().toString()).append("\n");
        builder.append(" > Group        - ").append(information.getProcessGroup().getName()).append("\n");
        builder.append(" > Template     - ").append(information.getProcessDetail().getTemplate().getName()).append("/")
                .append(information.getProcessDetail().getTemplate().getBackend()).append("\n");
        builder.append("\n");
        builder.append(" > Ready        - ").append(information.getProcessDetail().getProcessState().isReady() ? "&ayes&r" : "&cno&r").append("\n");
        builder.append(" > State        - ").append(information.getProcessDetail().getProcessState().name()).append("\n");
        builder.append(" > Connected    - ").append(information.getNetworkInfo().isConnected() ? "&ayes&r" : "&cno&r").append("\n");
        builder.append(" > Address      - ").append(information.getNetworkInfo().getHost())
                .append(":").append(information.getNetworkInfo().getPort()).append("\n");
        if (information.getNetworkInfo().isConnected()) {
            builder.append(" > Connected at - ").append(CommonHelper.DATE_FORMAT.format(information.getNetworkInfo().getConnectTime())).append("\n");
        }

        builder.append(" ").append("\n");
        builder.append(" > Inclusions").append("\n");

        if (!information.getProcessDetail().getTemplate().getTemplateInclusions().isEmpty()) {
            for (Inclusion templateInclusion : information.getProcessDetail().getTemplate().getTemplateInclusions()) {
                builder.append("   > ").append(templateInclusion.getKey()).append("/").append(templateInclusion.getBackend()).append("\n");
            }

            builder.append(" ").append("\n");
        }

        if (!information.getProcessDetail().getTemplate().getPathInclusions().isEmpty()) {
            for (Inclusion pathInclusion : information.getProcessDetail().getTemplate().getPathInclusions()) {
                builder.append("   > ").append(pathInclusion.getKey()).append(" FROM ").append(pathInclusion.getBackend()).append("\n");
            }
        }

        builder.append(" ").append("\n");

        builder.append(" > Runtime").append("\n");
        builder.append("  > OS           - ").append(information.getProcessDetail().getProcessRuntimeInformation().getOsVersion()).append("\n");
        builder.append("  > OS-Arch      - ").append(information.getProcessDetail().getProcessRuntimeInformation().getSystemArchitecture()).append("\n");
        builder.append("  > Java         - ").append(information.getProcessDetail().getProcessRuntimeInformation().getJavaVersion()).append("\n");
        builder.append("  > CPU          - ").append(DECIMAL_FORMAT.format(information.getProcessDetail().getProcessRuntimeInformation().getCpuUsageInternal())).append("%").append("\n");
        builder.append("  > Memory       - ").append(information.getProcessDetail().getProcessRuntimeInformation().getMemoryUsageInternal()).append("MB").append("\n");
        builder.append("  > Non-Heap     - ").append(information.getProcessDetail().getProcessRuntimeInformation().getNonHeapMemoryUsage()).append("MB").append("\n");
        builder.append("  > Threads      - ").append(information.getProcessDetail().getProcessRuntimeInformation().getThreadInfos().size()).append("\n");
        builder.append("  > Dead Threads - ").append(information.getProcessDetail().getProcessRuntimeInformation().getDeadLockedThreads().length);

        if (full) {
            builder.append(" ").append("\n");
            builder.append(" > Properties").append("\n");
            builder.append("  ").append(information.getExtra().toPrettyString()).append("\n");
        }

        source.sendMessages(builder.toString().split("\n"));
    }

    @Nullable
    private ProcessInformation getProcess(String s) {
        UUID process = CommonHelper.tryParse(s);
        return process == null
                ? ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(s)
                : ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(process);
    }

    private void showAllProcesses(@NotNull CommandSource source, @NotNull ProcessGroup group) {
        Set<ProcessInformation> all = this.sort(ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(group.getName()));
        all.forEach(
                e -> source.sendMessage(String.format(
                        FORMAT_LIST,
                        e.getProcessDetail().getName(),
                        e.getProcessPlayerManager().getOnlineCount(),
                        e.getProcessDetail().getMaxPlayers(),
                        e.getProcessDetail().getProcessState().name(),
                        e.getProcessDetail().getProcessUniqueID().toString()
                ))
        );
    }

    @NotNull
    private Set<ProcessInformation> sort(@NotNull Collection<ProcessInformation> all) {
        SortedSet<ProcessInformation> out = new TreeSet<>(Comparator.comparingInt(e -> e.getProcessDetail().getId()));
        out.addAll(all);
        return out;
    }
}
