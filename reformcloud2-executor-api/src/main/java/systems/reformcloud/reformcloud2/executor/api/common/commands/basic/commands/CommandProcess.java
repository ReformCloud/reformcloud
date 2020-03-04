package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static systems.reformcloud.reformcloud2.executor.api.common.CommonHelper.DECIMAL_FORMAT;

public final class CommandProcess extends GlobalCommand {

    public CommandProcess(@Nonnull Function<ProcessInformation, Boolean> screenToggle, @Nonnull Consumer<ProcessInformation> copy) {
        super("process", "reformcloud.command.process", "The process management command", "p", "processes");
        this.screenToggle = screenToggle;
        this.copy = copy;
    }

    private final Function<ProcessInformation, Boolean> screenToggle;

    private final Consumer<ProcessInformation> copy;

    @Override
    public void describeCommandToSender(@Nonnull CommandSource source) {
        source.sendMessages((
                "process list                                  | Lists all processes\n" +
                " --group=[group]                              | Lists all processes of the specified group\n" +
                " \n" +
                "process <name | uniqueID> [info]              | Shows information about a process\n" +
                "process <name | uniqueID> [stop]              | Stops the process\n" +
                "process <name | uniqueID> [screen]            | Toggles the screen logging of the process to the console\n" +
                "process <name | uniqueID> [copy]              | Copies the specified process is the currently running template\n" +
                "process <name | uniqueID> [execute] <command> | Sends the specified command to the process"
        ).split("\n"));
    }

    @Override
    public boolean handleCommand(@Nonnull CommandSource commandSource, @Nonnull String[] strings) {
        if (strings.length == 0) {
            this.describeCommandToSender(commandSource);
            return true;
        }

        Properties properties = StringUtil.calcProperties(strings, 1);
        if (strings[0].equalsIgnoreCase("list")) {
            Collection<ProcessInformation> processes;
            if (properties.containsKey("group")) {
                processes = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(properties.getProperty("group"));
            } else {
                processes = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses();
            }

            commandSource.sendMessage(LanguageManager.get("command-process-process-list-prefix", processes.size()));
            processes.forEach(
                    e -> commandSource.sendMessage("  - " + e.getName() + "/" + e.getProcessUniqueID())
            );
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
                    target.getTemplate().getName(),
                    target.getTemplate().getBackend())
            );
            copy.accept(target);
            return true;
        }

        if (strings.length == 1 || (strings.length == 2 && strings[1].equalsIgnoreCase("info"))) {
            this.describeProcessToSender(commandSource, target);
            return true;
        }

        if (strings.length == 2 && (strings[1].equalsIgnoreCase("stop") || strings[1].equalsIgnoreCase("kill"))) {
            ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().stopProcessAsync(target.getProcessUniqueID()).onComplete(info -> {});
            commandSource.sendMessage(LanguageManager.get("command-process-stop-proceed", strings[0]));
            return true;
        }

        if (strings.length > 2 && (strings[1].equalsIgnoreCase("command")
                || strings[1].equalsIgnoreCase("cmd")
                || strings[1].equalsIgnoreCase("execute"))) {
            String command = String.join(" ", Arrays.copyOfRange(strings, 2, strings.length));
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().executeProcessCommand(target.getName(), command);
            commandSource.sendMessage(LanguageManager.get("command-process-command-execute", command, strings[0]));
            return true;
        }

        this.describeCommandToSender(commandSource);
        return true;
    }

    private void describeProcessToSender(CommandSource source, ProcessInformation information) {
        StringBuilder builder = new StringBuilder();

        builder.append(" > Name         - ").append(information.getName()).append("\n");
        builder.append(" > Display      - ").append(information.getDisplayName()).append("\n");
        builder.append(" > Parent       - ").append(information.getParent()).append("\n");
        builder.append(" > Unique-ID    - ").append(information.getProcessUniqueID().toString()).append("\n");
        builder.append(" > Group        - ").append(information.getProcessGroup().getName()).append("\n");
        builder.append(" > Template     - ").append(information.getTemplate().getName()).append("/")
                .append(information.getTemplate().getBackend()).append("\n");
        builder.append("\n");
        builder.append(" > Ready        - ").append(information.getProcessState().isReady() ? "&ayes&r" : "&cno&r").append("\n");
        builder.append(" > State        - ").append(information.getProcessState().name()).append("\n");
        builder.append(" > Connected    - ").append(information.getNetworkInfo().isConnected() ? "&ayes&r" : "&cno&r").append("\n");
        builder.append(" > Address      - ").append(information.getNetworkInfo().getHost())
                .append(":").append(information.getNetworkInfo().getPort()).append("\n");
        if (information.getNetworkInfo().isConnected()) {
            builder.append(" > Connected at - ").append(CommonHelper.DATE_FORMAT.format(information.getNetworkInfo().getConnectTime())).append("\n");
        }

        builder.append(" ").append("\n");
        builder.append(" > Inclusions").append("\n");

        if (!information.getTemplate().getTemplateInclusions().isEmpty()) {
            for (Inclusion templateInclusion : information.getTemplate().getTemplateInclusions()) {
                builder.append("   > ").append(templateInclusion.getKey()).append("/").append(templateInclusion.getBackend()).append("\n");
            }

            builder.append(" ").append("\n");
        }

        if (!information.getTemplate().getPathInclusions().isEmpty()) {
            for (Inclusion pathInclusion : information.getTemplate().getPathInclusions()) {
                builder.append("   > ").append(pathInclusion.getKey()).append(" FROM ").append(pathInclusion.getBackend()).append("\n");
            }
        }

        builder.append(" ").append("\n");

        builder.append(" > Runtime").append("\n");
        builder.append("  > OS           - ").append(information.getProcessRuntimeInformation().getOsVersion()).append("\n");
        builder.append("  > OS-Arch      - ").append(information.getProcessRuntimeInformation().getSystemArchitecture()).append("\n");
        builder.append("  > Java         - ").append(information.getProcessRuntimeInformation().getJavaVersion()).append("\n");
        builder.append("  > CPU          - ").append(DECIMAL_FORMAT.format(information.getProcessRuntimeInformation().getCpuUsageInternal())).append("%").append("\n");
        builder.append("  > Memory       - ").append(information.getProcessRuntimeInformation().getMemoryUsageInternal()).append("MB").append("\n");
        builder.append("  > Non-Heap     - ").append(information.getProcessRuntimeInformation().getNonHeapMemoryUsage()).append("MB").append("\n");
        builder.append("  > Threads      - ").append(information.getProcessRuntimeInformation().getThreadInfos().size()).append("\n");
        builder.append("  > Dead Threads - ").append(information.getProcessRuntimeInformation().getDeadLockedThreads().length);

        source.sendMessages(builder.toString().split("\n"));
    }

    @Nullable
    private ProcessInformation getProcess(String s) {
        UUID process = CommonHelper.tryParse(s);
        return process == null
                ? ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(s)
                : ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(process);
    }
}
