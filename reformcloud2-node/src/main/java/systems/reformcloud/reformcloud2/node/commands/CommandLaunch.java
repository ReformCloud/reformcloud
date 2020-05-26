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
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfigurationBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.UUID;

public final class CommandLaunch extends GlobalCommand {

    public CommandLaunch() {
        super("launch", "reformcloud.command.launch", "Launches new processes", "start", "prepare", "new");
    }

    @NotNull
    private static Collection<ProcessInclusion> parseInclusions(@NotNull String from) {
        Collection<ProcessInclusion> out = new ArrayList<>();
        for (String inclusion : from.split(";")) {
            String[] parts = inclusion.split(",");
            if (parts.length != 2) {
                continue;
            }

            out.add(new ProcessInclusion(parts[0], parts[1]));
        }

        return out;
    }

    @Override
    public void describeCommandToSender(@NotNull CommandSource source) {
        source.sendMessages((
                "launch <group-name>            | Creates a new process bases on the group\n" +
                        " --template=[template]         | Uses a specific template for the startup (default: random)\n" +
                        " --unique-id=[unique-id]       | Sets the unique id of the new process (default: random)\n" +
                        " --display-name=[display-name] | Sets the display name of the new process (default: none)\n" +
                        " --max-memory=[memory]         | Sets the maximum amount of memory for the new process (default: group-based)\n" +
                        " --port=[port]                 | Sets the port of the new process (default: group-based)\n" +
                        " --id=[id]                     | Sets the id of the new process (default: chosen from amount of online processes)\n" +
                        " --max-players=[max-players]   | Sets the maximum amount of players for the process (default: group-based)\n" +
                        " --inclusions=[url,name;...]   | Sets the inclusions of the process (default: none)\n" +
                        " --amount=[amount]             | Starts the specified amount of processes (default: 1)\n" +
                        " --prepare-only=[prepare-only] | Prepares the process but does not start it (default: false)"
        ).split("\n"));
    }

    @Override
    public boolean handleCommand(@NotNull CommandSource commandSource, @NotNull String[] strings) {
        if (strings.length == 0) {
            this.describeCommandToSender(commandSource);
            return true;
        }

        ProcessGroup base = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(strings[0]);
        if (base == null) {
            commandSource.sendMessage(LanguageManager.get("command-launch-start-not-possible-group-not-exists", strings[0]));
            return true;
        }

        ProcessConfigurationBuilder builder = ProcessConfigurationBuilder.newBuilder(base);
        Properties properties = StringUtil.calcProperties(strings, 1);

        boolean prepareOnly = false;
        int amount = 1;

        if (properties.containsKey("template")) {
            Template baseTemplate = Streams.filter(base.getTemplates(), e -> e.getName().equals(properties.getProperty("template")));
            if (baseTemplate == null) {
                commandSource.sendMessage(LanguageManager.get("command-launch-template-not-exists", properties.getProperty("template"), base.getName()));
                return true;
            }

            builder.template(baseTemplate);
        }

        if (properties.containsKey("unique-id")) {
            UUID uniqueID = CommonHelper.tryParse(properties.getProperty("unique-id"));
            if (uniqueID == null) {
                commandSource.sendMessage(LanguageManager.get("command-unique-id-failed", properties.getProperty("unique-id")));
                return true;
            }

            builder.uniqueId(uniqueID);
        }

        if (properties.containsKey("display-name")) {
            builder.displayName(properties.getProperty("display-name"));
        }

        if (properties.containsKey("max-memory")) {
            Integer maxMemory = CommonHelper.fromString(properties.getProperty("max-memory"));
            if (maxMemory == null || maxMemory <= 100) {
                commandSource.sendMessage(LanguageManager.get("command-integer-failed", 100, properties.getProperty("max-memory")));
                return true;
            }

            builder.maxMemory(maxMemory);
        }

        if (properties.containsKey("port")) {
            Integer port = CommonHelper.fromString(properties.getProperty("port"));
            if (port == null || port <= 0) {
                commandSource.sendMessage(LanguageManager.get("command-integer-failed", 0, properties.getProperty("port")));
                return true;
            }

            builder.port(port);
        }

        if (properties.containsKey("id")) {
            Integer id = CommonHelper.fromString(properties.getProperty("id"));
            if (id == null || id <= 0) {
                commandSource.sendMessage(LanguageManager.get("command-integer-failed", 0, properties.getProperty("id")));
                return true;
            }

            builder.id(id);
        }

        if (properties.containsKey("max-players")) {
            Integer maxPlayers = CommonHelper.fromString(properties.getProperty("max-players"));
            if (maxPlayers == null || maxPlayers <= 0) {
                commandSource.sendMessage(LanguageManager.get("command-integer-failed", 0, properties.getProperty("max-players")));
                return true;
            }

            builder.maxPlayers(maxPlayers);
        }

        if (properties.containsKey("inclusions")) {
            builder.inclusions(parseInclusions(properties.getProperty("inclusions")));
        }

        if (properties.containsKey("prepare-only")) {
            Boolean prepare = CommonHelper.booleanFromString(properties.getProperty("prepare-only"));
            if (prepare == null) {
                commandSource.sendMessage(LanguageManager.get("command-required-boolean", properties.getProperty("prepare-only")));
                return true;
            }

            prepareOnly = prepare;
        }

        if (properties.containsKey("amount")) {
            Integer amountToStart = CommonHelper.fromString(properties.getProperty("amount"));
            if (amountToStart == null || amountToStart <= 0) {
                commandSource.sendMessage(LanguageManager.get("command-integer-failed", 0, properties.getProperty("amount")));
                return true;
            }

            amount = amountToStart;
        }

        if (prepareOnly) {
            for (int i = 1; i <= amount; i++) {
                ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().prepareProcessAsync(builder.build()).onComplete(info -> {
                });
            }
            commandSource.sendMessage(LanguageManager.get("command-launch-prepared-processes", amount, base.getName()));
        } else {
            for (int i = 1; i <= amount; i++) {
                ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().startProcessAsync(builder.build()).onComplete(info -> {
                });
            }
            commandSource.sendMessage(LanguageManager.get("command-launch-started-processes", amount, base.getName()));
        }

        return true;
    }
}
