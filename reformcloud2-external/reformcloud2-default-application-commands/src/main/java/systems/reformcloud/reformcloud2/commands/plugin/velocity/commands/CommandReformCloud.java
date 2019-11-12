package systems.reformcloud.reformcloud2.commands.plugin.velocity.commands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.velocity.VelocityExecutor;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandReformCloud implements Command {

    public CommandReformCloud(@Nonnull List<String> aliases) {
        this.aliases = aliases;
    }

    private final List<String> aliases;

    @Override
    public void execute(CommandSource commandSender, @NonNull String[] strings) {
        final String prefix = VelocityExecutor.getInstance().getMessages().getPrefix() + " ";

        if (strings.length == 1 && strings[0].equalsIgnoreCase("list")) {
            ExecutorAPI.getInstance().getAllProcesses().forEach(e -> commandSender.sendMessage(TextComponent.of(
                    "=> " + e.getName()
                            + "/ Display: " + e.getDisplayName()
                            + "/ UniqueID: " + e.getProcessUniqueID()
                            + "/ Parent: " + e.getParent()
                            + "/ Connected: " + e.getNetworkInfo().isConnected()
            )));
            return;
        }

        if (strings.length == 2) {
            switch (strings[0].toLowerCase()) {
                case "start": {
                    ExecutorAPI.getInstance().startProcess(strings[1]);
                    commandSender.sendMessage(getCommandSuccessMessage());
                    return;
                }

                case "stop": {
                    ExecutorAPI.getInstance().stopProcess(strings[1]);
                    commandSender.sendMessage(getCommandSuccessMessage());
                    return;
                }

                case "stopall": {
                    ExecutorAPI.getInstance().getProcesses(strings[1]).forEach(e -> ExecutorAPI.getInstance().stopProcess(e.getName()));
                    commandSender.sendMessage(getCommandSuccessMessage());
                    return;
                }

                case "maintenance": {
                    ProcessGroup processGroup = ExecutorAPI.getInstance().getProcessGroup(strings[1]);
                    if (processGroup == null) {
                        commandSender.sendMessage(TextComponent.of(prefix + "§cThis group is unknown"));
                        return;
                    }

                    processGroup.getPlayerAccessConfiguration().toggleMaintenance();
                    ExecutorAPI.getInstance().updateProcessGroup(processGroup);
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
                        ExecutorAPI.getInstance().startProcess(strings[1]);
                    } else {
                        for (int started = 1; started <= count; started++) {
                            ExecutorAPI.getInstance().startProcess(strings[1]);
                            AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 20);
                        }
                    }

                    commandSender.sendMessage(getCommandSuccessMessage());
                    return;
                }

                case "ofall": {
                    if (strings[2].equalsIgnoreCase("stop")) {
                        MainGroup mainGroup = ExecutorAPI.getInstance().getMainGroup(strings[1]);
                        if (mainGroup == null) {
                            commandSender.sendMessage(TextComponent.of(prefix + "§cThis main group is unknown"));
                            return;
                        }

                        mainGroup.getSubGroups().forEach(s -> {
                            ProcessGroup processGroup = ExecutorAPI.getInstance().getProcessGroup(s);
                            if (processGroup == null) {
                                return;
                            }

                            ExecutorAPI.getInstance().getProcesses(processGroup.getName()).forEach(processInformation -> {
                                ExecutorAPI.getInstance().stopProcess(processInformation.getProcessUniqueID());
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

            ExecutorAPI.getInstance().executeProcessCommand(strings[1], stringBuilder.toString());
            commandSender.sendMessage(getCommandSuccessMessage());
            return;
        }

        commandSender.sendMessage(TextComponent.of(prefix + "§7/rc list"));
        commandSender.sendMessage(TextComponent.of(prefix + "§7/rc start <group>"));
        commandSender.sendMessage(TextComponent.of(prefix + "§7/rc start <group> <count>"));
        commandSender.sendMessage(TextComponent.of(prefix + "§7/rc stop <name>"));
        commandSender.sendMessage(TextComponent.of(prefix + "§7/rc stopall <group>"));
        commandSender.sendMessage(TextComponent.of(prefix + "§7/rc ofall <mainGroup> stop"));
        commandSender.sendMessage(TextComponent.of(prefix + "§7/rc execute <name> <command>"));
        commandSender.sendMessage(TextComponent.of(prefix + "§7/rc maintenance <group>"));
    }

    private TextComponent getCommandSuccessMessage() {
        String message = VelocityExecutor.getInstance().getMessages().getCommandExecuteSuccess();
        message = VelocityExecutor.getInstance().getMessages().format(message);
        return TextComponent.of(message);
    }

    @Nonnull
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public boolean hasPermission(CommandSource source, @NonNull String[] args) {
        return source.hasPermission("reformcloud.command.reformcloud");
    }
}
