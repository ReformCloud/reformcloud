package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import javax.annotation.Nonnull;
import java.util.Properties;
import java.util.Random;

public final class CommandLaunch extends GlobalCommand {

    public CommandLaunch() {
        super("launch", "reformcloud.command.launch", "Launches new processes", "start", "prepare", "new");
    }

    @Override
    public void describeCommandToSender(@Nonnull CommandSource source) {
        source.sendMessages((
                "launch <group-name>            | Creates a new process bases on the group\n" +
                        " --template=[template]         | Uses a specific template for the startup (default: random)\n" +
                        " --amount=[amount]             | Starts the specified amount of processes (default: 1)\n" +
                        " --prepare-only=[prepare-only] | Prepares the process but does not start it (default: false)"
        ).split("\n"));
    }

    @Override
    public boolean handleCommand(@Nonnull CommandSource commandSource, @Nonnull String[] strings) {
        if (strings.length == 0) {
            this.describeCommandToSender(commandSource);
            return true;
        }

        ProcessGroup base = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[0]);
        if (base == null) {
            commandSource.sendMessage(LanguageManager.get("command-launch-start-not-possible-group-not-exists", strings[0]));
            return true;
        }

        Properties properties = StringUtil.calcProperties(strings, 1);
        String template = base.getTemplates().isEmpty() ? "default" : base.getTemplates().get(new Random().nextInt(base.getTemplates().size())).getName();
        int amount = 1;
        boolean prepareOnly = false;

        if (properties.containsKey("template")) {
            Template baseTemplate = Streams.filter(base.getTemplates(), e -> e.getName().equals(properties.getProperty("template")));
            if (baseTemplate == null) {
                commandSource.sendMessage(LanguageManager.get("command-launch-template-not-exists", properties.getProperty("template"), base.getName()));
                return true;
            }

            template = baseTemplate.getName();
        }

        if (properties.containsKey("amount")) {
            Integer amountToStart = CommonHelper.fromString(properties.getProperty("amount"));
            if (amountToStart == null || amountToStart <= 0) {
                commandSource.sendMessage(LanguageManager.get("command-integer-failed", 0, properties.getProperty("amount")));
                return true;
            }

            amount = amountToStart;
        }

        if (properties.containsKey("prepare-only")) {
            Boolean prepare = CommonHelper.booleanFromString(properties.getProperty("prepare-only"));
            if (prepare == null) {
                commandSource.sendMessage(LanguageManager.get("command-required-boolean", properties.getProperty("prepare-only")));
                return true;
            }

            prepareOnly = prepare;
        }

        if (prepareOnly) {
            for (int i = 1; i <= amount; i++) {
                ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().prepareProcessAsync(base.getName(), template).onComplete(info -> {
                });
            }
            commandSource.sendMessage(LanguageManager.get("command-launch-prepared-processes", amount, base.getName(), template));
        } else {
            for (int i = 1; i <= amount; i++) {
                ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().startProcessAsync(base.getName(), template).onComplete(info -> {
                });
            }
            commandSource.sendMessage(LanguageManager.get("command-launch-started-processes", amount, base.getName(), template));
        }

        return true;
    }
}
