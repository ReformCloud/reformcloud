package systems.reformcloud.reformcloud2.commands.plugin.bungeecord.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import systems.reformcloud.reformcloud2.executor.api.bungee.BungeeExecutor;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandReformCloud extends Command {

    public CommandReformCloud(String name, List<String> aliases) {
        super(name, "reformcloud.command.reformcloud", aliases.toArray(new String[0]));
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        final String prefix = BungeeExecutor.getInstance().getMessages().getPrefix() + " ";

        if (strings.length == 1 && strings[0].equalsIgnoreCase("list")) {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses().forEach(e -> commandSender.sendMessage(TextComponent.fromLegacyText(
                    "=> " + e.getProcessDetail().getName()
                            + "/ Display: " + e.getProcessDetail().getDisplayName()
                            + "/ UniqueID: " + e.getProcessDetail().getProcessUniqueID()
                            + "/ Parent: " + e.getProcessDetail().getParentName()
                            + "/ Connected: " + e.getNetworkInfo().isConnected()
            )));
            return;
        }

        if (strings.length == 2) {
            switch (strings[0].toLowerCase()) {
                case "copy": {
                    ProcessInformation process = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(strings[1]);
                    if (process == null) {
                        commandSender.sendMessage(TextComponent.fromLegacyText(prefix + "§cThis process is unknown"));
                        return;
                    }

                    process.toWrapped().copy();
                    commandSender.sendMessage(getCommandSuccessMessage());
                    return;
                }

                case "start": {
                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(strings[1]);
                    commandSender.sendMessage(getCommandSuccessMessage());
                    return;
                }

                case "stop": {
                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().stopProcess(strings[1]);
                    commandSender.sendMessage(getCommandSuccessMessage());
                    return;
                }

                case "stopall": {
                    ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(strings[1]).forEach(e -> ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().stopProcess(e.getProcessDetail().getName()));
                    commandSender.sendMessage(getCommandSuccessMessage());
                    return;
                }

                case "maintenance": {
                    ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[1]);
                    if (processGroup == null) {
                        commandSender.sendMessage(TextComponent.fromLegacyText(prefix + "§cThis group is unknown"));
                        return;
                    }

                    processGroup.getPlayerAccessConfiguration().toggleMaintenance();
                    ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().updateProcessGroup(processGroup);
                    commandSender.sendMessage(getCommandSuccessMessage());
                    return;
                }
            }
        }

        if (strings.length == 3) {
            switch (strings[0].toLowerCase()) {
                case "start": {
                    Integer count = CommonHelper.fromString(strings[2]);
                    if (count == null || count <= 0) {
                        count = 1;
                    }

                    if (count == 1) {
                        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(strings[1]);
                    } else {
                        for (int started = 1; started <= count; started++) {
                            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(strings[1]);
                            AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 20);
                        }
                    }

                    commandSender.sendMessage(getCommandSuccessMessage());
                    return;
                }

                case "ofall": {
                    if (strings[2].equalsIgnoreCase("stop")) {
                        MainGroup mainGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getMainGroup(strings[1]);
                        if (mainGroup == null) {
                            commandSender.sendMessage(TextComponent.fromLegacyText(prefix + "§cThis main group is unknown"));
                            return;
                        }

                        mainGroup.getSubGroups().forEach(s -> {
                            ProcessGroup processGroup = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(s);
                            if (processGroup == null) {
                                return;
                            }

                            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(processGroup.getName()).forEach(processInformation -> {
                                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().stopProcess(processInformation.getProcessDetail().getProcessUniqueID());
                                AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 10);
                            });
                        });
                        commandSender.sendMessage(getCommandSuccessMessage());
                        return;
                    }
                    break;
                }
            }
        }

        if (strings.length >= 3 && strings[0].equalsIgnoreCase("execute")) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : Arrays.copyOfRange(strings, 2, strings.length)) {
                stringBuilder.append(s).append(" ");
            }

            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().executeProcessCommand(strings[1], stringBuilder.toString());
            commandSender.sendMessage(getCommandSuccessMessage());
            return;
        }

        if (strings.length >= 2 && strings[0].equalsIgnoreCase("cmd")) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : Arrays.copyOfRange(strings, 1, strings.length)) {
                stringBuilder.append(s).append(" ");
            }

            ExecutorAPI.getInstance().getSyncAPI().getConsoleSyncAPI().dispatchCommandAndGetResult(stringBuilder.toString());
            commandSender.sendMessage(getCommandSuccessMessage());
            return;
        }

        commandSender.sendMessage(
                new TextComponent(TextComponent.fromLegacyText(prefix + "§7/rc list\n")),
                new TextComponent(TextComponent.fromLegacyText(prefix + "§7/rc copy <name>\n")),
                new TextComponent(TextComponent.fromLegacyText(prefix + "§7/rc start <group>\n")),
                new TextComponent(TextComponent.fromLegacyText(prefix + "§7/rc start <group> <count>\n")),
                new TextComponent(TextComponent.fromLegacyText(prefix + "§7/rc stop <name>\n")),
                new TextComponent(TextComponent.fromLegacyText(prefix + "§7/rc stopall <group>\n")),
                new TextComponent(TextComponent.fromLegacyText(prefix + "§7/rc ofall <mainGroup> stop\n")),
                new TextComponent(TextComponent.fromLegacyText(prefix + "§7/rc execute <name> <command>\n")),
                new TextComponent(TextComponent.fromLegacyText(prefix + "§7/rc maintenance <group>\n")),
                new TextComponent(TextComponent.fromLegacyText(prefix + "§7/rc cmd <command>"))
        );
    }

    private BaseComponent[] getCommandSuccessMessage() {
        String message = BungeeExecutor.getInstance().getMessages().getCommandExecuteSuccess();
        message = BungeeExecutor.getInstance().getMessages().format(message);
        return TextComponent.fromLegacyText(message);
    }
}
